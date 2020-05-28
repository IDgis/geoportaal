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
import static models.QStatus.status;
import static models.QSubject.subject;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import enums.OriginRequest;
import models.DublinCoreXML;
import nl.idgis.dav.model.DefaultResource;
import nl.idgis.dav.model.DefaultResourceDescription;
import nl.idgis.dav.model.DefaultResourceProperties;
import nl.idgis.dav.model.Resource;
import nl.idgis.dav.model.ResourceDescription;
import nl.idgis.dav.model.ResourceProperties;
import nl.idgis.dav.router.SimpleWebDAV;
import play.Configuration;
import play.i18n.Messages;
import play.mvc.Http;
import play.twirl.api.Html;
import util.QueryDSL;

public class DublinCoreMetadata extends SimpleWebDAV {
	private final QueryDSL q;
	private final Configuration configuration = play.Play.application().configuration();
	
	@Inject
	public DublinCoreMetadata(QueryDSL q) {
		this(q, "/");
	}
	
	public DublinCoreMetadata(QueryDSL q, String prefix) {
		super(prefix);
		
		this.q = q;
	}
	
	@Override
	public DublinCoreMetadata withPrefix(String prefix) {
		return new DublinCoreMetadata(q, prefix);
	}
	
	/**
	 * Generates a list of all UUID's
	 * 
	 * @return a {@link Stream} of {@link ResourceDescription} of the UUID's
	 */
	@Override
	public Stream<ResourceDescription> descriptions() {
		return q.withTransaction(tx -> {
			return tx.select(
					metadata.uuid, 
					metadata.dateSourceCreation)
				.from(metadata)
				.join(status).on(metadata.status.eq(status.id))
				.where(status.name.eq("published"))
				.orderBy(metadata.id.asc())
				.fetch()
				.stream()
				.map(dataset -> new DefaultResourceDescription(dataset.get(metadata.uuid) + ".xml", 
					new DefaultResourceProperties(false, dataset.get(metadata.dateSourceCreation))));
		});
	}
	
	/**
	 * Provides the properties of the metadata document
	 *
	 * @param name the UUID of the metadata
	 * @return a {@link Optional} with the {@link ResourceProperties} of the metadata
	 */
	@Override
	public Optional<ResourceProperties> properties(String name) {
		return q.withTransaction(tx -> {
			Optional<Date> optionalDate = Optional.ofNullable(
				tx.select(metadata.dateSourceCreation)
					.from(metadata)
					.where(metadata.uuid.eq(name))
					.fetchOne());
					
			return optionalDate.map(date -> new DefaultResourceProperties(false, date));
		});
	}
	
	private DublinCoreXML generateMetadata(String name, OriginRequest origin) {
		// Strips the extension from the string
		String metadataUuid = name.substring(0, name.indexOf(".xml"));
		
		return (DublinCoreXML) q.withTransaction(tx -> {
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
			
			// When the row is null return an empty optional
			if(datasetRow == null) return Optional.<Resource>empty();
			
			// Fetch the attachments from the database
			List<String> attachmentsDB = tx.select(mdAttachment.attachmentName)
					.from(mdAttachment)
					.where(mdAttachment.metadataId.eq(datasetRow.get(metadata.id)))
					.orderBy(mdAttachment.attachmentName.asc())
					.fetch();
			
			// Convert attachments from database to url's
			List<String> attachments = new ArrayList<String>();
			for(String att : attachmentsDB) {
				String url = null;
				switch(origin) {
					case ADMIN:
						url = configuration.getString("geoportaal.adminHost") + controllers.routes.Attachment.download(datasetRow.get(metadata.uuid), att).toString();
						break;
					case INTERNAL:
						url = configuration.getString("geoportaal.attachmentPrefixInternal") + datasetRow.get(metadata.uuid) + "/" + att.replaceAll(" ", "%20");
						break;
					case EXTERNAL:
						url = configuration.getString("geoportaal.attachmentPrefixExternal") + datasetRow.get(metadata.uuid) + "/" + att.replaceAll(" ", "%20");
						break;
				}
				
				if(url != null) attachments.add(url);
			}
			
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
			
			return dcx;
		});
	}
	
	/**
	 * Generates an XML page according to the DublinCore standard
	 * 
	 * @param name the UUID of the metadata
	 * @return the {@link Optional} with the {@link Resource} which contains 
	 * the content and content type of the XML page
	 */
	@Override
	public Optional<Resource> resource(String name) throws MalformedURLException, IOException {
		// Strips the extension from the string
		String metadataUuid = name.substring(0, name.indexOf(".xml"));
		
		return q.withTransaction(tx -> {
			// Get use limitation of document
			String useLimitationDocument = tx.select(useLimitation.name)
				.from(metadata)
				.join(useLimitation).on(metadata.useLimitation.eq(useLimitation.id))
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// Get value of trusted header
			String headerTrusted = Http.Context.current().request().getHeader(configuration.getString("trusted.header"));
			
			// Check if user is allowed access to document
			if(!"1".equals(headerTrusted) && !"extern".equals(useLimitationDocument)) {
				return Optional.<Resource>of(new DefaultResource("text/plain", "403: forbidden".getBytes("UTF-8")));
			}
			
			// Generate metadata
			DublinCoreXML dcx = generateMetadata(name, "1".equals(headerTrusted) ? OriginRequest.INTERNAL : OriginRequest.EXTERNAL);
			
			// Create an object to easily format dates
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			// Fetches the message of the use limitation attribute value
			String useLimitation = Messages.get("xml.uselimitation");
			
			String stylesheetIntern = configuration.getString("geoportaal.stylesheet.intern.url");
			String stylesheetExtern = configuration.getString("geoportaal.stylesheet.extern.url");
			
			// Returns the XML page
			if("1".equals(headerTrusted)) {
				return Optional.<Resource>of(new DefaultResource("application/xml", 
						views.xml.metadataintern.render(dcx, sdf, useLimitation, false, stylesheetIntern).body().getBytes("UTF-8")));
			} else {
				return Optional.<Resource>of(new DefaultResource("application/xml", 
						views.xml.metadataextern.render(dcx, sdf, useLimitation, false, stylesheetExtern).body().getBytes("UTF-8")));
			}
		});
	}
	
	public Html getMetadataInternal(String name, boolean noStyle) throws MalformedURLException, IOException {
		// Generate metadata
		DublinCoreXML dcx = generateMetadata(name, OriginRequest.ADMIN);
		
		String stylesheet = configuration.getString("geoportaal.stylesheet.intern.url");
		
		// Create an object to easily format dates
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		// Fetches the message of the use limitation attribute value
		String useLimitation = Messages.get("xml.uselimitation");
		
		// Returns the XML page
		return views.xml.metadataintern.render(dcx, sdf, useLimitation, noStyle, stylesheet);
	}
}