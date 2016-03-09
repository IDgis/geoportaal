package nl.idgis.geoportaal.conversie;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class MetadataRow {

	private static final String SUPPLIER = "nienhuis";
	private static final String STATUS = "ter goedkeuring";
	private static final String LAST_REVISION_USER = "conversie";
	private static final boolean PUBLISHED = false;

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
	private Timestamp dateSourceCreation;
	private Timestamp dateSourcePublication;
	private Timestamp dateSourceValidFrom;
	private Timestamp dateSourceValidUntil;
	private String supplier;
	private String status;
	private Boolean published;
	private String lastRevisionUser;
	private Timestamp lastRevisionDate;

	public static MetadataRow parseMetadataDocument(MetadataDocument d) throws Exception {
		MetadataRow row = new MetadataRow();

		String dUUID = retrieveFirstStringOrNull(Path.UUID, d);

		if (dUUID == null || dUUID.isEmpty()) {
			dUUID = UUID.randomUUID().toString();
		}

		row.setUuid(dUUID);
		row.setLocation(retrieveFirstStringOrNull(Path.LOCATION, d));
		row.setFileId(retrieveFirstStringOrNull(Path.RELATION, d));
		row.setTitle(retrieveFirstStringOrNull(Path.TITLE, d));
		row.setDescription(retrieveFirstStringOrNull(Path.DESCRIPTION, d));
		row.setTypeInformation(retrieveFirstStringOrNull(Path.TYPE_INFORMATION, d));
		row.setCreator(retrieveFirstStringOrNull(Path.CREATOR, d));
		row.setCreatorOther(retrieveFirstStringOrNull(Path.CREATOR_OTHER, d));
		row.setRights(retrieveFirstStringOrNull(Path.RIGHTS, d));
		row.setUseLimitation(retrieveFirstStringOrNull(Path.USE_LIMITATION, d));
		row.setMdFormat(retrieveFirstStringOrNull(Path.MD_FORMAT, d));
		row.setSource(retrieveFirstStringOrNull(Path.SOURCE, d));
		row.setDateSourceCreation(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_CREATION, d)));
		row.setDateSourcePublication(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_PUBLICATION, d)));
		row.setDateSourceValidFrom(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_VALID_FROM, d)));
		row.setDateSourceValidUntil(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_VALID_UNTIL, d)));
		row.setSupplier(SUPPLIER);
		row.setStatus(STATUS);
		row.setPublished(PUBLISHED);
		row.setLastRevisionUser(LAST_REVISION_USER);
		row.setLastRevisionDate(new Timestamp(System.currentTimeMillis()));

		return row;
	}

	private static String retrieveFirstStringOrNull(Path path, MetadataDocument d) throws Exception {
		List<String> strings = d.getStrings(path.path());

		if (strings.size() == 0)
			return null;

		return strings.get(0);
	}

	private static Timestamp toTime(String timeString) throws ParseException {
		if (timeString == null)
			return null;

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return new Timestamp(format.parse(timeString).getTime());
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

	public Timestamp getDateSourceCreation() {
		return dateSourceCreation;
	}

	public void setDateSourceCreation(Timestamp dateSourceCreation) {
		this.dateSourceCreation = dateSourceCreation;
	}

	public Timestamp getDateSourcePublication() {
		return dateSourcePublication;
	}

	public void setDateSourcePublication(Timestamp dateSourcePublication) {
		this.dateSourcePublication = dateSourcePublication;
	}

	public Timestamp getDateSourceValidFrom() {
		return dateSourceValidFrom;
	}

	public void setDateSourceValidFrom(Timestamp dateSourceValidFrom) {
		this.dateSourceValidFrom = dateSourceValidFrom;
	}

	public Timestamp getDateSourceValidUntil() {
		return dateSourceValidUntil;
	}

	public void setDateSourceValidUntil(Timestamp dateSourceValidUntil) {
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

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getLastRevisionUser() {
		return lastRevisionUser;
	}

	public void setLastRevisionUser(String lastRevisionUser) {
		this.lastRevisionUser = lastRevisionUser;
	}

	public Timestamp getLastRevisionDate() {
		return lastRevisionDate;
	}

	public void setLastRevisionDate(Timestamp lastRevisionDate) {
		this.lastRevisionDate = lastRevisionDate;
	}

	@Override
	public String toString() {
		return "MetadataRow [uuid=" + uuid + ", location=" + location + ", fileId=" + fileId + ", title=" + title
				+ ", description=" + description + ", typeInformation=" + typeInformation + ", creator=" + creator
				+ ", creatorOther=" + creatorOther + ", rights=" + rights + ", useLimitation=" + useLimitation
				+ ", mdFormat=" + mdFormat + ", source=" + source + ", dateSourceCreation=" + dateSourceCreation
				+ ", dateSourcePublication=" + dateSourcePublication + ", dateSourceValidFrom=" + dateSourceValidFrom
				+ ", dateSourceValidUntil=" + dateSourceValidUntil + ", supplier=" + supplier + ", status=" + status
				+ ", published=" + published + ", lastRevisionUser=" + lastRevisionUser + ", lastRevisionDate="
				+ lastRevisionDate + "]";
	}

}
