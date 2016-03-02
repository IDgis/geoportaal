package nl.idgis.geoportaal.conversie;

public class MetadataRow {

	private String uuid;
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
	private String dateSourceCreation;
	private String dateSourcePublication;
	private String dateSourceRevision;
	private String dateSourceValidFrom;
	private String dateSourceValidUntil;
	private String supplier;
	private String status;
	private String published;
	private String lastRevisionUser;
	private String lastRevisionDate;

	public static MetadataRow parseMetadataDocument(MetadataDocument d) {
		MetadataRow row = new MetadataRow();

		// TODO: set values from MetadataDocument to MetadataRow

		return row;
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

	public String getDateSourceCreation() {
		return dateSourceCreation;
	}

	public void setDateSourceCreation(String dateSourceCreation) {
		this.dateSourceCreation = dateSourceCreation;
	}

	public String getDateSourcePublication() {
		return dateSourcePublication;
	}

	public void setDateSourcePublication(String dateSourcePublication) {
		this.dateSourcePublication = dateSourcePublication;
	}

	public String getDateSourceRevision() {
		return dateSourceRevision;
	}

	public void setDateSourceRevision(String dateSourceRevision) {
		this.dateSourceRevision = dateSourceRevision;
	}

	public String getDateSourceValidFrom() {
		return dateSourceValidFrom;
	}

	public void setDateSourceValidFrom(String dateSourceValidFrom) {
		this.dateSourceValidFrom = dateSourceValidFrom;
	}

	public String getDateSourceValidUntil() {
		return dateSourceValidUntil;
	}

	public void setDateSourceValidUntil(String dateSourceValidUntil) {
		this.dateSourceValidUntil = dateSourceValidUntil;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	public String getLastRevisionUser() {
		return lastRevisionUser;
	}

	public void setLastRevisionUser(String lastRevisionUser) {
		this.lastRevisionUser = lastRevisionUser;
	}

	public String getLastRevisionDate() {
		return lastRevisionDate;
	}

	public void setLastRevisionDate(String lastRevisionDate) {
		this.lastRevisionDate = lastRevisionDate;
	}

}
