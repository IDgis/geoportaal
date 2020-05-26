package controllers;

import static models.QAccess.access;
import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QDocumentSearch.documentSearch;
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
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;

import models.DocSubject;
import models.Search;
import play.Routes;
import play.data.Form;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
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
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, document.dateDataset, 
					document.creator, document.description, document.thumbnail, document.downloadable, 
					document.spatialSchema, document.published, document.typeService, document.viewerUrl, 
					document.wmsOnly, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(mdType.name.ne("service"))
					.where(document.dateDataset.isNotNull())
					.where(document.description.isNotNull())
					.where(document.archived.isNull().or(document.archived.isFalse()))
					.where(document.maintenanceFrequency.isNull().or(document.maintenanceFrequency.ne("daily")));
			
			if(!intern) {
				Integer accessId = tx.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
				
				queryDocuments.where(document.accessId.eq(accessId));
			}
			
			List<Tuple> documents = queryDocuments.orderBy(document.dateDataset.desc(), document.title.asc())
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
			return redirect(controllers.routes.Application.search(0, s.getText(), s.getElementsString(), false, true, "sortDataset"));
		} if("browse".equals(s.getPage())) {
			return redirect(controllers.routes.Application.browse(0, s.getText(), s.getElementsString(), false, true, "sortDataset"));
		} else {
			return notFound("404 - not found");
		}
	}
	
	public Result search(Integer start, String textSearch, String typesString, Boolean filter, Boolean expand, String sort) {
		String portalAccess = play.Play.application().configuration().getString("portal.access");
		
		Lang curLang = Http.Context.current().lang();
		String tsvLang = Messages.get("tsv.language");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		String[] typesArray = typesString.split("\\++");
		List<String> types = Arrays.asList(typesArray);
		
		return q.withTransaction(tx -> {
			boolean intern = false;
			if("intern".equals(portalAccess)) {
				intern = true;
			}
			
			boolean includingDatasetsArchived = false;
			for(String type : types) {
				if("datasetArchived".equals(type)) includingDatasetsArchived = true;
			}
			
			DateTimePath<Timestamp> dateColumn = document.dateDataset;
			if("sortDescription".equals(sort)) {
				dateColumn = document.dateDescription;
			}
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.title, document.uuid, dateColumn, 
					document.creator, document.description, document.thumbnail, document.downloadable, 
					document.spatialSchema, document.published, document.typeService, document.viewerUrl,
					document.wmsOnly, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id));
			
			if(intern) {
				if(includingDatasetsArchived) {
					queryDocuments
						.where((mdType.name.in(types).and(document.archived.isNull().or(document.archived.isFalse())))
							.or(mdType.name.eq("dataset").and(document.archived.isTrue())));
				} else {
					queryDocuments
						.where(mdType.name.in(types))
						.where(document.archived.isNull().or(document.archived.isFalse()));
				}
			} else {
				Integer accessId = tx.select(access.id)
						.from(access)
						.where(access.name.eq("extern"))
						.fetchOne();
					
				queryDocuments
					.where(document.accessId.eq(accessId))
					.where(mdType.name.in(types))
					.where(document.archived.isNull().or(document.archived.isFalse()));
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
			
			List<Tuple> documents = queryDocuments.orderBy(dateColumn.desc().nullsLast(), document.title.asc())
					.offset(finalStart)
					.limit(10)
					.fetch();
			
			if(filter) {
				return ok(searchresult.render(documents, sdf, textSearch, types, typesString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand, sort));
			}
			
			return ok(search.render(documents, sdf, textSearch, types, typesString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand, sort));
		});
	}
	
	public Result browse(Integer start, String textSearch, String subjectsString, Boolean filter, Boolean expand, String sort) {
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
			
			DateTimePath<Timestamp> dateColumn = document.dateDataset;
			if("sortDescription".equals(sort)) {
				dateColumn = document.dateDescription;
			}
			
			SQLQuery<Tuple> queryDocuments = tx.select(document.uuid, document.title, dateColumn, 
					document.creator, document.description, document.thumbnail, document.downloadable, 
					document.spatialSchema, document.published, document.typeService, document.viewerUrl,
					document.wmsOnly, mdType.url, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.archived.isNull().or(document.archived.isFalse()))
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
			
			List<Tuple> documents = queryDocuments.orderBy(dateColumn.desc().nullsLast(), document.title.asc())
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
				
				DocSubject ds = new DocSubject(doc.get(document.uuid), doc.get(document.title), 
						doc.get(dateColumn), doc.get(document.creator), doc.get(document.description), 
						doc.get(document.thumbnail), docSubjects, doc.get(mdType.url), doc.get(mdType.name),
						doc.get(document.downloadable), doc.get(document.spatialSchema), doc.get(document.published), 
						doc.get(document.typeService), doc.get(document.viewerUrl), doc.get(document.wmsOnly));
				
				finalDocuments.add(ds);
			}
			
			if(filter) {
				return ok(browseresult.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand, sort));
			}
			
			return ok(browse.render(subjects, finalDocuments, sdf, textSearch, subjectsString, count, page, finalStart, startPrevious, startNext, startLast, pageLast, expand, sort));
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
			String contentType = response.getHeader("content-type");
			
			if(!"application/xml".equals(contentType)) return forbidden("403: forbidden");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document d = db.parse(response.getBodyAsStream());
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			
			if("service".equals(type)) {
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				
				xpath.setNamespaceContext(new NamespaceContext() {
					public String getNamespaceURI(String prefix) {
						if(prefix == null) {
							throw new NullPointerException("Null prefix");
						} else if("gmd".equals(prefix)) {
							return "http://www.isotc211.org/2005/gmd";
						} else if("gco".equals(prefix)) {
							return "http://www.isotc211.org/2005/gco";
						} else if("xlink".equals(prefix)) {
							return "http://www.w3.org/1999/xlink";
						} else if("gml".equals(prefix)) {
							return "http://www.opengis.net/gml";
						} else if("srv".equals(prefix)) {
							return "http://www.isotc211.org/2005/srv";
						}
						
						return XMLConstants.NULL_NS_URI;
					}
					
					public String getPrefix(String uri) {
						throw new UnsupportedOperationException();
					}
					
					public Iterator getPrefixes(String uri) {
						throw new UnsupportedOperationException();
					}
				});
				
				String metadataPrefix = play.Play.application().configuration().getString("metadata.prefix");
				String datasetUrls = "/gmd:MD_Metadata/gmd:identificationInfo/srv:SV_ServiceIdentification/"
						+ "srv:operatesOn/@xlink:href";
				NodeList nodelist = (NodeList) xpath.evaluate(datasetUrls, d, XPathConstants.NODESET);
				
				if(nodelist != null) {
					for(int node = 0;node < nodelist.getLength();node++) {
						String content = nodelist.item(node).getTextContent();
						
						StringBuilder sb = new StringBuilder(content);
						int indexBegin = sb.indexOf("/metadata/dataset");
						int indexEnd = sb.indexOf(".xml");
						
						String finalContent = sb.substring(indexBegin, indexEnd);
						nodelist.item(node).setNodeValue(metadataPrefix + finalContent);
					}
				}
			}
			
			if(noStyle) {
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
			}
			
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			t.transform(new DOMSource(d), new StreamResult(boas));
			boas.close();
			
			return ok(boas.toByteArray()).as("UTF-8").as("application/xml");
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