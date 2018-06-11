package nl.idgis.portal.harvester.util;

import java.util.ArrayList;
import java.util.List;

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
		if(!path.endsWith("text()") && !path.endsWith("@uuidref") &&!path.endsWith("codeListValue")) {
			throw new RuntimeException("path should end with text() or @uuidref");
		}
		
		NodeList nl = (NodeList) xp.evaluate(path, d, XPathConstants.NODESET);
		
		if(nl.item(0) != null) {
			return nl.item(0).getNodeValue().trim();
		}
		
		return null;
	}
	
	public List<String> getStrings(String path) throws Exception {
		if(!path.endsWith("text()") && !path.endsWith("@uuidref") && !path.endsWith("codeListValue")) {
			throw new RuntimeException("path should end with text() or @uuidref");
		}
		
		List<String> values = new ArrayList<>();
		
		NodeList nl = (NodeList) xp.evaluate(path, d, XPathConstants.NODESET);
		
		for(int i = 0; i < nl.getLength(); i++) {
			values.add(nl.item(i).getNodeValue());
		}
		
		return values;
	}
}
