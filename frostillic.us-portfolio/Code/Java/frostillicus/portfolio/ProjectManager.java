package frostillicus.portfolio;

import java.util.*;
import java.io.Serializable;
import org.openntf.domino.*;
import org.openntf.domino.utils.XSPUtil;

public class ProjectManager implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_THUMBNAIL = "/tango/package-x-generic.svg";
	public static final String DEFAULT_IMAGE = "";

	private Set<Project> projects;
	private Map<String, Project> projectMap;
	private Date lastUpdated = null;

	@SuppressWarnings("unchecked")
	public Collection<Project> getProjects() {

		boolean needsReset = this.projects == null;
		if(!needsReset) {
			// Check to see if the DB has changed. Getting the last modified date is surprisingly cheap
			Database database = XSPUtil.getCurrentDatabase();
			DateTime dt = database.getLastModified();
			Date databaseModified = dt.toJavaDate();
			if(databaseModified.after(this.lastUpdated)) {
				needsReset = true;
			}
		}

		if(needsReset) {
			this.projects = new TreeSet<Project>();
			this.projectMap = new HashMap<String, Project>();

			Database database = XSPUtil.getCurrentDatabase();
			View projectsView = database.getView("Projects");
			projectsView.setAutoUpdate(false);

			Document projectDoc = projectsView.getFirstDocument();
			while(projectDoc != null) {
				Project project = new Project();
				project.setShortName(projectDoc.getItemValueString("ShortName"));
				project.setName(projectDoc.getItemValueString("Name"));
				project.setDescription(projectDoc.getItemValueString("Description"));
				project.setUrl((List<String>)projectDoc.getItemValue("URL"));

				if(projectDoc.hasItem("Thumbnail")) {
					RichTextItem thumbnailItem = (RichTextItem)projectDoc.getFirstItem("Thumbnail");
					List<EmbeddedObject> objects = thumbnailItem.getEmbeddedObjects();
					if(objects.size() > 0) {
						EmbeddedObject attachment = objects.get(0);
						project.setThumbnailImage("/0/" + projectDoc.getUniversalID() + "/$FILE/" + attachment.getSource());
					} else {
						project.setThumbnailImage(DEFAULT_THUMBNAIL);
					}
				} else {
					project.setThumbnailImage(DEFAULT_THUMBNAIL);
				}

				if(projectDoc.hasItem("Image")) {
					RichTextItem thumbnailItem = (RichTextItem)projectDoc.getFirstItem("Image");
					List<EmbeddedObject> objects = thumbnailItem.getEmbeddedObjects();
					if(objects.size() > 0) {
						EmbeddedObject attachment = objects.get(0);
						project.setImage("/0/" + projectDoc.getUniversalID() + "/$FILE/" + attachment.getSource());
					} else {
						project.setImage(DEFAULT_IMAGE);
					}
				} else {
					project.setImage(DEFAULT_IMAGE);
				}

				this.projects.add(project);
				this.projectMap.put(projectDoc.getUniversalID(), project);

				Document tempDoc = projectDoc;
				projectDoc = projectsView.getNextDocument(projectDoc);
			}


			this.lastUpdated = new Date();
		}
		return this.projects;
	}

	public Map<String, Project> getProjectsById() {
		this.getProjects();
		return this.projectMap;
	}

}
