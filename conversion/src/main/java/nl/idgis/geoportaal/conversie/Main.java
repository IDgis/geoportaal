package nl.idgis.geoportaal.conversie;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Main {

	private static final String OUTPUT_DIR = "./csvfiles";
	
	public static void main(String[] args) throws Exception {
		File xmlDirectory = getFileFromArray(args);
		File csvFile = new File(new File(OUTPUT_DIR) + "/csv_overview.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile), 2048);
		final String header = "\"title\";\"creator\";\"subject\";\"description\";\"publisher\";\"contributor\";\"date\";\"issued\";\"valid_start\";\"valid_end\";\"type\";\"format\";\"identifier\";\"references\";\"relation (id)\";\"source\";\"language\";\"relation (attachment)\";\"rights\";\"temporal_start\";\"temportal_end\";\"bbox_lowercorner\";\"bbox_uppercorner\"";
		writer.write(header);
		
		int i = 0;
		for (File file : xmlDirectory.listFiles()) {
			System.out.println(i++ + ": " + file.getName());
			try {
				convert(writer, file, csvFile);
			} catch(Exception e) {
				System.out.println("File is invalid");
				e.printStackTrace();
			}
			
		}
		
		writer.close();
	}
	
	private static class MetadataDocument {
		
		private final Document d;
	
		private final XPath xp;
		
		MetadataDocument(Document d, XPath xp) {
			this.d = d;
			this.xp = xp;
		}
		
		List<String> getStrings(String path) throws Exception {
			if(!path.endsWith("text()")) {
				throw new RuntimeException("path should end with text()");
			}
			
			List<String> retval = new ArrayList<>();
			
			NodeList nl = (NodeList)xp.evaluate(path, d, XPathConstants.NODESET);
			
			for(int i = 0; i < nl.getLength(); i++) {
				retval.add(nl.item(i).getNodeValue());
			}
			
			return retval;
		}
	}
	
	private static MetadataDocument parseDocument(File xmlFile) throws Exception {
		// parse xml to DOM
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = db.parse(new FileInputStream(xmlFile));
		
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ns.put("dc", "http://purl.org/dc/elements/1.1/");
		ns.put("dcterms", "http://purl.org/dc/terms/");
		ns.put("ows", "http://www.opengis.net/ows");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}

		NamespaceContext nc = new NamespaceContext() {

			@Override
			public String getNamespaceURI(String pf) {
				return ns.get(pf);
			}

			@Override
			public String getPrefix(String ns) {
				return pf.get(ns);
			}
			
			@Override
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String ns) {
				return Arrays.asList(getPrefix(ns)).iterator();
			}
			
		};
		
		// construct XPath evaluator
		XPathFactory xf = XPathFactory.newInstance();
		XPath xp = xf.newXPath();
		xp.setNamespaceContext(nc);
		
		return new MetadataDocument(d, xp);
	}

	private static void convert(BufferedWriter writer, File xmlFile, File csvFile) throws Exception {
		writer.newLine();
		
		MetadataDocument d = parseDocument(xmlFile);
		
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:title/text()"), true);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:creator/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:subject/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:description/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:publisher/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:contributor/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:date/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:issued/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:valid/start/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:valid/end/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:type/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:format/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:identifier/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:references/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:relation/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:source/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:language/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:relation/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dc:rights/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:temporal/start/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/dcterms:temporal/end/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:LowerCorner/text()"), false);
		writeToCSV(writer, d.getStrings("/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:UpperCorner/text()"), false);
	}
	
	private static void writeToCSV(BufferedWriter writer, List<String> elementList, Boolean first) throws IOException {
		if(elementList.size() == 0) {
			if(first) {
				writer.write("\"\"");
			} else {
				writer.write(";\"\"");
			}
		}
		
		for(String element : elementList) {
			String elementNew = element.replaceAll("[\\t\\n\\r]", " ");
			String elementFinal = elementNew.replaceAll("[\\\"]", "\'");
			
			if(elementList.indexOf(element) == 0) {
				if(first) {
					writer.write("\"" + elementFinal);
				} else {
					writer.write(";\"" + elementFinal);
				}
			} if(elementList.size() == 1) {
				writer.write("\"");
			} else if(elementList.indexOf(element) == elementList.size() - 1) {
				writer.write("," + elementFinal + "\"");
			} else {
				writer.write("," + elementFinal);
			}
		}
	}

	private static File getFileFromArray(String[] array) throws FileNotFoundException {
		if (array.length != 1)
			throw new IllegalArgumentException("aantal argumenten is niet gelijk aan 1");

		File xmlDirectory = new File(array[0]);

		if (!xmlDirectory.exists())
			throw new FileNotFoundException(xmlDirectory.getAbsolutePath());
		
		return xmlDirectory;
	}
}