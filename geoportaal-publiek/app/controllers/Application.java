package controllers;

import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QMdType.mdType;
import static models.QMdTypeLabel.mdTypeLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.swing.text.Document;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.WindowOver;

import models.DocSubject;
import play.Routes;
import play.i18n.Lang;
import play.mvc.*;
import util.QueryDSL;
import views.html.*;

public class Application extends Controller {
	@Inject QueryDSL q;
	
	public Result index() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			List<Tuple> documents = tx.select(document.title, document.uuid, document.date, document.creator, document.description, 
					document.thumbnail, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.orderBy(document.date.desc())
					.limit(5)
					.fetch();
			
			return ok(index.render(documents, sdf));
		});
	}
	
	public Result search(Integer start, String typesString, Boolean filter) {
		Lang curLang = Http.Context.current().lang();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] typesArray = typesString.split("\\++");
		List<String> types = Arrays.asList(typesArray);
		
		return q.withTransaction(tx -> {
			List<Tuple> mdTypes = tx.select(mdType.name, mdTypeLabel.title)
					.from(mdType)
					.join(mdTypeLabel).on(mdType.id.eq(mdTypeLabel.mdTypeId))
					.where(mdTypeLabel.language.eq(curLang.code()))
					.fetch();
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, document.date, document.creator, document.description, 
					document.thumbnail, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.where(document.description.isNotNull())
					.where(mdType.name.in(types));
			
			Integer count = queryDocuments
					.fetch()
					.size();
			
			Integer startNext = start + 10;
			Integer startPrevious = start -10;
			
			if(startPrevious < 0) {
				startPrevious = 0;
			}
			
			Integer startLast = count - (count % 10);
			if(count % 10 == 0) {
				startLast -= 10;
			}
			
			Integer pageLast = startLast / 10 + 1;
			
			Integer finalStart = start;
			if(start >= count) {
				finalStart = count - (count % 10);
				if(count % 10 == 0) {
					finalStart -= 10;
				}
				
				startNext = finalStart + 10;
				startPrevious = finalStart -10;
				
				if(finalStart < 0) {
					finalStart = 0;
				}
			}
			
			List<Tuple> documents = queryDocuments.orderBy(document.date.desc(), document.title.asc())
					.offset(finalStart)
					.limit(10)
					.fetch();
			
			if(filter) {
				return ok(searchresult.render(mdTypes, documents, sdf, typesString, count, finalStart, startPrevious, startNext, startLast, pageLast));
			}
			
			return ok(search.render(mdTypes, documents, sdf, typesString, count, finalStart, startPrevious, startNext, startLast, pageLast));
		});
	}
	
	public Result browse(Integer start, String subjectsString, Boolean filter) {
		Lang curLang = Http.Context.current().lang();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] subjectsArray = subjectsString.split("\\++");
		List<String> subjectsList = Arrays.asList(subjectsArray);
		
		return q.withTransaction(tx -> {
			List<Tuple> subjects = tx.select(subject.name, subjectLabel.title)
					.from(subject)
					.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
					.where(subjectLabel.language.eq(curLang.code()))
					.fetch();
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.uuid, document.title, document.date, document.creator, document.description, 
					document.thumbnail, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.where(document.description.isNotNull())
					.where(document.id.in(SQLExpressions.select(docSubject.documentId)
							.from(docSubject)
							.join(subject).on(docSubject.subjectId.eq(subject.id))
							.where(subject.name.in(subjectsList))));
			
			Integer count = queryDocuments
					.fetch()
					.size();
			
			Integer startNext = start + 10;
			Integer startPrevious = start -10;
			
			if(startPrevious < 0) {
				startPrevious = 0;
			}
			
			Integer startLast = count - (count % 10);
			if(count % 10 == 0) {
				startLast -= 10;
			}
			
			Integer pageLast = startLast / 10 + 1;
			
			Integer finalStart = start;
			if(start >= count) {
				finalStart = count - (count % 10);
				if(count % 10 == 0) {
					finalStart -= 10;
				}
				
				startNext = finalStart + 10;
				startPrevious = finalStart -10;
				
				if(finalStart < 0) {
					finalStart = 0;
				}
			}
			
			List<Tuple> documents = queryDocuments.orderBy(document.date.desc(), document.title.asc())
					.offset(finalStart)
					.limit(10)
					.fetch();
			
			List<DocSubject> finalDocuments = new ArrayList<>();
			for(Tuple doc : documents) {
				List<String> docSubjects = tx.select(subject.name)
						.from(document)
						.join(docSubject).on(document.id.eq(docSubject.documentId))
						.join(subject).on(docSubject.subjectId.eq(subject.id))
						.where(document.uuid.eq(doc.get(document.uuid)))
						.orderBy(subject.id.asc())
						.fetch();
				
				DocSubject ds = new DocSubject(doc.get(document.uuid), doc.get(document.title), doc.get(document.date), doc.get(document.creator), 
						doc.get(document.description), doc.get(document.thumbnail), docSubjects, doc.get(mdType.url), doc.get(mdType.name));
				
				finalDocuments.add(ds);
			}
			
			if(filter) {
				return ok(browseresult.render(subjects, finalDocuments, sdf, subjectsString, count, finalStart, startPrevious, startNext, startLast, pageLast));
			}
			
			return ok(browse.render(subjects, finalDocuments, sdf, subjectsString, count, finalStart, startPrevious, startNext, startLast, pageLast));
		});
	}
	
	public Result about() {
		return ok(about.render());
	}
	
	public Result contact() {
		return ok(contact.render());
	}
	
	/**
	 * Make controller methods available in JavaScript
	 * 
	 * @return the {@link Result} of the controller methods as JavaScript script
	 */
	public Result jsRoutes() {
		return ok(Routes.javascriptRouter("jsRoutes",
			controllers.routes.javascript.Assets.versioned(),
			controllers.routes.javascript.Application.search(),
			controllers.routes.javascript.Application.browse()
		)).as("text/javascript");
	}
}