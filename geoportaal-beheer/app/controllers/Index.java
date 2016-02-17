package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QMetadataSearch.metadataSearch;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QStatus.status;
import static models.QStatusLabel.statusLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QSupplier.supplier;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;
import static models.QUser.user;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQuery;

import actions.DefaultAuthenticator;
import models.Delete;
import models.Search;
import models.Supplier;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;
import views.html.*;

@Security.Authenticated(DefaultAuthenticator.class)
public class Index extends Controller {
	@Inject ZooKeeper zk;
	@Inject QueryDSL q;
	
	public Result index(String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, String dateStartSearch, 
			String dateEndSearch) throws SQLException {
		return q.withTransaction(tx -> {
			List<Tuple> supplierList = tx.select(supplier.all())
	            	.from(supplier)
	            	.orderBy(supplier.name.asc())
	            	.fetch();
	    	
	    	List<Tuple> statusList = tx.select(status.name, statusLabel.label)
	            	.from(status)
	            	.join(statusLabel).on(status.id.eq(statusLabel.statusId))
	            	.fetch();
	    	
	    	List<Tuple> mdFormatList = tx.select(mdFormat.name, mdFormatLabel.label)
	            	.from(mdFormat)
	            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
	            	.fetch();
	    	
	    	SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
	        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
	        
	        Integer roleId = tx.select(user.roleId)
        			.from(user)
        			.where(user.username.eq(session("username")))
        			.fetchOne();
        	 
        	Integer supplierId = tx.select(user.id)
        			.from(user)
        			.where(user.username.eq(session("username")))
        			.fetchOne();
	        
	        if(textSearch == null && supplierSearch == null && statusSearch == null && mdFormatSearch == null && dateStartSearch == null && dateEndSearch == null) {
	        	SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.status, metadata.lastRevisionDate, statusLabel.label, supplier.name)
		    			.from(metadata)
		    			.join(status).on(metadata.status.eq(status.id))
		    			.join(supplier).on(metadata.supplier.eq(supplier.id))
		    			.join(statusLabel).on(status.id.eq(statusLabel.statusId));
	        	
	        	if(roleId.equals(2)) {
	        		datasetQuery.where(metadata.supplier.eq(supplierId));
	        	}
	        	
	        	List<Tuple> datasetRows = datasetQuery.where(metadata.status.notIn(5))
		    			.limit(200)
		    			.orderBy(metadata.lastRevisionDate.desc())
		    			.fetch();
		    	
		    	return ok(views.html.index.render(datasetRows, supplierList, statusList, mdFormatList, sdfUS, sdfLocal, roleId, "", "none", "none", "none", null, null));
	        } else {
	        	SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.status, metadata.lastRevisionDate, statusLabel.label, supplier.name, 
						status.name, mdFormat.name)
		    			.from(metadata)
		    			.join(status).on(metadata.status.eq(status.id))
		    			.join(supplier).on(metadata.supplier.eq(supplier.id))
		    			.join(statusLabel).on(status.id.eq(statusLabel.statusId))
		    			.join(mdFormat).on(metadata.mdFormat.eq(mdFormat.id));
	        	
	        	String[] textSearchTerms = textSearch.split("\\s+");
	        	if(textSearchTerms.length > 0) {
	        		String tsQuery = 
	        			Arrays.asList(textSearchTerms).stream()
	        				.filter(str -> !str.isEmpty())
	        				.collect(Collectors.joining(" | "));
	        		
	        		if(!tsQuery.isEmpty()) {	        		
		        		datasetQuery.where(
		        			tx.selectOne()
		        				.from(metadataSearch)
		        				.where(metadataSearch.metadataId.eq(metadata.id))
		        				.where(metadataSearch.tsv.query(tsQuery))
		        				.exists());
		        		
		        		// TODO: ranking?
	        		}
	        	}
	        	
				if(!"none".equals(supplierSearch)) {
					datasetQuery
						.where(supplier.name.eq(supplierSearch));
				}
				
				if(!"none".equals(statusSearch)) {
					datasetQuery
						.where(status.name.eq(statusSearch));
				}
				
				if("none".equals(statusSearch)) {
					datasetQuery.where(metadata.status.notIn(5));
				}
				
				if(roleId.equals(2) && "deleted".equals(statusSearch)) {
					datasetQuery.where(metadata.status.notIn(5));
				}
				
				if(!"none".equals(mdFormatSearch)) {
					datasetQuery
						.where(mdFormat.name.eq(mdFormatSearch));
				}
				
				Date finalDateStartSearch = null;
				if(!"".equals(dateStartSearch)) {
					finalDateStartSearch = sdfUS.parse(dateStartSearch);
				}
				
				Date finalDateEndSearch = null;
				if(!"".equals(dateEndSearch)) {
					finalDateEndSearch = sdfUS.parse(dateEndSearch);
				}
				
				Timestamp timestampStartSearch = null;
				Timestamp timestampEndSearch = null;
				if(finalDateStartSearch != null && finalDateEndSearch != null) {
					timestampStartSearch = new Timestamp(finalDateStartSearch.getTime());
					timestampEndSearch = new Timestamp(finalDateEndSearch.getTime() + 86400000);
					
					datasetQuery
						.where(metadata.lastRevisionDate.after(timestampStartSearch))
						.where(metadata.lastRevisionDate.before(timestampEndSearch));
				}
				
				if(roleId.equals(2)) {
	        		datasetQuery.where(metadata.supplier.eq(supplierId));
	        	}
				
				List<Tuple> datasetRows = datasetQuery
		    			.limit(200)
						.orderBy(metadata.lastRevisionDate.desc())
		    			.fetch();
				
				Timestamp resetTimestampEndSearch = null;
		        if(finalDateEndSearch != null) {
		        	resetTimestampEndSearch = new Timestamp(finalDateEndSearch.getTime());
		        }
		        
		        return ok(views.html.index.render(datasetRows, supplierList, statusList, mdFormatList, sdfUS, sdfLocal, roleId, textSearch, 
		        		supplierSearch, statusSearch, mdFormatSearch, timestampStartSearch, resetTimestampEndSearch));
	        }
		});
    }
	
	public Result search() {
		Form<Search> searchForm = Form.form(Search.class);
		Search s = searchForm.bindFromRequest().get();
		String textSearch = s.getText();
		String supplierSearch = s.getSupplier();
		String statusSearch = s.getStatus();
		String mdFormatSearch = s.getFormat();
		String dateStartSearch = s.getDateUpdateStart();
		String dateEndSearch = s.getDateUpdateEnd();
		
		if("".equals(dateStartSearch) || "".equals(dateEndSearch)) {
			dateStartSearch = "";
			dateEndSearch = "";
		}
		
		return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch));
	}
	
	public Result changeStatus() {
		Form<models.Status> statusForm = Form.form(models.Status.class);
		models.Status s = statusForm.bindFromRequest().get();
		List<String> changeRecords = s.getRecordsChange();
		String statusName = s.getStatus();
		
		return q.withTransaction(tx -> {
			Integer roleId = tx.select(user.roleId)
	    			.from(user)
	    			.where(user.username.eq(session("username")))
	    			.fetchOne();
			
			Integer userId = tx.select(user.id)
        			.from(user)
        			.where(user.username.eq(session("username")))
        			.fetchOne();
	    	
			List<String> finalChangeRecords = new ArrayList<String>();
			if(roleId.equals(2)) {
	    		if(changeRecords != null) {
	    			for(String record : changeRecords) {
		    			Integer statusId = tx.select(metadata.status)
			    			.from(metadata)
			    			.where(metadata.uuid.eq(record))
			    			.fetchOne();
		    			
		    			Integer supplierId = tx.select(metadata.supplier)
			    				.from(metadata)
			    				.where(metadata.uuid.eq(record))
			    				.fetchOne();
		    			
		    			if(!statusId.equals(4) && userId.equals(supplierId)) {
		    				finalChangeRecords.add(record);
		    			}
		    		}
	    		}
	    	} else {
	    		if(changeRecords != null) {
		    		for(String record : changeRecords) {
		    			finalChangeRecords.add(record);
		    		}
	    		}
	    	}
			
			if(finalChangeRecords != null && statusName != null) {
				Integer statusKey = tx.select(status.id)
					.from(status)
					.where(status.name.eq(statusName))
					.fetchOne();
				
				if(statusKey != null) {
					if(roleId.equals(2) && "published".equals(statusName)) {
						// do nothing
					} else {
						Long count = tx.update(metadata)
				    		.where(metadata.uuid.in(finalChangeRecords))
				    		.set(metadata.status, statusKey)
				    		.execute();
						
						Integer finalCount = count.intValue();
						if(!finalCount.equals(finalChangeRecords.size())) {
							throw new Exception("Changing status: different amount of affected rows than expected");
						}
					}
				}
			}
	    	
			return redirect(controllers.routes.Index.index(null, null, null, null, null, null));
		});
		
	}
	
	public Result changeSupplier() {
		Form<Supplier> supplierForm = Form.form(Supplier.class);
		Supplier s = supplierForm.bindFromRequest().get();
		List<String> changeRecords = s.getRecordsChange();
		String supplierName = s.getSupplier();
		
		return q.withTransaction(tx -> {
			if(changeRecords != null && supplierName != null) {
				Integer supplierKey = tx.select(supplier.id)
					.from(supplier)
					.where(supplier.name.eq(supplierName))
					.fetchOne();
				
				if(supplierKey != null) {
					Long count = tx.update(metadata)
			    		.where(metadata.uuid.in(changeRecords))
			    		.set(metadata.supplier, supplierKey)
			    		.execute();
			    	
					Integer finalCount = count.intValue();
					if(!finalCount.equals(changeRecords.size())) {
						throw new Exception("Changing supplier: different amount of affected rows than expected");
					}
				}
			}
			
			return redirect(controllers.routes.Index.index(null, null, null, null, null, null));
		});
	}
	
	public Result deleteMetadata() {
		Form<Delete> deleteForm = Form.form(Delete.class);
		Delete d = deleteForm.bindFromRequest().get();
		List<String> deleteRecords = d.getRecordsToDel();
		String permDel = d.getPermDel();
		
		return q.withTransaction(tx -> {
			Integer roleId = tx.select(user.roleId)
	    			.from(user)
	    			.where(user.username.eq(session("username")))
	    			.fetchOne();
			
			Integer userId = tx.select(user.id)
        			.from(user)
        			.where(user.username.eq(session("username")))
        			.fetchOne();
	    	
			List<String> finalDeleteRecords = new ArrayList<String>();
			if(roleId.equals(2)) {
	    		if(deleteRecords != null) {
	    			for(String record : deleteRecords) {
		    			Integer statusId = tx.select(metadata.status)
			    			.from(metadata)
			    			.where(metadata.uuid.eq(record))
			    			.fetchOne();
		    			
		    			Integer supplierId = tx.select(metadata.supplier)
		    				.from(metadata)
		    				.where(metadata.uuid.eq(record))
		    				.fetchOne();
		    			
		    			if(!statusId.equals(4) && userId.equals(supplierId)) {
		    				finalDeleteRecords.add(record);
		    			}
		    		}
	    		}
	    	} else {
	    		if(deleteRecords != null) {
		    		for(String record : deleteRecords) {
		    			finalDeleteRecords.add(record);
		    		}
	    		}
	    	}
			
			if(finalDeleteRecords != null) {
				if(permDel != null) {
					Long count = tx.delete(metadata)
						.where(metadata.uuid.in(finalDeleteRecords))
						.execute();
					
					Integer finalCount = count.intValue();
					if(!finalCount.equals(finalDeleteRecords.size())) {
						throw new Exception("Deleting records: different amount of affected rows than expected");
					}
				} else {
					Long count = tx.update(metadata)
						.where(metadata.uuid.in(finalDeleteRecords))
						.set(metadata.status, 5)
						.execute();
					
					Integer finalCount = count.intValue();
					if(!finalCount.equals(finalDeleteRecords.size())) {
						throw new Exception("Change status to deleted: different amount of affected rows than expected");
					}
				}
			}
			
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			return redirect(controllers.routes.Index.index(null, null, null, null, null, null));
		});
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
	
	public Boolean validateDate(String date) {
		if("".equals(date)) {
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
			controllers.routes.javascript.Metadata.validateForm(),
			controllers.routes.javascript.Index.validateForm()
        )).as ("text/javascript");
    }
}