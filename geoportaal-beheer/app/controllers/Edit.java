package controllers;

import static models.QDataset.dataset;
import static models.QInfoFormats.infoFormats;
import static models.QRights.rights;
import static models.QSubjects.subjects;
import static models.QTypeInformations.typeInformations;
import static models.QUseLimitations.useLimitations;
import static models.QCreators.creators;
import static models.QDataSubject.dataSubject;

import java.util.List;

import javax.sql.DataSource;

import com.querydsl.core.Tuple;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

public class Edit extends Controller {
	DataSource ds = DB.getDataSource();
	
	public Result edit(String datasetId) {
		Boolean create = false;
		
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	Tuple datasetRow = queryFactory.select(dataset.id, dataset.location, dataset.fileId, dataset.title, dataset.description,
    			dataset.typeInfo, dataset.creator, dataset.rights, dataset.useLimitation, dataset.format, dataset.source, 
    			dataset.dateSourceCreation.dayOfMonth(), dataset.dateSourceCreation.month(), dataset.dateSourceCreation.year(),
    			dataset.dateSourcePublication.dayOfMonth(), dataset.dateSourcePublication.month(), dataset.dateSourcePublication.year(),
    			dataset.dateSourceRevision.dayOfMonth(), dataset.dateSourceRevision.month(), dataset.dateSourceRevision.year(),
    			dataset.dateSourceValidFrom.dayOfMonth(), dataset.dateSourceValidFrom.month(), dataset.dateSourceValidFrom.year(),
    			dataset.dateSourceValidUntil.dayOfMonth(), dataset.dateSourceValidUntil.month(), dataset.dateSourceValidUntil.year())
    			.from(dataset)
    			.where(dataset.id.eq(datasetId))
    			.fetchFirst();
    	
    	List<String> subjectsDataset = queryFactory.select(dataSubject.subject)
    			.from(dataSubject)
    			.where(dataSubject.datasetId.eq(datasetId))
    			.fetch();
    	
    	List<Tuple> typeInformationList = queryFactory.select(typeInformations.identification, typeInformations.label)
        		.from(typeInformations)
        		.fetch();
        	
    	List<Tuple> creatorsList = queryFactory.select(creators.identification, creators.label)
            	.from(creators)
            	.fetch();
        	
       	List<Tuple> rightsList = queryFactory.select(rights.identification, rights.label)
               	.from(rights)
               	.fetch();
        	
       	List<Tuple> useLimitationList = queryFactory.select(useLimitations.identification, useLimitations.label)
               	.from(useLimitations)
               	.fetch();
        	
        List<Tuple> infoFormatList = queryFactory.select(infoFormats.identification, infoFormats.label)
               	.from(infoFormats)
               	.fetch();
        	
        List<Tuple> subjectList = queryFactory.select(subjects.identification, subjects.label)
               	.from(subjects)
               	.fetch();
    	
    	return ok(views.html.form.render(create, "", "", datasetRow, subjectsDataset, typeInformationList, creatorsList, rightsList, 
    			useLimitationList, infoFormatList, subjectList));
	}
	
	public Result changeStatus(String datasetId, String status) {
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.status, status)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result changeSupplier(String datasetId, String supplier) {
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.supplier, supplier)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
}
