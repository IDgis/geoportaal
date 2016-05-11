package controllers;

import static models.QAccess.access;
import static models.QDocumentSearch.documentSearch;
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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.text.Document;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.WindowOver;

import models.DocSubject;
import models.Search;
import play.Routes;
import play.data.Form;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;
import util.QueryDSL;
import views.html.*;

public class Application extends Controller {
	@Inject QueryDSL q;
	
	public Result index() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(play.Play.application().configuration().getString("portal.access"))) {
				intern = true;
			}
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, document.date, document.creator, document.description, 
					document.thumbnail, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.where(document.description.isNotNull());
			
			if(!intern) {
				Integer accessId = tx.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
				
				queryDocuments.where(document.accessId.eq(accessId));
			}
			
			List<Tuple> documents = queryDocuments.orderBy(document.date.desc(), document.title.asc())
					.limit(5)
					.fetch();
			
			return ok(index.render(documents, sdf));
		});
	}
	
	public Result searchText() {
		// Fetches the form
		Form<Search> searchForm = Form.form(Search.class);
		Search s = searchForm.bindFromRequest().get();
		
		if("search".equals(s.getPage())) {
			return redirect(controllers.routes.Application.search(0, s.getText(), s.getElementsString(), false));
		} if("browse".equals(s.getPage())) {
			return redirect(controllers.routes.Application.browse(0, s.getText(), s.getElementsString(), false));
		} else {
			return notFound("404 - not found");
		}
	}
	
	public Result search(Integer start, String textSearch, String typesString, Boolean filter) {
		Lang curLang = Http.Context.current().lang();
		String tsvLang = Messages.get("tsv.language");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] typesArray = typesString.split("\\++");
		List<String> types = Arrays.asList(typesArray);
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(play.Play.application().configuration().getString("portal.access"))) {
				intern = true;
			}
			
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
			
			if(!intern) {
				Integer accessId = tx.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
				
				queryDocuments.where(document.accessId.eq(accessId));
			}
			
			// Strip characters from text search string that conflict with Postgres full-text search
			String textSearchFirstStrip = textSearch.replace("&", "");
			String textSearchSecondStrip = textSearchFirstStrip.replace("(", "");
			String textSearchThirdStrip = textSearchSecondStrip.replace(")", "");
			String textSearchFinalStrip = textSearchThirdStrip.replace(":", "");
			
			// Convert text search string to an array
			String[] textSearchTerms = textSearchFinalStrip.split("\\s+");
			
			// Convert array of text search words to list
			List<String> finalListTextSearch = new ArrayList<String>();
			List<String> textListTermsSearch = Arrays.asList(textSearchTerms);
			for(String word : textListTermsSearch) {
				if(word.length() > 0) {
					finalListTextSearch.add(word + ":*");
				}
			}
			
			// Create a string of all the words in the text search list with a '&' between them
			String tsQuery = 
				finalListTextSearch.stream()
					.filter(str -> !str.isEmpty())
					.collect(Collectors.joining(" & "));
			
			// Filter records on text search words
			if(!tsQuery.isEmpty()) {
				queryDocuments.where(
					tx.selectOne()
						.from(documentSearch)
						.where(documentSearch.documentId.eq(document.id))
						.where(documentSearch.tsv.query(tsvLang, tsQuery))
						.exists());
			}
			
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
				return ok(searchresult.render(mdTypes, documents, sdf, textSearch, typesString, count, finalStart, startPrevious, startNext, startLast, pageLast));
			}
			
			return ok(search.render(mdTypes, documents, sdf, textSearch, typesString, count, finalStart, startPrevious, startNext, startLast, pageLast));
		});
	}
	
	public Result browse(Integer start, String textSearch, String subjectsString, Boolean filter) {
		Lang curLang = Http.Context.current().lang();
		String tsvLang = Messages.get("tsv.language");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] subjectsArray = subjectsString.split("\\++");
		List<String> subjectsList = Arrays.asList(subjectsArray);
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(play.Play.application().configuration().getString("portal.access"))) {
				intern = true;
			}
			
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
			
			if(!intern) {
				Integer accessId = tx.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
				
				queryDocuments.where(document.accessId.eq(accessId));
			}
			
			// Strip characters from text search string that conflict with Postgres full-text search
			String textSearchFirstStrip = textSearch.replace("&", "");
			String textSearchSecondStrip = textSearchFirstStrip.replace("(", "");
			String textSearchThirdStrip = textSearchSecondStrip.replace(")", "");
			String textSearchFinalStrip = textSearchThirdStrip.replace(":", "");
			
			// Convert text search string to an array
			String[] textSearchTerms = textSearchFinalStrip.split("\\s+");
			
			// Convert array of text search words to list
			List<String> finalListTextSearch = new ArrayList<String>();
			List<String> textListTermsSearch = Arrays.asList(textSearchTerms);
			for(String word : textListTermsSearch) {
				if(word.length() > 0) {
					finalListTextSearch.add(word + ":*");
				}
			}
			
			// Create a string of all the words in the text search list with a '&' between them
			String tsQuery = 
				finalListTextSearch.stream()
					.filter(str -> !str.isEmpty())
					.collect(Collectors.joining(" & "));
			
			// Filter records on text search words
			if(!tsQuery.isEmpty()) {
				queryDocuments.where(
					tx.selectOne()
						.from(documentSearch)
						.where(documentSearch.documentId.eq(document.id))
						.where(documentSearch.tsv.query(tsvLang, tsQuery))
						.exists());
			}
			
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
				return ok(browseresult.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, finalStart, startPrevious, startNext, startLast, pageLast));
			}
			
			return ok(browse.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, finalStart, startPrevious, startNext, startLast, pageLast));
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