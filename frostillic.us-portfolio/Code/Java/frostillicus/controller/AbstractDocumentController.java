package frostillicus.controller;


import javax.faces.context.FacesContext;

import lotus.domino.NotesException;
import frostillicus.portfolio.Util;

import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.DataObject;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

@SuppressWarnings("serial")
public abstract class AbstractDocumentController implements XPageController {
	private final String documentName;

	public AbstractDocumentController(String documentName) {
		this.documentName = documentName;
	}

	public void afterPageLoad() throws Exception { }

	public boolean isEditable() {
		return this.getDominoDocument().isEditable();
	}
	public String getDocumentId() {
		return this.getDominoDocument().getDocumentId();
	}

	public String save() throws NotesException {
		DominoDocument dominoDocument = this.getDominoDocument();

		boolean isNewNote = dominoDocument.isNewNote();
		dominoDocument.save();

		Util.addConfirmationMessage(isNewNote ? dominoDocument.getValue("Form") + " submitted." : dominoDocument.getValue("Form") + " updated.");
		return "xsp-success";
	}
	public String cancel() {
		return "xsp-success";
	}

	protected DominoDocument getDominoDocument() {
		return (DominoDocument)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), documentName);
	}
	public DataObject getDoc() { return this.getDominoDocument(); }
}
