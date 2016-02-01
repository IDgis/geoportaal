package controllers;

import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QStatus.status;
import static models.QStatusLabel.statusLabel;
import static models.QSupplier.supplier;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import actions.DefaultAuthenticator;
import models.Search;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

@Security.Authenticated(DefaultAuthenticator.class)
public class Index extends Controller {
	@Inject ZooKeeper zk;
	@Inject Database db;
	
	public Result index() throws SQLException {
		List<Tuple> datasetRows = db.queryFactory
    			.select(metadata.id, metadata.uuid, metadata.title, metadata.lastRevisionDate, statusLabel.label, supplier.name)
    			.from(metadata)
    			.join(status).on(metadata.status.eq(status.id))
    			.join(supplier).on(metadata.supplier.eq(supplier.id))
    			.join(statusLabel).on(status.id.eq(statusLabel.statusId))
    			.limit(200)
    			.orderBy(metadata.lastRevisionDate.desc())
    			.fetch();
    	
    	List<Tuple> supplierList = db.queryFactory.select(supplier.all())
            	.from(supplier)
            	.fetch();
    	
    	List<Tuple> statusList = db.queryFactory
    			.select(status.name, statusLabel.label)
            	.from(status)
            	.join(statusLabel).on(status.id.eq(statusLabel.statusId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.name, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        return ok(views.html.index.render(datasetRows, supplierList, statusList, mdFormatList, sdfUS, sdfLocal, "", "none", "none", "none", null, null));
    }
	
	public Result changeStatus(Integer datasetId, String statusStr) {
		Integer statusKey = db.queryFactory.select(status.id)
			.from(status)
			.where(status.name.eq(statusStr))
			.fetchFirst();
		
		db.queryFactory.update(metadata)
    		.where(metadata.id.eq(datasetId))
    		.set(metadata.status, statusKey)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result changeSupplier(Integer datasetId, String supplierStr) {
		Integer supplierKey = db.queryFactory.select(supplier.id)
			.from(supplier)
			.where(supplier.name.eq(supplierStr))
			.fetchFirst();
		
		db.queryFactory.update(metadata)
    		.where(metadata.id.eq(datasetId))
    		.set(metadata.supplier, supplierKey)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result deleteMetadata(Integer datasetId) {
		db.queryFactory.delete(metadata)
    		.where(metadata.id.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(mdAttachment)
    		.where(mdAttachment.metadataId.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(mdSubject)
    		.where(mdSubject.metadataId.eq(datasetId))
    		.execute();
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result validateForm() {
		
		try {
			DynamicForm requestData = Form.form().bindFromRequest();
			String dateSearchStart = requestData.get("dateUpdateStart");
			String dateSearchEnd = requestData.get("dateUpdateEnd");
			
			Boolean dateSearchStartReturn = validateDate(dateSearchStart);
			Boolean dateSearchEndReturn = validateDate(dateSearchEnd);
			
			String dateSearchStartMsg = null;
			String dateSearchEndMsg = null;
			
			if(!dateSearchStartReturn || !dateSearchEndReturn) {
				if(!dateSearchStartReturn) {
					dateSearchStartMsg = "De update datum, van is niet correct ingevuld.";
				} else {
					dateSearchStartMsg = null;
				}
				
				if(!dateSearchEndReturn) {
					dateSearchEndMsg = "De update datum, tot is niet correct ingevuld.";
				} else {
					dateSearchEndMsg = null;
				}
			}
			
			return ok(bindingerror.render(null, null, null, null, null, null, dateSearchStartMsg, dateSearchEndMsg));
		} catch(IllegalStateException ise) {
				return ok(bindingerror.render("Er is iets misgegaan. Controleer of de velden correct zijn ingevuld.", null, null, null, null, null, null, null));
			}
	}
	
	public Result search() {
		Form<Search> searchForm = Form.form(Search.class);
		Search s = searchForm.bindFromRequest().get();
		String textSearch = s.getText();
		String supplierSearch = s.getSupplier();
		String statusSearch = s.getStatus();
		String mdFormatSearch = s.getFormat();
		Date dateStartSearch = s.getDateUpdateStart();
		Date dateEndSearch = s.getDateUpdateEnd();
		
		SQLQuery<Tuple> datasetQuery = db.queryFactory
    			.select(metadata.id, metadata.uuid, metadata.title, metadata.lastRevisionDate, statusLabel.label, supplier.name, status.name, mdFormat.name)
    			.from(metadata)
    			.join(status).on(metadata.status.eq(status.id))
    			.join(supplier).on(metadata.supplier.eq(supplier.id))
    			.join(statusLabel).on(status.id.eq(statusLabel.statusId))
    			.join(mdFormat).on(metadata.mdFormat.eq(mdFormat.id));
		
		if(!textSearch.equals("")) {
			datasetQuery
				.where(metadata.title.containsIgnoreCase(textSearch)
						.or(metadata.description.containsIgnoreCase(textSearch)));
		}
		
		if(!supplierSearch.equals("none")) {
			datasetQuery
				.where(supplier.name.eq(supplierSearch));
		}
		
		if(!statusSearch.equals("none")) {
			datasetQuery
				.where(status.name.eq(statusSearch));
		}
		
		if(!mdFormatSearch.equals("none")) {
			datasetQuery
				.where(mdFormat.name.eq(mdFormatSearch));
		}
		
		Timestamp timestampStartSearch = null;
		Timestamp timestampEndSearch = null;
		if(dateStartSearch != null && dateEndSearch != null) {
			timestampStartSearch = new Timestamp(dateStartSearch.getTime());
			timestampEndSearch = new Timestamp(dateEndSearch.getTime() + 86400000);
			
			datasetQuery
				.where(metadata.lastRevisionDate.after(timestampStartSearch))
				.where(metadata.lastRevisionDate.before(timestampEndSearch));
		}
		
		List<Tuple> datasetRows = datasetQuery
    			.limit(200)
				.orderBy(metadata.lastRevisionDate.desc())
    			.fetch();
    	
    	List<Tuple> supplierList = db.queryFactory.select(supplier.all())
            	.from(supplier)
            	.fetch();
    	
    	List<Tuple> statusList = db.queryFactory
    			.select(status.name, statusLabel.label)
            	.from(status)
            	.join(statusLabel).on(status.id.eq(statusLabel.statusId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.name, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        Timestamp resetTimestampEndSearch = null;
        if(dateEndSearch != null) {
        	resetTimestampEndSearch = new Timestamp(dateEndSearch.getTime());
        }
    	
		return ok(views.html.index.render(datasetRows, supplierList, statusList, mdFormatList, sdfUS, sdfLocal, textSearch, 
				supplierSearch, statusSearch, mdFormatSearch, timestampStartSearch, resetTimestampEndSearch));
	}
	
	public Boolean validateDate(String date) {
		if(date.equals("")) {
			return true;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			sdf.parse(date);
			return true;
		} catch(ParseException pe) {
			return false;
		}
	}
	
	public Result jsRoutes() {
		return ok (Routes.javascriptRouter ("jsRoutes",
            controllers.routes.javascript.Assets.versioned(),
            controllers.routes.javascript.Index.deleteMetadata(),
			controllers.routes.javascript.Index.changeStatus(),
			controllers.routes.javascript.Index.changeSupplier(),
			controllers.routes.javascript.MetadataDC.validateForm(),
			controllers.routes.javascript.Index.validateForm()
        )).as ("text/javascript");
    }
}