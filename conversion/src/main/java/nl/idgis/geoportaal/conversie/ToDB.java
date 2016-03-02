package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;

public class ToDB implements OutDestination {

	private Connection connection;

	public ToDB(String username, String password, String serverName, int portNumber, String databaseName) throws SQLException {
		connection = createConnection(username, password, serverName, portNumber, databaseName);
	}

	@Override
	public void convertFile(File file, MetadataDocument d) throws Exception {
		MetadataRow row = MetadataRow.parseMetadataDocument(d);

		// TODO: insert row
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
