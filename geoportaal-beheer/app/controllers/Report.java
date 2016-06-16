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
import static models.QRole.role1;
import static models.QStatus.status;
import static models.QStatusLabel.statusLabel;
import static models.QSubject.subject;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;
import static models.QUser.user;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import com.querydsl.core.Tuple;

import actions.DefaultAuthenticator;
import play.data.Form;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;

@Security.Authenticated(DefaultAuthenticator.class)
public class Report extends Controller {
	@Inject QueryDSL q;
	@Inject WSClient ws;
	
	/**
	 * The rendering of the report page
	 * 
	 * @return the {@link Result} of the report page
	 */
	public Result renderReport() {
		return q.withTransaction(tx -> {
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			if(!roleId.equals(2)) {
				return ok(views.html.report.render(roleId));
			} else {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
		});
	}
	
	public Result getCSV() {
		Form<models.Report> reportForm = Form.form(models.Report.class);
		models.Report report = reportForm.bindFromRequest().get();
		
		if("dataset".equals(report.getTypeData()) || "download".equals(report.getTypeData())) {
			return redirect(routes.Report.writeOtherCSV(report.getTypeData()));
		} else if("dc".equals(report.getTypeData())) {
			return redirect(routes.Report.writeDcCSV());
		} else {
			return notFound();
		}
	}
	
	public Promise<Result> writeOtherCSV(String type) {
		WSRequest request = ws.url("http://sql-csv-" + type + ":9000").setFollowRedirects(true);
		
		LocalDate ld = LocalDate.now();
		response().setContentType("text/csv");
		
		if("dataset".equals(type)) {
			response().setHeader("Content-Disposition", "attachment; filename=\"rapport_geodata_" + ld.getYear() + ld.getMonthOfYear() + 
					ld.getDayOfMonth() + ".csv\"");
		} else if("download".equals(type)) {
			response().setHeader("Content-Disposition", "attachment; filename=\"rapport_download_" + ld.getYear() + ld.getMonthOfYear() + 
					ld.getDayOfMonth() + ".csv\"");
		}
		
		return request.get().map(response -> {
			return ok(response.getBodyAsStream());
		});
	}
	
	public Result writeDcCSV() throws Exception {
		LocalDate ld = LocalDate.now();
		
		response().setContentType("text/csv");
		response().setHeader("Content-Disposition", "attachment; filename=\"rapport_dublincore_" + ld.getYear() + ld.getMonthOfYear() + 
				ld.getDayOfMonth() + ".csv\"");
		
		String header = "\"title\";\"creator\";\"subject\";\"description\";\"date_creation\";\"date_publication\";\"date_valid_start\";"
				+ "\"date_valid_end\";\"type\";\"format\";\"identifier\";\"location\";\"number\";\"source\";\"attachment\";"
				+ "\"attachment_size_in_mb\";\"attachment_size_in_mb_total\";\"rights\";\"use_limitation\";\"supplier\";\"role_supplier\";"
				+ "\"status\";\"last_revision_user\";\"last_revision_date\";\"publisher\";\"contributor\";\"language\";\"west_bound\";"
				+ "\"east_bound\";\"south_bound\";\"north_bound\";";
		
		StringBuilder strb = new StringBuilder();
		strb.append(header);
		strb.append(System.lineSeparator());
		
		return q.withTransaction(tx -> {
			List<Tuple> mds =  tx.select(metadata.id, metadata.uuid, metadata.title, metadata.location, metadata.fileId, metadata.description, 
					metadata.creator, metadata.creatorOther, creatorLabel.label, metadata.dateSourceCreation, metadata.dateSourcePublication, 
					metadata.dateSourceValidFrom, metadata.dateSourceValidUntil, typeInformationLabel.label, mdFormatLabel.label, metadata.source, 
					rightsLabel.label, useLimitationLabel.label, statusLabel.label, user.label, metadata.lastRevisionUser, 
					metadata.lastRevisionDate, role1.role)
				.from(metadata)
				.join(creator).on(metadata.creator.eq(creator.id))
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.join(typeInformation).on(metadata.typeInformation.eq(typeInformation.id))
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.join(mdFormat).on(metadata.mdFormat.eq(mdFormat.id))
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.join(rights).on(metadata.rights.eq(rights.id))
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.join(useLimitation).on(metadata.useLimitation.eq(useLimitation.id))
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.join(status).on(metadata.status.eq(status.id))
				.join(statusLabel).on(status.id.eq(statusLabel.statusId))
				.join(user).on(metadata.supplier.eq(user.id))
				.join(role1).on(user.roleId.eq(role1.id))
				.fetch();
			
			Tuple constantValues = tx.select(constants.all())
				.from(constants)
				.fetchOne();
			
			for(Tuple md : mds) {
				strb.append("\"" + escapeQuotes(md.get(metadata.title)) + "\";");
				if(md.get(metadata.creator).equals(9)) {
					strb.append("\"" + escapeQuotes(md.get(metadata.creatorOther)) + "\";");
				} else {
					strb.append("\"" + escapeQuotes(md.get(creatorLabel.label)) + "\";");
				}
				
				List<Tuple> attachments = tx.select(mdAttachment.attachmentName, mdAttachment.attachmentLength)
						.from(mdAttachment)
						.where(mdAttachment.metadataId.eq(md.get(metadata.id)))
						.fetch();
				
				List<String> subjects = tx.select(subject.name)
					.from(mdSubject)
					.join(subject).on(mdSubject.subject.eq(subject.id))
					.where(mdSubject.metadataId.eq(md.get(metadata.id)))
					.fetch();
				
				strb.append("\"");
				for(Integer i = 0; i < subjects.size(); i++) {
					if(i.equals(subjects.size() - 1)) {
						strb.append(escapeQuotes(subjects.get(i)));
					} else {
						strb.append(escapeQuotes(subjects.get(i)) + " | ");
					}
				}
				strb.append("\";");
				
				strb.append("\"" + escapeQuotes(md.get(metadata.description).replaceAll("[\\t\\n\\r]", " ")) + "\";");
				
				strb.append("\"" + getValueDate(md.get(metadata.dateSourceCreation)) + "\";");
				strb.append("\"" + getValueDate(md.get(metadata.dateSourcePublication)) + "\";");
				strb.append("\"" + getValueDate(md.get(metadata.dateSourceValidFrom)) + "\";");
				strb.append("\"" + getValueDate(md.get(metadata.dateSourceValidUntil)) + "\";");
				
				strb.append("\"" + escapeQuotes(md.get(typeInformationLabel.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(mdFormatLabel.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(metadata.uuid)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(metadata.location)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(metadata.fileId)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(metadata.source)) + "\";");
				
				strb.append("\"");
				for(Integer i = 0; i < attachments.size(); i++) {
					if(i.equals(attachments.size() - 1)) {
						strb.append(escapeQuotes(attachments.get(i).get(mdAttachment.attachmentName)));
					} else {
						strb.append(escapeQuotes(attachments.get(i).get(mdAttachment.attachmentName)) + " | ");
					}
				}
				strb.append("\";");
				
				DecimalFormat df = new DecimalFormat("0.##");
				strb.append("\"");
				for(Integer i = 0; i < attachments.size(); i++) {
					if(i.equals(attachments.size() - 1)) {
						strb.append(df.format(attachments.get(i).get(mdAttachment.attachmentLength) / 1024.0 / 1024.0));
					} else {
						strb.append(df.format(attachments.get(i).get(mdAttachment.attachmentLength) / 1024.0 / 1024.0) + " | ");
					}
				}
				strb.append("\";");
				
				Double contentLengthTotal = 0.0;
				for(Integer i = 0; i < attachments.size(); i++) {
					contentLengthTotal += attachments.get(i).get(mdAttachment.attachmentLength);
				}
				
				if(contentLengthTotal.equals(0.0)) {
					strb.append("\"" + "" + "\";");
				} else {
					strb.append("\"" + df.format(contentLengthTotal / 1024.0 / 1024.0) + "\";");
				}
				
				strb.append("\"" + escapeQuotes(md.get(rightsLabel.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(useLimitationLabel.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(user.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(role1.role)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(statusLabel.label)) + "\";");
				strb.append("\"" + escapeQuotes(md.get(metadata.lastRevisionUser)) + "\";");
				strb.append("\"" + getValueDate(md.get(metadata.lastRevisionDate)) + "\";");
				
				strb.append("\"" + escapeQuotes(constantValues.get(constants.publisher)) + "\";");
				strb.append("\"" + escapeQuotes(constantValues.get(constants.contributor)) + "\";");
				strb.append("\"" + escapeQuotes(constantValues.get(constants.language)) + "\";");
				
				DecimalFormat dfBounds = new DecimalFormat("0.###");
				strb.append("\"" + dfBounds.format(constantValues.get(constants.westBoundLongitude)) + "\";");
				strb.append("\"" + dfBounds.format(constantValues.get(constants.eastBoundLongitude)) + "\";");
				strb.append("\"" + dfBounds.format(constantValues.get(constants.southBoundLongitude)) + "\";");
				strb.append("\"" + dfBounds.format(constantValues.get(constants.northBoundLongitude)) + "\";");
				
				strb.append(System.lineSeparator());
			}
			
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			if(!roleId.equals(2)) {
				return ok(strb.toString().getBytes());
			} else {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
		});
	}
	
	public String getValueDate(Timestamp ts) {
		if(ts != null) {
			LocalDate ld = LocalDate.fromDateFields(new Date(ts.getTime()));
			return ld.toString();
		} else {
			return "";
		}
	}
	
	public String escapeQuotes(String value) {
		if(value == null) {
			return "";
		} else {
			if(value.endsWith("\"")) {
				return value + "\"";
			} else {
				return value;
			}
		}
	}
}
