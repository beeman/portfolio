package model;

import java.util.*;

import lotus.domino.*;
import frostillicus.model.*;

public class ContactMethodManager extends AbstractDominoManager<ContactMethod> {
	private static final long serialVersionUID = 5115742408457876015L;

	@Override
	protected ContactMethod create() throws NotesException {
		return new ContactMethod(getDatabase());
	}

	@Override
	protected ContactMethod createFromDocument(final Document doc) throws NotesException {
		return new ContactMethod(doc);
	}

	@Override
	protected AbstractDominoList<ContactMethod> getNamedCollection(final String name, final String category) throws NotesException {
		return new ContactMethodList(getDatabase(), "Contact Methods", category);
	}

}

class ContactMethodList extends AbstractDominoList<ContactMethod> {
	private static final long serialVersionUID = 6863827355183568052L;

	public ContactMethodList(final Database database, final String viewName, final String category) throws NotesException {
		super(database, viewName, category);
	}

	@Override
	protected ContactMethod createFromViewEntry(final ViewEntry entry, final List<DominoColumnInfo> columnInfo) throws NotesException {
		return new ContactMethod(entry, columnInfo);
	}
}