
public enum DcPath {
	UUID ("/rdf:RDF/rdf:Description/dc:identifier/text()"),
	TITLE ("/rdf:RDF/rdf:Description/dc:title/text()"),
	DATE ("/rdf:RDF/rdf:Description/dc:date/text()"),
	ORGANISATION_CREATOR ("/rdf:RDF/rdf:Description/dc:creator/text()"),
	ABSTRACT ("/rdf:RDF/rdf:Description/dc:description/text()"),
	
	LOCATION ("/rdf:RDF/rdf:Description/dcterms:references/text()"),
	NUMBER ("/rdf:RDF/rdf:Description/dcterms:relation/text()"),
	ATTACHMENT ("/rdf:RDF/rdf:Description/dc:relation/text()"),
	TYPE ("/rdf:RDF/rdf:Description/dc:type/text()"),
	PUBLISHER ("/rdf:RDF/rdf:Description/dc:publisher/text()"),
	CONTRIBUTOR ("/rdf:RDF/rdf:Description/dc:contributor/text()"),
	RIGHTS ("/rdf:RDF/rdf:Description/dc:rights/text()"),
	USE_LIMITATION ("/rdf:RDF/rdf:Description/dc:rights[@rdf:datatype='gebruiksrestricties'][1]/text()"),
	FORMAT ("/rdf:RDF/rdf:Description/dc:format/text()"),
	SOURCE ("/rdf:RDF/rdf:Description/dc:source/text()"),
	SUBJECT ("/rdf:RDF/rdf:Description/dc:subject/text()"),
	LANGUAGE ("/rdf:RDF/rdf:Description/dc:language/text()");
	
	private final String path;

	DcPath(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
