package frostillicus.controller;

import javax.faces.context.FacesContext;
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

	protected DominoDocument getDominoDocument() {
		return (DominoDocument)ExtLibUtil.resolveVariable(FacesContext.getCurrentInstance(), documentName);
	}
	public DataObject getDoc() { return this.getDominoDocument(); }
}
