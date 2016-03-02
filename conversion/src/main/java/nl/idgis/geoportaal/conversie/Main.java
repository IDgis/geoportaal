package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

	private static final String XML_ENCODING = "UTF-8";

	public static void main(String[] args) throws Exception {
		File xmlDirectory = getFileFromArray(args, 0);

		File csvFile = new File(getFileFromArray(args, 1) + "/conversion_input.csv");
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

	private static File getFileFromArray(String[] array, Integer numberParam) throws FileNotFoundException {
		if (array.length != 2)
			throw new IllegalArgumentException("aantal argumenten is niet gelijk aan 2");

		File file = new File(array[numberParam]);

		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());

		return file;
	}
}