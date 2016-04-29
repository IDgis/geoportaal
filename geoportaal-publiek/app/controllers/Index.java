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
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import models.DocSubject;

import play.i18n.Lang;
import play.mvc.*;
import util.QueryDSL;
import views.html.*;

public class Index extends Controller {
	@Inject QueryDSL q;
	
	public Result index() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			List<Tuple> documents = tx.select(document.title, document.date, document.creator, document.description, document.thumbnail, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.orderBy(document.date.desc())
					.limit(5)
					.fetch();
			
			return ok(index.render(documents, sdf));
		});
	}
	
	public Result search() {
		Lang curLang = Http.Context.current().lang();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			List<Tuple> mdTypes = tx.select(mdType.name, mdTypeLabel.title)
					.from(mdType)
					.join(mdTypeLabel).on(mdType.id.eq(mdTypeLabel.mdTypeId))
					.where(mdTypeLabel.language.eq(curLang.code()))
					.fetch();
			
			List<Tuple> documents = tx.select(document.title, document.date, document.creator, document.description, document.thumbnail, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.orderBy(document.date.desc())
					.limit(10)
					.fetch();
			
			return ok(search.render(mdTypes, documents, sdf));
		});
	}
	
	public Result browse() {
		Lang curLang = Http.Context.current().lang();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			List<Tuple> subjects = tx.select(subject.name, subjectLabel.title)
					.from(subject)
					.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
					.where(subjectLabel.language.eq(curLang.code()))
					.fetch();
			
			List<Tuple> documents = tx.select(document.uuid, document.title, document.date, document.creator, document.description, document.thumbnail, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.where(mdType.name.ne("service"))
					.orderBy(document.date.desc())
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
				
				DocSubject ds = new DocSubject(doc.get(document.title), doc.get(document.date), doc.get(document.creator), 
						doc.get(document.description), doc.get(document.thumbnail), docSubjects, doc.get(mdType.name));
				
				finalDocuments.add(ds);
			}
			
			return ok(browse.render(subjects, finalDocuments, sdf));
		});
	}
	
	public Result about() {
		return ok(about.render());
	}
	
	public Result contact() {
		return ok(contact.render());
	}
}