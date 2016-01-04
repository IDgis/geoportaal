package models;

import java.util.Date;
import java.util.List;

public class DublinCore {
	
	private String uuid;
	private String location;
	private String fileId;
	private String title;
	private String description;
	private String typeInfo;
	private String creator;
	private String rights;
	private String useLimitation;
	private String format;
	private String source;
	private Date dateSourceCreation;
	private Date dateSourcePublication;
	private Date dateSourceRevision;
	private Date dateSourceValidFrom;
	private Date dateSourceValidUntil;
	private List<String> subject;
	
	public DublinCore() {
		
	}
	
	public DublinCore(String uuid, String location, String fileId, String title, String description, String typeInfo, String creator, 
			String rights, String useLimitation, String format, String source, Date dateSourceCreation, Date dateSourcePublication, 
			Date dateSourceRevision, Date dateSourceValidFrom, Date dateSourceValidUntil, List<String> subject) {
		super();
		this.uuid = uuid;
		this.location = location;
		this.fileId = fileId;
		this.title = title;
		this.description = description;
		this.typeInfo = typeInfo;
		this.creator = creator;
		this.rights = rights;
		this.useLimitation = useLimitation;
		this.format = format;
		this.source = source;
		this.dateSourceCreation = dateSourceCreation;
		this.dateSourcePublication = dateSourcePublication;
		this.dateSourceRevision = dateSourceRevision;
		this.dateSourceValidFrom = dateSourceValidFrom;
		this.dateSourceValidUntil = dateSourceValidUntil;
		this.subject = subject;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public String getTypeInfo() {
		return typeInfo;
	}

	public void setTypeInfo(String typeInfo) {
		this.typeInfo = typeInfo;
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
