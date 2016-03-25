package controllers;

import static models.QConstants.constants;
import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QSubject.subject;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import models.DublinCoreXML;
import play.i18n.*;
import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;

/**
 * The class for the entity to generate XML of the DublinCore metadata
 * 
 * @author Sandro
 *
 */
public class MetadataXML extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * Generates an XML page according to the DublinCore standard
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @return the {@link Result} of the XML page
	 */
	public Result generateXml(String metadataUuid) {
		// Sets the content type of the response to XML
		response().setContentType("application/xml");
		
		// Create an object to easily format dates
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return q.withTransaction(tx -> {
			// Fetch the metadata record with all relevant information
			Tuple datasetRow = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, metadata.location, metadata.fileId, 
					typeInformationLabel.label, metadata.creator, creatorLabel.label, metadata.creatorOther, rightsLabel.label, useLimitationLabel.label, 
					mdFormatLabel.label, metadata.source, metadata.dateSourceCreation, metadata.dateSourcePublication, metadata.dateSourceValidFrom, 
					metadata.dateSourceValidUntil)
				.from(metadata)
				.join(creator).on(metadata.creator.eq(creator.id))
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.join(mdFormat).on(metadata.mdFormat.eq(mdFormat.id))
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.join(rights).on(metadata.rights.eq(rights.id))
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.join(typeInformation).on(metadata.typeInformation.eq(typeInformation.id))
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.join(useLimitation).on(metadata.useLimitation.eq(useLimitation.id))
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// Fetch the attachments
			List<String> attachments = tx.select(mdAttachment.attachmentName)
					.from(mdAttachment)
					.where(mdAttachment.metadataId.eq(datasetRow.get(metadata.id)))
					.orderBy(mdAttachment.attachmentName.asc())
					.fetch();
			
			// Fetch the id of the creator
			Integer creatorId = datasetRow.get(metadata.creator);
			
			/*
			 * If the creator of the record is 'other' the creator string will be set on the creator other field, otherwise it will be set
			 * on the creator label
			 */
			String creator;
			if(creatorId.equals(9)) {
				creator = datasetRow.get(metadata.creatorOther);
			} else {
				creator = datasetRow.get(creatorLabel.label);
			}
			
			// Sets the various dates if they aren't null
			Date d = null;
			if(datasetRow.get(metadata.dateSourceCreation) != null) {
				d = new Date(datasetRow.get(metadata.dateSourceCreation).getTime());
			}
			
			Date di = null;
			if(datasetRow.get(metadata.dateSourcePublication) !=null) {
				di = new Date(datasetRow.get(metadata.dateSourcePublication).getTime());
			}
			
			Date dvs = null;
			if(datasetRow.get(metadata.dateSourceValidFrom) != null) {
				dvs = new Date(datasetRow.get(metadata.dateSourceValidFrom).getTime());
			}
			
			Date dve = null;
			if(datasetRow.get(metadata.dateSourceValidUntil) != null) {
				dve = new Date(datasetRow.get(metadata.dateSourceValidUntil).getTime());
			}
			
			// Fetches the subjects
			List<String> subjects = tx.select(subject.name)
				.from(mdSubject)
				.join(subject).on(mdSubject.subject.eq(subject.id))
				.where(mdSubject.metadataId.eq(datasetRow.get(metadata.id)))
				.orderBy(subject.name.asc())
				.fetch();
			
			// Fetches the values of the constants table
			Tuple constantsRow = tx.select(constants.all())
					.from(constants)
					.fetchOne();
			
			// Formats the values of the coordinates in an upper corner and a lower corner
			String upperCorner = constantsRow.get(constants.northBoundLongitude) + " " + constantsRow.get(constants.westBoundLongitude);
			String lowerCorner = constantsRow.get(constants.southBoundLongitude) + " " + constantsRow.get(constants.eastBoundLongitude);
			
			// Creates a DublinCoreXML object
			DublinCoreXML dcx = new DublinCoreXML(
					datasetRow.get(metadata.uuid),
					datasetRow.get(metadata.title),
					datasetRow.get(metadata.description),
					datasetRow.get(metadata.location),
					datasetRow.get(metadata.fileId),
					attachments,
					datasetRow.get(typeInformationLabel.label),
					creator,
					constantsRow.get(constants.publisher),
					constantsRow.get(constants.contributor),
					datasetRow.get(rightsLabel.label),
					datasetRow.get(useLimitationLabel.label),
					datasetRow.get(mdFormatLabel.label),
					datasetRow.get(metadata.source),
					d,
					di,
					dvs,
					dve,
					subjects,
					constantsRow.get(constants.language),
					lowerCorner,
					upperCorner
			);
			
			// Fetches the message of the use limitation attribute value
			String useLimitation = Messages.get("xml.uselimitation");
			
			// Returns the XML page
			return ok(views.xml.metadata.render(dcx, sdf, useLimitation));
		});
	}
}
