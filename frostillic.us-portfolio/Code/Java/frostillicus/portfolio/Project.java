package frostillicus.portfolio;

import java.io.Serializable;

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	private String shortName;
	private String name;
	private String description;
	private String url;
	private String thumbnailImage;
	private String image;

	public String getShortName() { return shortName; }
	public void setShortName(String shortName) { this.shortName = shortName; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }

	public String getThumbnailImage() { return thumbnailImage; }
	public void setThumbnailImage(String thumbnailImage) { this.thumbnailImage = thumbnailImage; }

	public String getImage() { return image; }
	public void setImage(String image) { this.image = image; }
}
