package nl.idgis.geoportaal.conversie;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Converter {

	private String xmlEncoding;
	private OutDestination outType;

	public Converter(String xmlEncoding, OutDestination outType) throws IOException {
		this.xmlEncoding = xmlEncoding;
		this.outType = outType;
	}

	public void convertFiles(File[] files) throws Exception {
		try {
			PrintWriter errorWriter = new PrintWriter("log.txt");

			int countSuccessful = 0;

			int i = 0;
			for (File file : files) {
				System.out.println(i++ + ": " + file.getName());
				try {
					outType.convertFile(file, parseDocument(file));
					countSuccessful++;
				} catch(Exception e) {
					System.out.println("File is invalid: " + file.getName());
					e.printStackTrace();
					errorWriter.println("File is invalid: " + file.getName());
					e.printStackTrace(errorWriter);
					errorWriter.println();
					errorWriter.flush();
				}
			}

			errorWriter.close();

			System.out.println(countSuccessful + "/" + i + " succesvol verwerkt");
		} finally {
			outType.close();
		}
	}

	private MetadataDocument parseDocument(File xmlFile) throws Exception {
		// parse xml to DOM
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = db.parse(new InputSource(new InputStreamReader(new FileInputStream(xmlFile), xmlEncoding)));

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
}
