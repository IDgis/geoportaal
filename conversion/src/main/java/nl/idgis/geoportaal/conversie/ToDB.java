package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.ds.PGSimpleDataSource;

public class ToDB implements OutDestination {

	private String schema;
	private Connection connection;
	private boolean connected;

	public void connect(String username, String password, String serverName, int portNumber, String databaseName, String schema) throws SQLException {
		connection = createConnection(username, password, serverName, portNumber, databaseName);
		connected = connection.isValid(3000);
		this.schema = schema;
	}

	@Override
	public void convertFile(File file, MetadataDocument d) throws Exception {
		if (!connected)
			throw new Exception("connect method is nog niet aangeroepen of connectie is gesloten");

		MetadataRow row = MetadataRow.parseMetadataDocument(d);

		System.out.println(row.toString());

		PreparedStatement statement = connection.prepareStatement("INSERT INTO " + schema
				+ ".metadata VALUES(uuid=?,location=?,file_id=?,title=?,description=?,"
				+ "type_information=?,creator=?,creator_other?,rights=?,use_limitation=?,"
				+ "md_format=?,source=?,date_source_creation=?,date_source_publication?,"
				+ "date_source_revision=?,date_source_valid_from=?,"
				+ "date_source_valid_until=?,supplier=?,status=?,published=?,"
				+ "last_revision_user=?,last_revision_date=?)");
		putValuesInStatement(row, statement);

		System.out.println(statement.toString());

		statement.cancel();

	}

	private void putValuesInStatement(MetadataRow row, PreparedStatement statement) throws Exception {
		statement.setString(1, row.getUuid());
		statement.setString(2, row.getLocation());
		statement.setString(3, row.getFileId());
		statement.setString(4, row.getTitle());
		statement.setString(5, row.getDescription());
		statement.setInt(6, resolveIntFromLabel(row.getTypeInformation()));
		statement.setInt(7, resolveIntFromLabel(row.getCreator()));
		statement.setString(8, row.getCreatorOther());
		statement.setInt(9, resolveIntFromLabel(row.getRights()));
		statement.setInt(10, resolveIntFromLabel(row.getUseLimitation()));
		statement.setInt(11, resolveIntFromLabel(row.getMdFormat()));
		statement.setString(12, row.getSource());
		statement.setTimestamp(13,row.getDateSourceCreation());
		statement.setTimestamp(14, row.getDateSourcePublication());
		statement.setTimestamp(15, row.getDateSourceRevision());
		statement.setTimestamp(16, row.getDateSourceValidFrom());
		statement.setTimestamp(17, row.getDateSourceValidUntil());
		statement.setInt(18, resolveIntFromLabel(row.getSupplier()));
		statement.setInt(19, resolveIntFromLabel(row.getStatus()));
		statement.setBoolean(20, row.getPublished());
		statement.setString(21, row.getLastRevisionUser());
		statement.setTimestamp(22, row.getLastRevisionDate());
	}

	private Integer resolveIntFromLabel(Label label) throws Exception {
		final String table = label.getTable();
		final String labelTable = label.getLabelTable();
		final String value = label.getValue();

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
		System.out.println(sql);
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
}
