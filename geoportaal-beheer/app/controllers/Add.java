package controllers;

import static models.QDataset.dataset;
import static models.QAttachment.attachment1;
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
		String uuid = UUID.randomUUID().toString();
		Date dateToday = new Date();
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date(dateToday.getTime()));

		return ok(views.html.addRecord.render(uuid, today));
	}
	
	public Result submit() throws ParseException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	Date dateToday = new Date();
    	
		Date dSC = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceCreation());
		String textDSC = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dSC.getTime()));
		
		Date dSP = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourcePublication());
		String textDSP = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dSP.getTime()));
		
		Date dSR = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceRevision());
		String textDSR = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dSR.getTime()));
		
		Date dSVF = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidFrom());
		String textDSVF = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dSVF.getTime()));
		
		Date dSVU = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidUntil());
		String textDSVU = new SimpleDateFormat("yyyy-MM-dd").format(new Date(dSVU.getTime()));
		
		System.out.println(textDSC);
		System.out.println(textDSP);
		System.out.println(textDSR);
		System.out.println(textDSVF);
		System.out.println(textDSVU);
		
		queryFactory.insert(dataset)
    		.columns(dataset.id, dataset.location, dataset.fileId, dataset.title, dataset.description, dataset.type,
    			dataset.creator, dataset.rights, dataset.useLimitation, dataset.format, dataset.source, dataset.dateSourceCreation,
    			dataset.dateSourcePublication, dataset.dateSourceRevision, dataset.dateSourceValidFrom, dataset.dateSourceValidUntil,
    			dataset.supplier, dataset.status, dataset.published, dataset.lastRevisionUser, dataset.lastRevisionDate)
    		.values(dc.getId(), dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getType(), dc.getCreator(),
    				dc.getRights(), dc.getUseLimitation(), dc.getFormat(), dc.getSource(), textDSC, textDSP, textDSR, textDSVF, textDSVU, 
    				"Nienhuis", "concept", "false", "Nienhuis", dateToday)
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