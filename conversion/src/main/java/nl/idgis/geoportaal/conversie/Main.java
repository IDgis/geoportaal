package nl.idgis.geoportaal.conversie;

import java.io.File;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

	private static final String SCHEMA = "schema";
	private static final String DATABASE = "database";
	private static final String PORT = "port";
	private static final String SERVER_NAME = "servername";
	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	private static final String CSV_DIR = "csvDir";
	private static final String XML_DIR = "xmlDir";
	private static final String XML_ENCODING = "UTF-8";

	public static void main(String[] args) throws Exception {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("conversion");

		parser.addArgument("-" + XML_DIR).dest(XML_DIR).type(String.class).help("map met XML-bestanden").required(true);
		parser.addArgument("-" + CSV_DIR).type(String.class).help("map waar CSV-bestand met resultaten naar wordt geschreven (default: 'csvfiles')").setDefault("csvfiles");
		parser.addArgument("-" + USERNAME).type(String.class).help("gebruikersnaam om in te loggen op de databaseserver");
		parser.addArgument("-" + PASSWORD).type(String.class).help("wachtwoord om in te loggen op de databaseserver");
		parser.addArgument("-" + SERVER_NAME).type(String.class).help("server waarop de database zich bevindt (bijvoorbeeld 'localhost'");
		parser.addArgument("-" + PORT).type(Integer.class).help("poortnummer van de server");
		parser.addArgument("-" + DATABASE).type(String.class).help("naam van de database");
		parser.addArgument("-" + SCHEMA).type(String.class).help("database schema indien in public").setDefault("public");

		File xmlDirectory = null;
		File csvFile = null;
		File txtFile = null;
		String username = null;
		String password = null;
		String serverName = null;
		Integer port = null;
		String database = null;
		String schema = null;

		try {
			Namespace res = parser.parseArgs(args);

			xmlDirectory = new File(res.getString(XML_DIR));
			csvFile = new File(res.getString(CSV_DIR) + "/conversion_input.csv");
			txtFile = new File("./content_lengths.txt");
			username = res.getString(USERNAME);
			password = res.getString(PASSWORD);
			serverName = res.getString(SERVER_NAME);
			port = res.getInt(PORT);
			database = res.getString(DATABASE);
			schema = res.getString(SCHEMA);
		} catch (ArgumentParserException e) {
            parser.handleError(e);
            return;
        }

		if (!xmlDirectory.exists() && !xmlDirectory.isDirectory())
			throw new Exception(xmlDirectory.getPath() + " is niet geldig");

		boolean toCsv = username == null && password == null && serverName == null && port == null && database == null;

		OutDestination out;

		if (toCsv) {
			out = new ToCSV(csvFile, txtFile,
					"\"file_name\";" + "\"title\";\"creator\";\"subject\";\"description\";\"publisher\";"
							+ "\"contributor\";\"date\";\"issued\";\"valid_start\";\"valid_end\";"
							+ "\"type\";\"format\";\"identifier\";\"references\";\"relation_id\";"
							+ "\"source\";\"language\";\"relation_attachment\";\"http_status_code\";"
							+ "\"http_content_length\";\"rights_file\";\"rights_use\";\"temporal_start\";"
							+ "\"temportal_end\";\"bbox_lowercorner\";\"bbox_uppercorner\"");
		} else {
			out = new ToDB();
			((ToDB) out).connect(username, password, serverName, port, database, schema);
		}

		Converter converter = new Converter(XML_ENCODING, out);

		converter.convertFiles(xmlDirectory.listFiles());
	}
}
