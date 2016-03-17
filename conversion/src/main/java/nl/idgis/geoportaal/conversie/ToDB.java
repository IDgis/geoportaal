package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

public class ToDB implements OutDestination {

	private static Mapper creatorMapper = new Mapper("creator_conversion.csv");
	private static Mapper useLimitationMapper = new Mapper("use_limitation_conversion.csv");
	private static Mapper mdFormatMapper = new Mapper("md_format_conversion.csv");

	private String schema;
	private Connection connection;
	private boolean connected;

	public void connect(String username, String password, String serverName, int portNumber, String databaseName, String schema) throws SQLException {
		connection = createConnection(username, password, serverName, portNumber, databaseName);
		connection.setAutoCommit(false);
		connected = connection.isValid(3000);
		this.schema = schema;
	}

	@Override
	public void convertFile(File file, MetadataDocument d) throws Exception {
		if (!connected)
			throw new Exception("connect method is nog niet aangeroepen of connectie is gesloten");

		MetadataRow row = MetadataRow.parseMetadataDocument(d, creatorMapper, useLimitationMapper, mdFormatMapper);

		final String metadataSql = "INSERT INTO " + schema + ".metadata (uuid,location,file_id,title,description,"
				+ "type_information,creator,creator_other,rights,use_limitation,"
				+ "md_format,source,date_source_creation,date_source_publication,"
				+ "date_source_revision,date_source_valid_from,date_source_valid_until,"
				+ "supplier,status,published,last_revision_user,last_revision_date) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


		PreparedStatement metadataStatement = connection.prepareStatement(metadataSql, Statement.RETURN_GENERATED_KEYS);


		metadataStatement.setObject(1, row.getUuid(), Types.VARCHAR);
		metadataStatement.setObject(2, row.getLocation(), Types.VARCHAR);
		metadataStatement.setObject(3, row.getFileId(), Types.VARCHAR);
		metadataStatement.setObject(4, row.getTitle(), Types.VARCHAR);
		metadataStatement.setObject(5, row.getDescription(), Types.VARCHAR);
		metadataStatement.setObject(6, resolveIntFromLabel(row.getTypeInformation()), Types.INTEGER);
		metadataStatement.setObject(7, resolveIntFromLabel(row.getCreator()), Types.INTEGER);
		metadataStatement.setObject(8, getCreatorOther(row.getCreator(), row), Types.VARCHAR);
		metadataStatement.setObject(9, resolveIntFromLabel(row.getRights()), Types.INTEGER);
		metadataStatement.setObject(10, resolveIntFromLabel(row.getUseLimitation()), Types.INTEGER);
		metadataStatement.setObject(11, resolveIntFromLabel(row.getMdFormat()), Types.INTEGER);
		metadataStatement.setObject(12, row.getSource(), Types.VARCHAR);
		metadataStatement.setObject(13,row.getDateSourceCreation(), Types.TIMESTAMP);
		metadataStatement.setObject(14, row.getDateSourcePublication(), Types.TIMESTAMP);
		metadataStatement.setObject(15, row.getDateSourceRevision(), Types.TIMESTAMP);
		metadataStatement.setObject(16, row.getDateSourceValidFrom(), Types.TIMESTAMP);
		metadataStatement.setObject(17, row.getDateSourceValidUntil(), Types.TIMESTAMP);
		metadataStatement.setObject(18, resolveIntFromLabel(row.getSupplier()), Types.INTEGER);
		metadataStatement.setObject(19, resolveIntFromLabel(row.getStatus()), Types.INTEGER);
		metadataStatement.setObject(20, row.getPublished(), Types.BOOLEAN);
		metadataStatement.setObject(21, row.getLastRevisionUser(), Types.VARCHAR);
		metadataStatement.setObject(22, row.getLastRevisionDate(), Types.TIMESTAMP);

		try {
			metadataStatement.executeUpdate();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}

		ResultSet genKeys = metadataStatement.getGeneratedKeys();
		if (!genKeys.next())
			throw new Exception("geen gegenereerde primary key gevonden");

		final int metadataId = genKeys.getInt(1);

		PreparedStatement attachmentStatement = connection.prepareStatement(
				"INSERT INTO " + schema + ".md_attachment (metadata_id,attachment_name,"
						+ "attachment_content,attachment_mimetype) VALUES (?,?,?,?)");

		final String[] attachmentUrls = row.getAttachment();

		if (attachmentUrls != null) {
			Attachment attachment = null;
			for (String attachmentUrl : attachmentUrls) {
				attachment = Attachment.openConnection(attachmentUrl);
				attachmentStatement.setObject(1, metadataId, Types.INTEGER);
				attachmentStatement.setObject(2, attachment.getFileName(), Types.VARCHAR);
				attachmentStatement.setBinaryStream(3, attachment.getDataStream(), attachment.getLength());
				attachmentStatement.setObject(4, attachment.getMimeType(),Types.VARCHAR);
				attachmentStatement.executeUpdate();
			}

			if (attachment != null) {
				attachment.close();
			}
		}

		PreparedStatement subjectStatement = connection.prepareStatement("INSERT INTO " + schema + ".md_subject (metadata_id,subject) VALUES (?,?)");

		final List<Label> subjects = row.getSubject();

		if (subjects != null) {
			for (Label subject : subjects) {
				subjectStatement.setInt(1, metadataId);
				subjectStatement.setInt(2, resolveIntFromLabel(subject));
				subjectStatement.executeUpdate();
			}
		}

		connection.commit();
	}

	private Integer resolveIntFromLabel(Label label) throws Exception {
		final String table = label.getTable();
		final String labelTable = label.getLabelTable();
		final String value = label.getValue();
		
		if (value == null)
			return null;
		
		Integer id = null;
		if(table.equals("user")) {
			id = select("id", table, "username", value);
		} else {
			id = select("id", table, "name", value);
		}
		
		if (id != null)
			return id;

		id = select(table + "_id", labelTable, "label", value);
		
		if(table.equals("creator") && id == null) {
			id = 9;
		}
		
		if (id != null)
			return id;

		throw new Exception("'" + value + "' niet gevonden in tabellen " + table + " en " + labelTable);
	}
	
	private String getCreatorOther(Label label, MetadataRow row) throws Exception {
		Integer id = resolveIntFromLabel(label);
		if(id.equals(9)) {
			return row.getCreatorOther().getValue();
		} else {
			return "";
		}
	}

	private <T> T select(String valueColumn, String table, String whereColumn, String whereValue) throws Exception {
		final String sql = "SELECT " + valueColumn + " FROM " + schema + "." + table
				+ " WHERE LOWER(" + whereColumn + ") = LOWER('" + whereValue + "')";
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(sql);

		List<T> values = new ArrayList<>();
		while (results.next()) {
			values.add((T) results.getObject(1));
		}

		if (values.isEmpty())
			return null;

		if (values.size() > 1)
			throw new Exception("meer dan 1 resultaat verkregen uit query: " + sql);

		return values.get(0);
	}

	private Connection createConnection(String username, String password, String serverName, int portNumber, String databaseName) throws SQLException {
		Connection connection;
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setServerName(serverName);
		dataSource.setPortNumber(portNumber);
		dataSource.setDatabaseName(databaseName);

		connection = dataSource.getConnection();

		return connection;
	}

	@Override
	public void close() throws SQLException {
		connection.close();
	}
}
