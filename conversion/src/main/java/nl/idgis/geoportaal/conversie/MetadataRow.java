package nl.idgis.geoportaal.conversie;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class MetadataRow {

	private static final String DATA_TYPE = "rdf:datatype";
	private static final String TABLE_TYPE_INFORMATION = "type_information";
	private static final String TABLE_CREATOR = "creator";
	private static final String TABLE_RIGHTS = "rights";
	private static final String TABLE_USE_LIMITATION = "use_limitation";
	private static final String TABLE_MD_FORMAT = "md_format";
	private static final String TABLE_SUPPLIER = "supplier";
	private static final String TABLE_STATUS = "status";
	private static final String SUPPLIER = "nienhuis";
	private static final String STATUS = "ter goedkeuring";
	private static final String LAST_REVISION_USER = "conversie";
	private static final boolean PUBLISHED = false;

	private String uuid;
	private String location;
	private String fileId;
	private String title;
	private String description;
	private Label typeInformation;
	private Label creator;
	private String creatorOther;
	private Label rights;
	private Label useLimitation;
	private Label mdFormat;
	private String source;
	private Timestamp dateSourceCreation;
	private Timestamp dateSourcePublication;
	private Timestamp dateSourceRevision;
	private Timestamp dateSourceValidFrom;
	private Timestamp dateSourceValidUntil;
	private Label supplier;
	private Label status;
	private Boolean published;
	private String lastRevisionUser;
	private Timestamp lastRevisionDate;

	public static MetadataRow parseMetadataDocument(MetadataDocument d, Mapper creatorMapper, Mapper useLimitationMapper) throws Exception {
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
		row.setTypeInformation(new Label(retrieveFirstStringOrNull(Path.TYPE_INFORMATION, d), TABLE_TYPE_INFORMATION));
		row.setCreator(new Label(map(retrieveFirstStringOrNull(Path.CREATOR, d), creatorMapper), TABLE_CREATOR));
		row.setCreatorOther(retrieveFirstStringOrNull(Path.CREATOR_OTHER, d));
		row.setRights(new Label(retrieveFirstStringOrNull(Path.RIGHTS, d, DATA_TYPE, "gebruiksrestricties", false), TABLE_RIGHTS));
		row.setUseLimitation(new Label(map(retrieveFirstStringOrNull(Path.USE_LIMITATION, d, DATA_TYPE, "gebruiksrestricties", true), useLimitationMapper), TABLE_USE_LIMITATION));
		row.setMdFormat(new Label(retrieveFirstStringOrNull(Path.MD_FORMAT, d), TABLE_MD_FORMAT));
		row.setSource(retrieveFirstStringOrNull(Path.SOURCE, d));
		row.setDateSourceCreation(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_CREATION, d)));
		row.setDateSourcePublication(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_PUBLICATION, d)));
		row.setDateSourceRevision(null);
		row.setDateSourceValidFrom(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_VALID_FROM, d)));
		row.setDateSourceValidUntil(toTime(retrieveFirstStringOrNull(Path.DATE_SOURCE_VALID_UNTIL, d)));
		row.setSupplier(new Label(SUPPLIER, TABLE_SUPPLIER));
		row.setStatus(new Label(STATUS, TABLE_STATUS));
		row.setPublished(PUBLISHED);
		row.setLastRevisionUser(LAST_REVISION_USER);
		row.setLastRevisionDate(new Timestamp(System.currentTimeMillis()));

		return row;
	}

	private static String map(String creator, Mapper mapper) throws Exception {
		String newValue = mapper.get(creator);
		if (newValue != null)
			return newValue;

		return creator;
	}

	private static String retrieveFirstStringOrNull(Path path, MetadataDocument d) throws Exception {
		return retrieveFirstStringOrNull(path, d, null, null, false);
	}

	private static String retrieveFirstStringOrNull(Path path, MetadataDocument d, String attrName, String attrValue, boolean shouldMatch) throws Exception {
		List<String> strings = d.getStrings(path.path(), attrName, attrValue, shouldMatch);

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

	public Label getTypeInformation() {
		return typeInformation;
	}

	public void setTypeInformation(Label typeInformation) {
		this.typeInformation = typeInformation;
	}

	public Label getCreator() {
		return creator;
	}

	public void setCreator(Label creator) {
		this.creator = creator;
	}

	public String getCreatorOther() {
		return creatorOther;
	}

	public void setCreatorOther(String creatorOther) {
		this.creatorOther = creatorOther;
	}

	public Label getRights() {
		return rights;
	}

	public void setRights(Label rights) {
		this.rights = rights;
	}

	public Label getUseLimitation() {
		return useLimitation;
	}

	public void setUseLimitation(Label useLimitation) {
		this.useLimitation = useLimitation;
	}

	public Label getMdFormat() {
		return mdFormat;
	}

	public void setMdFormat(Label mdFormat) {
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

	public Timestamp getDateSourceRevision() {
		return dateSourceRevision;
	}

	public void setDateSourceRevision(Timestamp dateSourceRevision) {
		this.dateSourceRevision = dateSourceRevision;
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

	public Label getSupplier() {
		return supplier;
	}

	public void setSupplier(Label supplier) {
		this.supplier = supplier;
	}

	public Label getStatus() {
		return status;
	}

	public void setStatus(Label status) {
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
