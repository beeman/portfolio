package frostillicus.controller;

import java.util.*;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.xml.xpath.XPathExpressionException;

import lotus.domino.*;


import com.ibm.xsp.component.UISelectItemEx;
import com.ibm.xsp.component.UIViewRootEx2;
import com.ibm.xsp.component.xp.*;
import com.ibm.xsp.convert.DateTimeConverter;
import com.ibm.xsp.extlib.component.data.UIFormLayoutRow;
import com.ibm.xsp.extlib.component.data.UIFormTable;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import com.raidomatic.xml.*;

import frostillicus.portfolio.Util;

public class DocumentController implements XPageController {
	private static final long serialVersionUID = -2874896616467576061L;

	private final String documentName;
	private final String formTableId;
	private XMLDocument xml = new XMLDocument();
	private int fieldIndex = 0;	// Use this to make some unique IDs for child controls

	public DocumentController(String documentName, String formTableId) {
		this.documentName = documentName;
		this.formTableId = formTableId;
	}

	public void afterPageLoad() throws Exception {
		//this.setPageTitle();
		this.generateForm();
	}

	public void setPageTitle() {
		((UIViewRootEx2)FacesContext.getCurrentInstance().getViewRoot()).setPageTitle((String)this.getDominoDocument().getValue("Form"));
	}

	public void generateForm() throws Exception {
		// Set a nice title, if applicable
		Object docTitle = this.getDominoDocument().getValue("$$Title");
		if(docTitle instanceof String && ((String)docTitle).length() > 0) {
			this.getFormTable().setFormTitle(this.getDominoDocument().getValue("Form") + " - " + (String)docTitle);
		}

		Database database = ExtLibUtil.getCurrentDatabase();
		Form form = database.getForm((String)this.getDominoDocument().getValue("Form"));
		String formUNID = Util.strRightBack(Util.strLeftBack(form.getURL(), "?"), "/");
		Document formDoc = database.getDocumentByUNID(formUNID);
		String dxl = formDoc.generateXML();
		this.xml.loadString(dxl);

		for(XMLNode fieldNode : this.xml.selectNodes("//field")) {
			if(!fieldNode.getAttribute("kind").contains("computed")) {
				this.addField(fieldNode);
			}
		}
	}

	public String save() throws NotesException {
		this.getDominoDocument().save();
		Util.addConfirmationMessage("Document saved.");
		return "xsp-success";
	}
	public String cancel() {
		return "xsp-success";
	}


	@SuppressWarnings("unchecked")
	private void addField(XMLNode fieldNode) throws XPathExpressionException, NotesException {
		String name = fieldNode.getAttribute("name");
		String fieldLabel = fieldNode.getAttribute("htmltitle");
		if(fieldLabel.length() == 0) { fieldLabel = name; }

		UIFormTable formTable = this.getFormTable();

		UIFormLayoutRow formRow = new UIFormLayoutRow();
		formRow.setLabel(fieldLabel);
		formTable.getChildren().add(formRow);

		UIInput field;
		String fieldType = fieldNode.getAttribute("type");
		if(fieldType.equals("keyword")) {
			XMLNode keywordsNode = fieldNode.selectSingleNode("keywords");
			if(keywordsNode.getAttribute("ui").equals("radiobutton")) {
				field = new XspSelectOneRadio();
			} else if(keywordsNode.getAttribute("ui").equals("checkbox")) {
				field = new XspSelectManyCheckbox();
			} else {
				field = new XspSelectOneMenu();
			}
			XMLNode textlist = keywordsNode.selectSingleNode("textlist");
			if(textlist != null) {
				// Then it's just a normal list
				for(XMLNode text : textlist.selectNodes("text")) {
					UISelectItemEx selectItem = new UISelectItemEx();
					String choice = text.getTextContent();
					String label, value;
					if(choice.contains("|")) {
						value = Util.strRight(choice, "|");
						label = Util.strLeft(choice, "|");
					} else {
						value = choice;
						label = choice;
					}
					selectItem.setItemLabel(label);
					selectItem.setItemValue(value);

					field.getChildren().add(selectItem);
				}
			} else {
				// Then we'll assume it's a formula
				XMLNode formula = keywordsNode.selectSingleNode("formula");
				List<Object> result = (List<Object>)ExtLibUtil.getCurrentSession().evaluate(formula.getTextContent(), this.getDominoDocument().getDocument(true));
				// I probably shouldn't duplicate this code.
				for(Object choiceObj : result) {
					String choice = choiceObj.toString();
					UISelectItemEx selectItem = new UISelectItemEx();
					String label, value;
					if(choice.contains("|")) {
						value = Util.strRight(choice, "|");
						label = Util.strLeft(choice, "|");
					} else {
						value = choice;
						label = choice;
					}
					selectItem.setItemLabel(label);
					selectItem.setItemValue(value);

					field.getChildren().add(selectItem);

					// Sigh...
					if(choiceObj instanceof Base) {
						((Base)choiceObj).recycle();
					}
				}
			}
		} else if(fieldType.equals("richtext")) {
			field = new XspInputRichText();
		} else if(fieldType.equals("datetime")) {
			field = new XspInputText();

			// Now find the format and set an appropriate converter
			XMLNode datetimeformat = fieldNode.selectSingleNode("datetimeformat");
			DateTimeConverter converter = new DateTimeConverter();
			converter.setType(datetimeformat.getAttribute("show").equals("datetime") ? "both" : datetimeformat.getAttribute("show"));
			field.setConverter(converter);
			System.out.println("Set converter type to " + (datetimeformat.getAttribute("show").equals("datetime") ? "both" : datetimeformat.getAttribute("show")));

			XspDateTimeHelper helper = new XspDateTimeHelper();
			helper.setId("dthelper" + this.fieldIndex);
			field.getChildren().add(helper);
		} else {
			field = new XspInputText();
		}
		field.setId("generatedfield" + this.fieldIndex);



		String valueExpr = "#{" + this.documentName + "." + name + "}";

		ValueBinding value = FacesContext.getCurrentInstance().getApplication().createValueBinding(valueExpr);
		field.setValueBinding("value", value);
		formRow.getChildren().add(field);

		this.fieldIndex++;
	}

	private DominoDocument getDominoDocument() {
		return (DominoDocument)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), documentName);
	}
	private UIFormTable getFormTable() {
		return (UIFormTable)ExtLibUtil.getComponentFor(FacesContext.getCurrentInstance().getViewRoot(), formTableId);
	}
}
