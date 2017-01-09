package nl.idgis.geoportaal.attributecheck;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Main {
	public static void main(String[] args) throws Exception {
		try {
			String pathOfBase = args[1];
			String pathOfCheck = args[3];
			String csvFile = args[5];
			
			Set<MetadataDescription> setBase = build(pathOfBase);
			Set<MetadataDescription> setCheck = build(pathOfCheck);
			
			List<MetadataDescription> missingMds = new ArrayList<>();
			
			for(MetadataDescription md : setBase) {
				if(!setCheck.contains(md)) {
					missingMds.add(md);
				}
			}
			File f = new File(csvFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(f), 2048);
			
			writer.write("\"fileIdentifier\";\"metadataIdentifier\";\"table\"");
			writer.newLine();
			
			for(MetadataDescription md : missingMds) {
				writer.write("\"" + md.getFileIdentifier() + "\";\"" + md.getMetadataIdentifier() + "\";\""
						+ md.getTable() + "\"");
				writer.newLine();
			}
			
			writer.flush();
			writer.close();
		} catch(ArrayIndexOutOfBoundsException aiob) {
			System.out.println("You made an error in the arguments");
			throw aiob;
		} catch(FileNotFoundException fnf) {
			System.out.println("Directory of csv output is not valid");
			throw fnf;
		}
	}
	
	public static Set<MetadataDescription> build(String s) throws Exception {
		Path path = Paths.get(s);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Map<String, String> ns = new HashMap<>();
		ns.put("gml", "http://www.opengis.net/gml");
		ns.put("xlink", "http://www.w3.org/1999/xlink");
		ns.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		
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
		
		XPathFactory xf = XPathFactory.newInstance();
		XPath xp = xf.newXPath();
		xp.setNamespaceContext(nc);
		
		Set<MetadataDescription> smd = new HashSet<>();
		
		if(Files.isDirectory(path)) {
			Files.list(path)
					.forEach(p -> {
						try {
							Document d = db.parse(
									new InputSource(
											new InputStreamReader(
													new FileInputStream(
															p.toAbsolutePath().toString()), 
															"UTF-8")));
							
							NodeList fileIdentifierNodes = (NodeList) xp.evaluate(
									"//gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString/text()",
									d, XPathConstants.NODESET);
							
							NodeList metadataIdentifierNodes = (NodeList) xp.evaluate(
									"//gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/"
									+ "gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/"
									+ "gmd:code/gco:CharacterString/text()",
									d, XPathConstants.NODESET);
							
							NodeList tableNodes = (NodeList) xp.evaluate(
									"//gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/"
									+ "gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString/"
									+ "text()",
									d, XPathConstants.NODESET);
							
							String fileIdentifier = getIdentifier(nullCheckNode(fileIdentifierNodes));
							String metadataIdentifier = getIdentifier(nullCheckNode(metadataIdentifierNodes));
							String table = getTableName(nullCheckNode(tableNodes));
							
							smd.add(new MetadataDescription(
									fileIdentifier, 
									metadataIdentifier, 
									table));
						} catch(SAXException se) {
							System.out.println(p.toAbsolutePath() + " couldn't be parsed");
							se.printStackTrace();
						} catch(Exception e) {
							System.out.println("there is something wrong with " + p.toAbsolutePath());
							e.printStackTrace();
						}
			});
			
			return smd;
		} else {
			throw new Exception(s + " is not a valid directory");
		}
	}
	
	public static String nullCheckNode(NodeList nl) {
		if(nl.item(0) != null) {
			return nl.item(0).getNodeValue();
		} else {
			return null;
		}
	}
	
	public static String getIdentifier(String identifier) {
		if(identifier != null && !identifier.trim().isEmpty()) {
			return identifier.trim().toLowerCase();
		}
		
		return null;
	}
	
	public static String getTableName(String table) {
		if(table != null && !table.trim().isEmpty()) {
			
			final String tableName;
			if(table.contains(" ")) {
				tableName = table.substring(0, table.indexOf(" ")).trim();
			} else {
				tableName = table.trim();
			}
			
			return tableName.replace(":", ".").toLowerCase();
		}
		
		return null;
	}
}