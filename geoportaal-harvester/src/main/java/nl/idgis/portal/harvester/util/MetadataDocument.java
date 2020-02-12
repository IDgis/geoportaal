package nl.idgis.portal.harvester.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MetadataDocument {
	private final Document d;
	private final XPath xp;
	
	public MetadataDocument(Document d, XPath xp) {
		this.d = d;
		this.xp = xp;
	}
	
	public String getString(String path) throws XPathExpressionException {
		if(!path.endsWith("text()") && !path.endsWith("@uuidref") && !path.endsWith("codeListValue")) {
			throw new RuntimeException("path should end with text() or @uuidref");
		}
		
		NodeList nl = (NodeList) xp.evaluate(path, d, XPathConstants.NODESET);
		
		if(nl.item(0) != null) {
			return handleLineEndings(nl.item(0).getNodeValue().trim());
		}
		
		return null;
	}
	
	public List<String> getStrings(String path) throws Exception {
		if(!path.endsWith("text()") && !path.endsWith("@uuidref") && !path.endsWith("@codeListValue")) {
			throw new RuntimeException("path should end with text(), @uuidref or @codeListValue");
		}
		
		List<String> values = new ArrayList<>();
		
		NodeList nl = (NodeList) xp.evaluate(path, d, XPathConstants.NODESET);
		
		for(int i = 0; i < nl.getLength(); i++) {
			values.add(handleLineEndings(nl.item(i).getNodeValue()));
		}
		
		return values;
	}
	
	private String handleLineEndings(String value) {
		return 
			Arrays.asList(value.split("\n"))
				.stream()
				.map(s -> s.trim())
				.collect(Collectors.joining(" "));
	}
}
