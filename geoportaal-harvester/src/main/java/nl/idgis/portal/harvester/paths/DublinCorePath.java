package nl.idgis.portal.harvester.paths;

public enum DublinCorePath {
	TITLE ("/rdf:RDF/rdf:Description/dc:title/text()"),
	ABSTRACT ("/rdf:RDF/rdf:Description/dc:description/text()"),
	NUMBER ("/rdf:RDF/rdf:Description/dcterms:relation/text()"),
	LOCATION ("/rdf:RDF/rdf:Description/dcterms:references/text()"),
	ATTACHMENT ("/rdf:RDF/rdf:Description/dc:relation/text()"),
	USE_LIMITATION ("/rdf:RDF/rdf:Description/dc:rights[@rdf:datatype='gebruiksrestricties'][1]/text()"),
	
	ORGANISATION_CREATOR ("/rdf:RDF/rdf:Description/dc:creator/text()"),
	PUBLISHER ("/rdf:RDF/rdf:Description/dc:publisher/text()"),
	SOURCE ("/rdf:RDF/rdf:Description/dc:source/text()"),
	
	DATE_CREATED ("/rdf:RDF/rdf:Description/dc:date/text()"),
	DATE_PUBLISHED ("/rdf:RDF/rdf:Description/dcterms:issued/text()"),
	
	UUID ("/rdf:RDF/rdf:Description/dc:identifier/text()"),
	SUBJECT ("/rdf:RDF/rdf:Description/dc:subject/text()"),
	RIGHTS ("/rdf:RDF/rdf:Description/dc:rights/text()"),
	FORMAT ("/rdf:RDF/rdf:Description/dc:format/text()"),
	LANGUAGE ("/rdf:RDF/rdf:Description/dc:language/text()"),
	TYPE ("/rdf:RDF/rdf:Description/dc:type/text()"),
	
	CONTRIBUTOR ("/rdf:RDF/rdf:Description/dc:contributor/text()")
	;
	
	private final String path;

	DublinCorePath(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
