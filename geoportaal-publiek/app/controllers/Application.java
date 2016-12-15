package controllers;

import static models.QAccess.access;
import static models.QDocumentSearch.documentSearch;
import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QMdType.mdType;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

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
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.*;
import util.QueryDSL;
import views.html.*;

public class Application extends Controller {
	@Inject QueryDSL q;
	@Inject WSClient ws;
	
	public Result index() {
		String portalAccess = play.Play.application().configuration().getString("portal.access");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(portalAccess)) {
				intern = true;
			}
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, document.date, document.creator, document.description, 
					document.thumbnail, document.downloadable, document.spatialSchema, document.published, document.typeService, mdType.url, mdType.name)
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
			return redirect(controllers.routes.Application.search(0, s.getText(), s.getElementsString(), false, true));
		} if("browse".equals(s.getPage())) {
			return redirect(controllers.routes.Application.browse(0, s.getText(), s.getElementsString(), false, true));
		} else {
			return notFound("404 - not found");
		}
	}
	
	public Result search(Integer start, String textSearch, String typesString, Boolean filter, Boolean expand) {
		String portalAccess = play.Play.application().configuration().getString("portal.access");
		
		Lang curLang = Http.Context.current().lang();
		String tsvLang = Messages.get("tsv.language");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] typesArray = typesString.split("\\++");
		List<String> types = Arrays.asList(typesArray);
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(portalAccess)) {
				intern = true;
			}
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, document.date, document.creator, document.description, 
					document.thumbnail, document.downloadable, document.spatialSchema, document.published, document.typeService, mdType.url, mdType.name)
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
			
			Integer page = (start + 10) / 10;
			
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
				return ok(searchresult.render(documents, sdf, textSearch, typesString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand));
			}
			
			return ok(search.render(documents, sdf, textSearch, typesString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand));
		});
	}
	
	public Result browse(Integer start, String textSearch, String subjectsString, Boolean filter, Boolean expand) {
		String portalAccess = play.Play.application().configuration().getString("portal.access");
		
		Lang curLang = Http.Context.current().lang();
		String tsvLang = Messages.get("tsv.language");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] subjectsArray = subjectsString.split("\\++");
		List<String> subjectsList = Arrays.asList(subjectsArray);
		
		return q.withTransaction(tx -> {
			Boolean intern = false;
			if("intern".equals(portalAccess)) {
				intern = true;
			}
			
			List<Tuple> subjects = tx.select(subject.name, subjectLabel.title)
					.from(subject)
					.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
					.where(subjectLabel.language.eq(curLang.code()))
					.orderBy(subjectLabel.title.asc())
					.fetch();
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.uuid, document.title, document.date, document.creator, document.description, 
					document.thumbnail, document.downloadable, document.spatialSchema, document.published, document.typeService, mdType.url, mdType.name)
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
			
			Integer page = (start + 10) / 10;
			
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
						doc.get(document.description), doc.get(document.thumbnail), docSubjects, doc.get(mdType.url), doc.get(mdType.name),
						doc.get(document.downloadable), doc.get(document.spatialSchema), doc.get(document.published), doc.get(document.typeService));
				
				finalDocuments.add(ds);
			}
			
			if(filter) {
				return ok(browseresult.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand));
			}
			
			return ok(browse.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand));
		});
	}
	
	public Result about() {
		return ok(about.render());
	}
	
	public Result contact() {
		return ok(contact.render());
	}
	
	public Result help() {
		return ok(help.render());
	}
	
	public Promise<Result> getMetadata(String type, String uuid, Boolean noStyle) throws MalformedURLException, IOException {
		String access = play.Play.application().configuration().getString("portal.access");
		
		String url = getMetadataUrl(type);
			
		if(url == null) {
			return Promise.pure(notFound("404 - not found"));
		}
		
		WSRequest request = ws.url(url + uuid + ".xml");
		
		if("intern".equals(access)) {
			request.setHeader(play.Play.application().configuration().getString("trusted.header"), "1");
		} else {
			request.setHeader(play.Play.application().configuration().getString("trusted.header"), "0");
		}
		
		return request.get().map(response -> {
			if(noStyle) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.parse(response.getBodyAsStream());
				
				// remove existing stylesheet
				NodeList children = d.getChildNodes();
				for(int i = 0; i < children.getLength(); i++) {
					Node n = children.item(i);
					if(n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
						ProcessingInstruction pi = (ProcessingInstruction)n;
						if("xml-stylesheet".equals(pi.getTarget())) {
							d.removeChild(pi);
						}
					}
				}
				
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				
				ByteArrayOutputStream boas = new ByteArrayOutputStream();
				t.transform(new DOMSource(d), new StreamResult(boas));
				boas.close();
				
				return ok(boas.toByteArray()).as("UTF-8").as("application/xml");
			} else {
				return ok(response.getBodyAsStream()).as("UTF-8").as("application/xml");
			}
		});
	}
	
	public String getMetadataUrl(String type) {
		return q.withTransaction(tx -> {
			return tx.select(mdType.url)
				.from(mdType)
				.where(mdType.name.eq(type))
				.fetchOne();
		});
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