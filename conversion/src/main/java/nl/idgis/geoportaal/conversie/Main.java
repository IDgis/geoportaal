package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.io.FileNotFoundException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

public class Main {

	private static final String DATABASE = "database";
	private static final String PORT = "port";
	private static final String SERVER_NAME = "serverName";
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

		Namespace res = parser.parseArgs(args);

		File xmlDirectory = new File(res.getString(XML_DIR));

		File csvFile = new File(res.getString(CSV_DIR) + "/conversion_input.csv");
		File txtFile = new File("./content_lengths.txt");

		Converter converter = new Converter(XML_ENCODING, new ToCSV(csvFile, txtFile,
				"\"file_name\";"
				+ "\"title\";\"creator\";\"subject\";\"description\";\"publisher\";"
				+ "\"contributor\";\"date\";\"issued\";\"valid_start\";\"valid_end\";"
				+ "\"type\";\"format\";\"identifier\";\"references\";\"relation_id\";"
				+ "\"source\";\"language\";\"relation_attachment\";\"http_status_code\";"
				+ "\"http_content_length\";\"rights_file\";\"rights_use\";\"temporal_start\";"
				+ "\"temportal_end\";\"bbox_lowercorner\";\"bbox_uppercorner\""));

		converter.convertFiles(xmlDirectory.listFiles());
	}
}
