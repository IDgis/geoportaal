package nl.idgis.geoportaal.conversie;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetadataDocument {
	
	private final Document d;

	private final XPath xp;
	
	MetadataDocument(Document d, XPath xp) {
		this.d = d;
		this.xp = xp;
	}
	
	List<String> getStrings(String path) throws Exception {
		return getStrings(path, null, null, false);
	}
	
	// Only add nodes when attribute has specific value
	List<String> getStrings(String path, String attributeName, String attributeValue, boolean shouldAttrValMatch) throws Exception {
		List<String> retval = new ArrayList<>();
		
		NodeList nl = (NodeList)xp.evaluate(path, d, XPathConstants.NODESET);
		
		for(int i = 0; i < nl.getLength(); i++) {
			Node item = nl.item(i);
			String text = item.getTextContent();
			
			if(hasAttributeMatch(item, attributeName, attributeValue) == shouldAttrValMatch)
				retval.add(text);
		}
		
		return retval;
	}

	private boolean hasAttributeMatch(Node item, String attributeName, String attributeValue) {
		if(attributeName == null || attributeValue == null)
			return false;
		
		NamedNodeMap attributes = item.getAttributes();
		if(attributes == null)
			return false;
		
		Node attribute = attributes.getNamedItem(attributeName);
		if(attribute == null)
			return false;
		
		String attributeFoundValue = attribute.getNodeValue();
		if(attributeValue == null || !attributeFoundValue.equals(attributeValue))
			return false;
		
		return true;
	}
}

