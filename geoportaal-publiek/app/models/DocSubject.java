package models;

import java.sql.Timestamp;
import java.util.List;

public class DocSubject {
	private String uuid;
	private String title;
	private Timestamp date;
	private String creator;
	private String description;
	private String thumbnail;
	private List<String> subjects;
	private String metadataUrl;
	private String metadataType;
	private Boolean downloadable;
	private String spatialSchema;
	private Boolean published;
	private String typeService;
	
	public DocSubject(String uuid, String title, Timestamp date, String creator, String description, String thumbnail,
			List<String> subjects, String metadataUrl, String metadataType, Boolean downloadable, String spatialSchema,
			Boolean published, String typeService) {
		super();
		this.uuid = uuid;
		this.title = title;
		this.date = date;
		this.creator = creator;
		this.description = description;
		this.thumbnail = thumbnail;
		this.subjects = subjects;
		this.metadataUrl = metadataUrl;
		this.metadataType = metadataType;
		this.downloadable = downloadable;
		this.spatialSchema = spatialSchema;
		this.published = published;
		this.typeService = typeService;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getMetadataUrl() {
		return metadataUrl;
	}

	public void setMetadataUrl(String metadataUrl) {
		this.metadataUrl = metadataUrl;
	}

	public String getMetadataType() {
		return metadataType;
	}

	public void setMetadataType(String metadataType) {
		this.metadataType = metadataType;
	}

	public Boolean getDownloadable() {
		return downloadable;
	}

	public void setDownloadable(Boolean downloadable) {
		this.downloadable = downloadable;
	}

	public String getSpatialSchema() {
		return spatialSchema;
	}

	public void setSpatialSchema(String spatialSchema) {
		this.spatialSchema = spatialSchema;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getTypeService() {
		return typeService;
	}

	public void setTypeService(String typeService) {
		this.typeService = typeService;
	}
	
}