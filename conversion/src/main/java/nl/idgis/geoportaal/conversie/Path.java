package nl.idgis.geoportaal.conversie;

public enum Path {

	TITLE ("/rdf:RDF/rdf:Description/dc:title"),
	SUBJECT ("/rdf:RDF/rdf:Description/dc:subject"),
	RELATION ("/rdf:RDF/rdf:Description/dcterms:relation"),
	ATTACHMENT ("/rdf:RDF/rdf:Description/dc:relation"),
	TEMP_START ("/rdf:RDF/rdf:Description/dcterms:temporal/start"),
	TEMP_END ("/rdf:RDF/rdf:Description/dcterms:temporal/end"),
	PUBLISHER ("/rdf:RDF/rdf:Description/dc:publisher"),
	LANGUAGE ("/rdf:RDF/rdf:Description/dc:language"),
	UUID ("/rdf:RDF/rdf:Description/dc:identifier"),
	LOCATION ("/rdf:RDF/rdf:Description/dcterms:references"),
	DESCRIPTION ("/rdf:RDF/rdf:Description/dc:description"),
	TYPE_INFORMATION ("/rdf:RDF/rdf:Description/dc:type"),
	CREATOR ("/rdf:RDF/rdf:Description/dc:creator"),
	CREATOR_OTHER ("/rdf:RDF/rdf:Description/dc:contributor"),
	RIGHTS ("/rdf:RDF/rdf:Description/dc:rights"),
	USE_LIMITATION ("/rdf:RDF/rdf:Description/dc:rights"),
	MD_FORMAT ("/rdf:RDF/rdf:Description/dc:format"),
	SOURCE ("/rdf:RDF/rdf:Description/dc:source"),
	DATE_SOURCE_CREATION ("/rdf:RDF/rdf:Description/dc:date"),
	DATE_SOURCE_PUBLICATION ("/rdf:RDF/rdf:Description/dcterms:issued"),
	DATE_SOURCE_VALID_FROM ("/rdf:RDF/rdf:Description/dcterms:valid/start"),
	DATE_SOURCE_VALID_UNTIL ("/rdf:RDF/rdf:Description/dcterms:valid/end"),
	LOWER_CORNER ("/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:LowerCorner"),
	UPPER_CORNER ("/rdf:RDF/rdf:Description/ows:WGS84BoundingBox/ows:UpperCorner");

	private final String path;

	Path(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
