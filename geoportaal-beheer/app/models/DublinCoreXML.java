package models;

import java.util.Date;
import java.util.List;

/**
 * The model class to store information about the DublinCore metadata record to generate the XML
 * 
 * @author Sandro
 *
 */
public class DublinCoreXML {
	
	private String identifier;
	private String title;
	private String description;
	private String location;
	private String fileId;
	private List<String> attachments;
	private String type;
	private String typeResearch;
	private String creator;
	private String publisher;
	private String contributor;
	private String rights;
	private String useLimitation;
	private String format;
	private String source;
	private Date date;
	private Date dateIssued;
	private Date dateValidStart;
	private Date dateValidEnd;
	private List<String> subjects;
	private String language;
	private String lowerCorner;
	private String upperCorner;
	
	public DublinCoreXML() {
	}
	
	/**
	 * The constructor of the DublinCore class to generate the XML
	 * 
	 * @param identifier the identifier XML element
	 * @param title the title XML element
	 * @param description the description XML element
	 * @param location the location XML element
	 * @param fileId the number of the metadata
	 * @param attachments the list of attachments
	 * @param type the type XML element
	 * @param typeResearch the research type
	 * @param creator the creator XML element
	 * @param publisher the publisher XML element
	 * @param contributor the contributor XML element
	 * @param rights the rights XML element
	 * @param useLimitation the use limitation XML element
	 * @param format the format XML element
	 * @param source the source XML element
	 * @param date the date the metadata was created
	 * @param dateIssued the date the metadata was published
	 * @param dateValidStart the start date when the data was valid
	 * @param dateValidEnd the end date when the data was valid
	 * @param subjects the list of subjects
	 * @param language the language XML element
	 * @param lowerCorner the lower corner of the coordinates
	 * @param upperCorner the upper corner of the coordinates
	 */
	public DublinCoreXML(String identifier, String title, String description, String location, String fileId, List<String> attachments, String type, 
		String typeResearch, String creator, String publisher, String contributor, String rights, String useLimitation, String format, String source, Date date,
		Date dateIssued, Date dateValidStart, Date dateValidEnd, List<String> subjects, String language, String lowerCorner, String upperCorner) {
		super();
		this.identifier = identifier;
		this.title = title;
		this.description = description;
		this.location = location;
		this.fileId = fileId;
		this.attachments = attachments;
		this.type = type;
		this.typeResearch = typeResearch;
		this.creator = creator;
		this.publisher = publisher;
		this.contributor = contributor;
		this.rights = rights;
		this.useLimitation = useLimitation;
		this.format = format;
		this.source = source;
		this.date = date;
		this.dateIssued = dateIssued;
		this.dateValidStart = dateValidStart;
		this.dateValidEnd = dateValidEnd;
		this.subjects = subjects;
		this.language = language;
		this.lowerCorner = lowerCorner;
		this.upperCorner = upperCorner;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeResearch() {
		return typeResearch;
	}

	public void setTypeResearch(String typeResearch) {
		this.typeResearch = typeResearch;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDateIssued() {
		return dateIssued;
	}

	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	public Date getDateValidStart() {
		return dateValidStart;
	}

	public void setDateValidStart(Date dateValidStart) {
		this.dateValidStart = dateValidStart;
	}

	public Date getDateValidEnd() {
		return dateValidEnd;
	}

	public void setDateValidEnd(Date dateValidEnd) {
		this.dateValidEnd = dateValidEnd;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLowerCorner() {
		return lowerCorner;
	}

	public void setLowerCorner(String lowerCorner) {
		this.lowerCorner = lowerCorner;
	}

	public String getUpperCorner() {
		return upperCorner;
	}

	public void setUpperCorner(String upperCorner) {
		this.upperCorner = upperCorner;
	}
}
