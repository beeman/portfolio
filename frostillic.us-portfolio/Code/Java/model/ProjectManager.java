package model;

import java.util.List;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.ViewEntry;
import frostillicus.model.AbstractDominoList;
import frostillicus.model.AbstractDominoManager;
import frostillicus.model.DominoColumnInfo;

public class ProjectManager extends AbstractDominoManager<Project> {
	private static final long serialVersionUID = 2215410499791879238L;

	@Override
	protected Project create() throws NotesException {
		return new Project(getDatabase());
	}

	@Override
	protected Project createFromDocument(final Document doc) throws NotesException {
		return new Project(doc);
	}

	@Override
	protected AbstractDominoList<Project> getNamedCollection(final String name, final String category) throws NotesException {
		return new ProjectList(getDatabase(), "Projects", category);
	}

}

class ProjectList extends AbstractDominoList<Project> {
	private static final long serialVersionUID = -2693312735777617313L;

	public ProjectList(final Database database, final String viewName, final String category) throws NotesException {
		super(database, viewName, category);
	}

	@Override
	protected Project createFromViewEntry(final ViewEntry entry, final List<DominoColumnInfo> columnInfo) throws NotesException {
		return new Project(entry, columnInfo);
	}
}