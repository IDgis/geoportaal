package nl.idgis.portal.harvester.util;

import org.apache.jackrabbit.webdav.property.DavProperty;

public class WebDavBooleanParser {
	
	public static boolean parse(DavProperty<?> value) {
		if(value == null) {
			return false;
		} else {
			return Boolean.parseBoolean(value.getValue().toString());
		}
	}
}
