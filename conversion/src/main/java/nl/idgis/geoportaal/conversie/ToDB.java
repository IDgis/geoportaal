package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;

public class ToDB implements OutDestination {

	private Connection connection;
	private boolean connected;

	public void connect(String username, String password, String serverName, int portNumber, String databaseName) throws SQLException {
		connection = createConnection(username, password, serverName, portNumber, databaseName);
		connected = connection.isValid(3000);
	}

	@Override
	public void convertFile(File file, MetadataDocument d) throws Exception {
		if (!connected)
			throw new Exception("connect method is nog niet aangeroepen of connectie is gesloten");

		MetadataRow row = MetadataRow.parseMetadataDocument(d);

		System.out.println(row.toString());
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
