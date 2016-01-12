package models;

import java.util.Date;
import java.util.List;

public class DublinCore {
	
	private String location;
	private String fileId;
	private String title;
	private String description;
	private String typeInformation;
	private String creator;
	private String creatorOther;
	private String rights;
	private String useLimitation;
	private String mdFormat;
	private String source;
	private Date dateSourceCreation;
	private Date dateSourcePublication;
	private Date dateSourceRevision;
	private Date dateSourceValidFrom;
	private Date dateSourceValidUntil;
	private List<String> subject;
	
	public DublinCore() {
		
	}
	
	public DublinCore(String location, String fileId, String title, String description, String typeInformation, String creator, 
			String creatorOther, String rights, String useLimitation, String mdFormat, String source, Date dateSourceCreation, Date dateSourcePublication, 
			Date dateSourceRevision, Date dateSourceValidFrom, Date dateSourceValidUntil, List<String> subject) {
		super();
		this.location = location;
		this.fileId = fileId;
		this.title = title;
		this.description = description;
		this.typeInformation = typeInformation;
		this.creator = creator;
		this.creatorOther = creatorOther;
		this.rights = rights;
		this.useLimitation = useLimitation;
		this.mdFormat = mdFormat;
		this.source = source;
		this.dateSourceCreation = dateSourceCreation;
		this.dateSourcePublication = dateSourcePublication;
		this.dateSourceRevision = dateSourceRevision;
		this.dateSourceValidFrom = dateSourceValidFrom;
		this.dateSourceValidUntil = dateSourceValidUntil;
		this.subject = subject;
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

	public Date getDateSourcePublication() {
		return dateSourcePublication;
	}

	public void setDateSourcePublication(Date dateSourcePublication) {
		this.dateSourcePublication = dateSourcePublication;
	}

	public Date getDateSourceRevision() {
		return dateSourceRevision;
	}

	public void setDateSourceRevision(Date dateSourceRevision) {
		this.dateSourceRevision = dateSourceRevision;
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
}
