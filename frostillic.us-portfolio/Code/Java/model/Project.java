package model;

import java.util.*;

import lotus.domino.*;

import frostillicus.model.AbstractDominoModel;
import frostillicus.model.DominoColumnInfo;

public class Project extends AbstractDominoModel implements Comparable<Project> {
	private static final long serialVersionUID = -5925844861502270545L;
	public static final String DEFAULT_THUMBNAIL = "/tango/package-x-generic.svg";
	public static final String DEFAULT_IMAGE = "";

	private String thumbnailImage_;
	private String image_;

	protected Project(final Database database) throws NotesException {
		super(database);
		setValue("Form", "Project");
	}
	protected Project(final ViewEntry entry, final List<DominoColumnInfo> columnInfo) throws NotesException { super(entry, columnInfo); }
	protected Project(final Document doc) throws NotesException { super(doc); }

	@Override
	protected Collection<String> summaryFields() {
		return Arrays.asList(new String[] {
				"shortname", "name", "description"
		});
	}

	@SuppressWarnings("unchecked")
	public String getThumbnailImage() {
		if(thumbnailImage_ == null) {
			try {
				Document doc = document();
				if(doc.hasItem("Thumbnail")) {
					RichTextItem thumbnailItem = (RichTextItem)doc.getFirstItem("Thumbnail");
					List<EmbeddedObject> objects = thumbnailItem.getEmbeddedObjects();
					if(objects.size() > 0) {
						EmbeddedObject attachment = objects.get(0);
						thumbnailImage_ = "/0/" + doc.getUniversalID() + "/$FILE/" + attachment.getSource();
					} else {
						thumbnailImage_ = DEFAULT_THUMBNAIL;
					}
				} else {
					thumbnailImage_ = DEFAULT_THUMBNAIL;
				}
			} catch(NotesException ne) {
				throw new RuntimeException(ne);
			}
		}
		return thumbnailImage_;
	}
	@SuppressWarnings("unchecked")
	public String getImage() {
		if(image_ == null) {
			try {
				Document doc = document();
				if(doc.hasItem("Image")) {
					RichTextItem thumbnailItem = (RichTextItem)doc.getFirstItem("Image");
					List<EmbeddedObject> objects = thumbnailItem.getEmbeddedObjects();
					if(objects.size() > 0) {
						EmbeddedObject attachment = objects.get(0);
						image_ = "/0/" + doc.getUniversalID() + "/$FILE/" + attachment.getSource();
					} else {
						image_ = DEFAULT_IMAGE;
					}
				} else {
					image_ = DEFAULT_IMAGE;
				}
			} catch(NotesException ne) {
				throw new RuntimeException(ne);
			}
		}
		return image_;
	}

	public int compareTo(final Project other) {
		String name = (String)getValue("Name");
		String otherName = (String)other.getValue("Name");
		return name.compareToIgnoreCase(otherName);
	}

}
