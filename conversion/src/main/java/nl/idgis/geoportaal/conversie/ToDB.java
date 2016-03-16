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

		MetadataRow row = MetadataRow.parseMetadataDocument(d, creatorMapper, useLimitationMapper);

		PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO " + schema + ".metadata (uuid,location,file_id,title,description,"
						+ "type_information,creator,creator_other,rights,use_limitation,"
						+ "md_format,source,date_source_creation,date_source_publication,"
						+ "date_source_revision,date_source_valid_from,date_source_valid_until,"
						+ "supplier,status,published,last_revision_user,last_revision_date) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		putValuesInStatement(row, statement);

		statement.executeUpdate();

		connection.commit();
	}

	private void putValuesInStatement(MetadataRow row, PreparedStatement statement) throws Exception {
		statement.setObject(1, row.getUuid(), Types.VARCHAR);
		statement.setObject(2, row.getLocation(), Types.VARCHAR);
		statement.setObject(3, row.getFileId(), Types.VARCHAR);
		statement.setObject(4, row.getTitle(), Types.VARCHAR);
		statement.setObject(5, row.getDescription(), Types.VARCHAR);
		statement.setObject(6, resolveIntFromLabel(row.getTypeInformation()), Types.INTEGER);
		statement.setObject(7, resolveIntFromLabel(row.getCreator()), Types.INTEGER);
		statement.setObject(8, row.getCreatorOther(), Types.VARCHAR);
		statement.setObject(9, resolveIntFromLabel(row.getRights()), Types.INTEGER);
		statement.setObject(10, resolveIntFromLabel(row.getUseLimitation()), Types.INTEGER);
		statement.setObject(11, resolveIntFromLabel(row.getMdFormat()), Types.INTEGER);
		statement.setObject(12, row.getSource(), Types.VARCHAR);
		statement.setObject(13,row.getDateSourceCreation(), Types.TIMESTAMP);
		statement.setObject(14, row.getDateSourcePublication(), Types.TIMESTAMP);
		statement.setObject(15, row.getDateSourceRevision(), Types.TIMESTAMP);
		statement.setObject(16, row.getDateSourceValidFrom(), Types.TIMESTAMP);
		statement.setObject(17, row.getDateSourceValidUntil(), Types.TIMESTAMP);
		statement.setObject(18, resolveIntFromLabel(row.getSupplier()), Types.INTEGER);
		statement.setObject(19, resolveIntFromLabel(row.getStatus()), Types.INTEGER);
		statement.setObject(20, row.getPublished(), Types.BOOLEAN);
		statement.setObject(21, row.getLastRevisionUser(), Types.VARCHAR);
		statement.setObject(22, row.getLastRevisionDate(), Types.TIMESTAMP);
	}

	private Integer resolveIntFromLabel(Label label) throws Exception {
		final String table = label.getTable();
		final String labelTable = label.getLabelTable();
		final String value = label.getValue();

		if (value == null)
			return null;

		Integer id = select("id", table, "name", value);

		if (id != null)
			return id;

		id = select(table + "_id", labelTable, "label", value);

		if (id!= null)
			return id;

		throw new Exception("'" + value + "' niet gevonden in tabellen " + table + " en " + labelTable);
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
