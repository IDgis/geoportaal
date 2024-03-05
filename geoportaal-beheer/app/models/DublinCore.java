package models;

import java.util.Date;
import java.util.List;

/**
 * The model class to store information about the DublinCore metadata record
 * 
 * @author Sandro
 *
 */
public class DublinCore {
	
	private String location;
	private String fileId;
	private String title;
	private String description;
	private String typeInformation;
	private String typeResearch;
	private String creator;
	private String creatorOther;
	private String rights;
	private String useLimitation;
	private String mdFormat;
	private String source;
	private Date dateSourceCreation;
	private Date dateSourceValidFrom;
	private Date dateSourceValidUntil;
	private List<String> subject;
	private List<String> theme;
	private List<String> wooTheme;
	private List<String> deletedAttachment;
	
	public DublinCore() {
		
	}
	
	/**
	 * The constructor of the DublinCore metadata class
	 * 
	 * @param location the location field
	 * @param fileId the file id field
	 * @param title the title field
	 * @param description the description field
	 * @param typeInformation the type information option selected
	 * @param typeResearch the type research option selected
	 * @param creator the creator option selected
	 * @param creatorOther the creator other field (only applicable if creator option selected is other)
	 * @param rights the rights option selected
	 * @param useLimitation the use limitation option selected
	 * @param mdFormat the format option selected
	 * @param source the source field
	 * @param dateSourceCreation the date when the data was created
	 * @param dateSourceRevision the date when the data was last changed
	 * @param dateSourceValidFrom the start date when the data was valid
	 * @param dateSourceValidUntil the end date when the data was valid
	 * @param subject the subjects checked
	 * @param theme the themes checked
	 * @param deletedAttachment the UUID's of the attachments about to be deleted
	 */
	public DublinCore(String location, String fileId, String title, String description, String typeInformation, String typeResearch, String creator, 
			String creatorOther, String rights, String useLimitation, String mdFormat, String source, Date dateSourceCreation, 
			Date dateSourceValidFrom, Date dateSourceValidUntil, List<String> subject, List<String> theme,
			List<String> wooTheme, List<String> deletedAttachment) {
		super();
		this.location = location;
		this.fileId = fileId;
		this.title = title;
		this.description = description;
		this.typeInformation = typeInformation;
		this.typeResearch = typeResearch;
		this.creator = creator;
		this.creatorOther = creatorOther;
		this.rights = rights;
		this.useLimitation = useLimitation;
		this.mdFormat = mdFormat;
		this.source = source;
		this.dateSourceCreation = dateSourceCreation;
		this.dateSourceValidFrom = dateSourceValidFrom;
		this.dateSourceValidUntil = dateSourceValidUntil;
		this.subject = subject;
		this.theme = theme;
		this.wooTheme = wooTheme;
		this.deletedAttachment = deletedAttachment;
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
	public String getTypeInformation() {
		return typeInformation;
	}

	public void setTypeInformation(String typeInformation) {
		this.typeInformation = typeInformation;
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
	public String getCreatorOther() {
		return creatorOther;
	}

	public void setCreatorOther(String creatorOther) {
		this.creatorOther = creatorOther;
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
	public String getMdFormat() {
		return mdFormat;
	}
	public void setMdFormat(String mdFormat) {
		this.mdFormat = mdFormat;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public Date getDateSourceCreation() {
		return dateSourceCreation;
	}

	public void setDateSourceCreation(Date dateSourceCreation) {
		this.dateSourceCreation = dateSourceCreation;
	}

	public Date getDateSourceValidFrom() {
		return dateSourceValidFrom;
	}

	public void setDateSourceValidFrom(Date dateSourceValidFrom) {
		this.dateSourceValidFrom = dateSourceValidFrom;
	}

	public Date getDateSourceValidUntil() {
		return dateSourceValidUntil;
	}

	public void setDateSourceValidUntil(Date dateSourceValidUntil) {
		this.dateSourceValidUntil = dateSourceValidUntil;
	}

	public List<String> getSubject() {
		return subject;
	}

	public void setSubject(List<String> subject) {
		this.subject = subject;
	}
	
	public List<String> getTheme() {
		return theme;
	}
	
	public void setWooTheme(List<String> wooTheme) {
		this.wooTheme = wooTheme;
	}
	
	public List<String> getWooTheme() {
		return wooTheme;
	}
	
	public void setTheme(List<String> theme) {
		this.theme = theme;
	}

	public List<String> getDeletedAttachment() {
		return deletedAttachment;
	}

	public void setDeletedAttachment(List<String> deletedAttachment) {
		this.deletedAttachment = deletedAttachment;
	}
}
