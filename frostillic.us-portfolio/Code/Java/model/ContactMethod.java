package model;

import java.util.*;

import lotus.domino.*;
import frostillicus.model.AbstractDominoModel;
import frostillicus.model.DominoColumnInfo;

public class ContactMethod extends AbstractDominoModel {
	private static final long serialVersionUID = 4626540812586218529L;

	public ContactMethod(final Database database) throws NotesException {
		super(database);
		setValue("Form", "Contact Method");
	}

	public ContactMethod(final ViewEntry entry, final List<DominoColumnInfo> columnInfo) throws NotesException { super(entry, columnInfo); }
	public ContactMethod(final Document doc) throws NotesException { super(doc); }

	@Override
	protected Collection<String> summaryFields() {
		return Arrays.asList(new String[] { "index", "name", "url", "text" });
	}

}
