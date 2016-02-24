package nl.idgis.geoportaal.conversie;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ToCSV implements OutDestination {

	private BufferedWriter csvWriter;
	private BufferedWriter txtWriter;

	public ToCSV(File csvFile, File txtFile, String header) throws IOException {
		txtWriter = new BufferedWriter(new FileWriter(txtFile), 2048);
		csvWriter = new BufferedWriter(new FileWriter(csvFile), 2048);
		csvWriter.write(header);
	}

	@Override
	public void convertFile(File xmlFile, MetadataDocument d) throws Exception {
		csvWriter.newLine();

		final String dataType = "rdf:datatype";
		final String fileName = xmlFile.getName();

		csvWriter.write("\"" + xmlFile.getName() + "\"");
		writeToCSV(csvWriter, d.getStrings(Path.TITLE.path()), true);
		writeToCSV(csvWriter, d.getStrings(Path.CREATOR.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.SUBJECT.path(), dataType, "theme:provisa", false), false);
		writeToCSV(csvWriter, d.getStrings(Path.DESCRIPTION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.PUBLISHER.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.CREATOR_OTHER.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.DATE_SOURCE_CREATION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.DATE_SOURCE_PUBLICATION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.DATE_SOURCE_VALID_FROM.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.DATE_SOURCE_VALID_UNTIL.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.TYPE_INFORMATION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.MD_FORMAT.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.UUID.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.LOCATION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.RELATION.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.SOURCE.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.LANGUAGE.path()), false);

		List<String> httpURLs = d.getStrings(Path.ATTACHMENT.path());
		writeToCSV(csvWriter, httpURLs, false);
		writeToCSV(csvWriter, retrieveStatusCodes(httpURLs), false);
		writeToCSV(csvWriter, retrieveContentLengths(httpURLs, txtWriter, fileName), false);

		writeToCSV(csvWriter, d.getStrings(Path.RIGHTS.path(), dataType, "gebruiksrestricties", false), false);
		writeToCSV(csvWriter, d.getStrings(Path.USE_LIMITATION.path(), dataType, "gebruiksrestricties", true), false);
		writeToCSV(csvWriter, d.getStrings(Path.TEMP_START.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.TEMP_END.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.LOWER_CORNER.path()), false);
		writeToCSV(csvWriter, d.getStrings(Path.UPPER_CORNER.path()), false);
	}

	private static List<String> retrieveStatusCodes(List<String> httpURLsStrings) {
		List<String> httpStatusCodes = new ArrayList<>();

		if(httpURLsStrings.isEmpty())
			return httpStatusCodes;

		String[] httpURLs = httpURLsStrings.get(0).split("\\s+");

		for(String httpURL : httpURLs)
			httpStatusCodes.add(requestStatusCode(httpURL));

		return httpStatusCodes;
	}

	private static String requestStatusCode(String httpURL) {
		HttpURLConnection.setFollowRedirects(false);
		int responseCode = 0;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(httpURL).openConnection();
			con.setRequestMethod("HEAD");
			responseCode = con.getResponseCode();
		} catch (Exception e) {
			return e.getClass().getName() + ": " + e.getMessage();
		}
		return Integer.toString(responseCode);
	}

	private static List<String> retrieveContentLengths(List<String> httpURLsStrings, BufferedWriter writerCL, String fileName) throws IOException {
		List<String> httpContentLengths = new ArrayList<>();

		if(httpURLsStrings.isEmpty())
			return httpContentLengths;

		String[] httpURLs = httpURLsStrings.get(0).split("\\s+");

		for(String httpURL : httpURLs) {
			String contLength = requestContentLength(httpURL);

			httpContentLengths.add(contLength + "MB");
			writerCL.write(fileName + ",");
			writerCL.write(requestContentLength(httpURL));
			writerCL.newLine();
		}

		return httpContentLengths;
	}

	private static String requestContentLength(String httpURL) {
		HttpURLConnection.setFollowRedirects(false);
		int contentLengthInMb = 0;
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(httpURL).openConnection();
			con.setRequestMethod("HEAD");
			contentLengthInMb = con.getContentLength() / 1024 / 1024;
		} catch (Exception e) {
			return e.getClass().getName() + ": " + e.getMessage();
		}
		return Integer.toString(contentLengthInMb);
	}

	private static void writeToCSV(BufferedWriter writer, List<String> elementList, Boolean first) throws IOException {
		if(elementList.size() == 0) {
			if(first) {
				writer.write("\"\"");
			} else {
				writer.write(";\"\"");
			}
		}

		for(int i = 0; i < elementList.size(); i++) {
			String element = elementList.get(i);
			String elementNew = element.replaceAll("[\\t\\n\\r]", " ");
			String elementFinal = elementNew.replaceAll("[\\\"]", "\'");

			boolean firstOfList = false;
			if(i == 0) {
				writer.write(";\"" + elementFinal);
				firstOfList = true;
			}

			if(elementList.size() == 1) {
				writer.write("\"");
			} else if(i == elementList.size() - 1) {
				writer.write("," + elementFinal + "\"");
			} else if(!firstOfList) {
				writer.write("," + elementFinal);
			}
		}
	}
}
