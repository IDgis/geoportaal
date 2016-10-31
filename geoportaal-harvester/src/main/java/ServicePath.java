
public enum ServicePath {
	UUID ("/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString/text()"),
	TITLE ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString/text()"),
	DATE ("/gmd:MD_Metadata/gmd:dateStamp/gco:Date/text()"),
	ORGANISATION_CREATOR ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:pointOfContact[1]/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString/text()"),
	ABSTRACT ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:abstract/gco:CharacterString/text()"),
	TYPE_SERVICE ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType[1]/gco:LocalName/text()"),
	
	USE_LIMITATION ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString/text()"),
	ACCESS_CONSTRAINT ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue"),
	OTHER_CONSTRAINT ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString/text()"),
	INDIVIDUAL_NAME_CREATOR ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString/text()"),
	ORGANISATION_CONTACT ("/gmd:MD_Metadata/gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString/text()"),
	INDIVIDUAL_NAME_CONTACT ("/gmd:MD_Metadata/gmd:contact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString/text()"),
	GEO_AREA ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:description/gco:CharacterString/text()"),
	LAYER ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:coupledResource/srv:SV_CoupledResource/gco:ScopedName/text()"),
	ATTACHED_FILE ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/srv:operatesOn/@uuidref"),
	KEYWORD ("/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString/text()");
	
	private final String path;

	ServicePath(String path) {
		this.path = path;
	}

	public String path() {
		return path;
	}
}
