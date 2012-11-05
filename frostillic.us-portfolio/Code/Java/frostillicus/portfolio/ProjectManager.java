package frostillicus.portfolio;

import java.util.*;
import java.io.Serializable;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import lotus.domino.*;

public class ProjectManager implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_THUMBNAIL = "/tango/package-x-generic.svg";
	public static final String DEFAULT_IMAGE = "/icons/vwicn999.gif";

	private List<Project> projects;

	@SuppressWarnings("unchecked")
	public Collection<Project> getProjects() throws NotesException {
		if(this.projects == null) {
			this.projects = new ArrayList<Project>();

			Database database = ExtLibUtil.getCurrentDatabase();
			View projectsView = database.getView("Projects");
			projectsView.setAutoUpdate(false);

			Document projectDoc = projectsView.getFirstDocument();
			while(projectDoc != null) {
				Project project = new Project();
				project.setShortName(projectDoc.getItemValueString("ShortName"));
				project.setName(projectDoc.getItemValueString("Name"));
				project.setDescription(projectDoc.getItemValueString("Description"));
				project.setUrl(projectDoc.getItemValueString("URL"));

				if(projectDoc.hasItem("Thumbnail")) {
					RichTextItem thumbnailItem = (RichTextItem)projectDoc.getFirstItem("Thumbnail");
					List<EmbeddedObject> objects = thumbnailItem.getEmbeddedObjects();
					if(objects.size() > 0) {
						EmbeddedObject attachment = objects.get(0);
						project.setThumbnailImage("/0/" + projectDoc.getUniversalID() + "/$FILE/" + attachment.getSource());
					} else {
						project.setThumbnailImage(DEFAULT_THUMBNAIL);
					}
					thumbnailItem.recycle();
				} else {
					project.setThumbnailImage(DEFAULT_THUMBNAIL);
				}

				project.setImage(DEFAULT_IMAGE);

				this.projects.add(project);

				Document tempDoc = projectDoc;
				projectDoc = projectsView.getNextDocument(projectDoc);
				tempDoc.recycle();
			}

			projectsView.recycle();

		}
		return this.projects;
	}

}
