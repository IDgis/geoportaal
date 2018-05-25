package nl.idgis.portal.harvester.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class GetMetadataDocument {
	
	public static MetadataDocument parse(Document d, Map<String, String> ns, Map<String, String> pf) {
		// parse xml to DOM
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
