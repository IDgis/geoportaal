package models;

import java.sql.Timestamp;
import java.util.List;

public class DocSubject {
	private String title;
	private Timestamp date;
	private String creator;
	private String description;
	private String thumbnail;
	private List<String> subjects;
	private String metadataType;
	
	public DocSubject(String title, Timestamp date, String creator, String description, String thumbnail,
			List<String> subjects, String metadataType) {
		super();
		this.title = title;
		this.date = date;
		this.creator = creator;
		this.description = description;
		this.thumbnail = thumbnail;
		this.subjects = subjects;
		this.metadataType = metadataType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public String getMetadataType() {
		return metadataType;
	}

	public void setMetadataType(String metadataType) {
		this.metadataType = metadataType;
	}
	
}
