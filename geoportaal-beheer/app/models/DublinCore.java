package models;

public class DublinCore {
	
	private String id;
	private String location;
	private String fileId;
	private String title;
	private String description;
	private String attachment;
	private String type;
	private String creator;
	private String rights;
	private String useLimitation;;
	private String format;;
	private String source;
	private String dateCreation;
	private String datePublication;
	private String dateRevision;
	private String dateValidFrom;
	private String dateValidUntil;
	
	public DublinCore(String id, String location, String fileId, String title, String description, String attachment,
			String type, String creator, String rights, String useLimitation, String format, String source,
			String dateCreation, String datePublication, String dateRevision, String dateValidFrom,
			String dateValidUntil) {
		super();
		this.id = id;
		this.location = location;
		this.fileId = fileId;
		this.title = title;
		this.description = description;
		this.attachment = attachment;
		this.type = type;
		this.creator = creator;
		this.rights = rights;
		this.useLimitation = useLimitation;
		this.format = format;
		this.source = source;
		this.dateCreation = dateCreation;
		this.datePublication = datePublication;
		this.dateRevision = dateRevision;
		this.dateValidFrom = dateValidFrom;
		this.dateValidUntil = dateValidUntil;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getUseLimitation() {
		return useLimitation;
	}
	public void setUseLimitation(String useLimitation) {
		this.useLimitation = useLimitation;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	public String getDatePublication() {
		return datePublication;
	}
	public void setDatePublication(String datePublication) {
		this.datePublication = datePublication;
	}
	public String getDateRevision() {
		return dateRevision;
	}
	public void setDateRevision(String dateRevision) {
		this.dateRevision = dateRevision;
	}
	public String getDateValidFrom() {
		return dateValidFrom;
	}
	public void setDateValidFrom(String dateValidFrom) {
		this.dateValidFrom = dateValidFrom;
	}
	public String getDateValidUntil() {
		return dateValidUntil;
	}
	public void setDateValidUntil(String dateValidUntil) {
		this.dateValidUntil = dateValidUntil;
	}
}
