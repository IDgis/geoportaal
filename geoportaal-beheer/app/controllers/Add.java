package controllers;

import static models.QAttachment.attachment1;
import static models.QDataset.dataset;
import static models.QSubject.subject1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.sql.DataSource;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import models.DublinCore;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

public class Add extends Controller {
	DataSource ds = DB.getDataSource();
	
	public Result add() {
		Boolean create = true;
		
		String uuid = UUID.randomUUID().toString();
		Date dateToday = new Date();
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date(dateToday.getTime()));
		
		return ok(views.html.form.render(create, uuid, today, null, null));
	}
	
	public Result submit() throws ParseException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	Date dateToday = new Date();
    	
		Date dSC = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceCreation());
		Date dSP = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourcePublication());
		Date dSR = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceRevision());
		Date dSVF = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidFrom());
		Date dSVU = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidUntil());
		
		queryFactory.insert(dataset)
    		.columns(dataset.id, dataset.location, dataset.fileId, dataset.title, dataset.description, dataset.typeInfo,
    			dataset.creator, dataset.rights, dataset.useLimitation, dataset.format, dataset.source, dataset.dateSourceCreation,
    			dataset.dateSourcePublication, dataset.dateSourceRevision, dataset.dateSourceValidFrom, dataset.dateSourceValidUntil,
    			dataset.supplier, dataset.status, dataset.published, dataset.lastRevisionUser, dataset.lastRevisionDate)
    		.values(dc.getId(), dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getTypeInfo(), dc.getCreator(),
    				dc.getRights(), dc.getUseLimitation(), dc.getFormat(), dc.getSource(), dSC, dSP, dSR, dSVF, dSVU, 
    				"Nienhuis", "concept", false, "Nienhuis", dateToday)
    		.execute();
    	
		
		if(dc.getAttachment() != null) {
			for(String attachment : dc.getAttachment()) {
				queryFactory.insert(attachment1)
					.columns(attachment1.datasetId, attachment1.attachment)
					.values(dc.getId(), attachment)
					.execute();
			}
		}
		
		if(dc.getSubject() != null) {
			for(String subject : dc.getSubject()) {
				queryFactory.insert(subject1)
					.columns(subject1.datasetId, subject1.subject)
					.values(dc.getId(), subject)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result cancel() {

		return redirect(controllers.routes.Index.index());
	}
}