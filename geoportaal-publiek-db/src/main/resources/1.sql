# --- !Ups

CREATE SCHEMA gp;

CREATE TABLE gp.document (
		id serial,
		uuid text not null,
		md_type_id integer not null,
		title text not null,
		date timestamp,
		creator text,
		description text,
		thumbnail text,
		access_id integer not null
)
;

CREATE TABLE gp.doc_subject (
		id serial,
		document_id integer not null,
		subject_id integer not null
)
;

CREATE TABLE gp.subject (
		id serial,
		name text not null
)
;

CREATE TABLE gp.subject_label (
		id serial,
		subject_id integer not null,
		language text not null,
		title text not null
)
;

CREATE TABLE gp.md_type (
		id serial,
		url text not null,
		name text not null
)
;

CREATE TABLE gp.any_text (
		id serial,
		document_id integer not null,
		content text not null
)
;

CREATE TABLE gp.access (
	id serial,
	name text not null
)
;

ALTER TABLE gp.document ADD CONSTRAINT UQ_document_uuid UNIQUE (uuid);
ALTER TABLE gp.md_type ADD CONSTRAINT UQ_md_type_url UNIQUE (url);
ALTER TABLE gp.doc_subject ADD CONSTRAINT UQ_doc_subject_document_id_subject_id UNIQUE (document_id, subject_id);
ALTER TABLE gp.subject ADD CONSTRAINT UQ_subject_name UNIQUE (name);
ALTER TABLE gp.subject_label ADD CONSTRAINT UQ_subject_label_subject_id_language UNIQUE (subject_id, language);

ALTER TABLE gp.document ADD CONSTRAINT PK_document PRIMARY KEY (id);
ALTER TABLE gp.doc_subject ADD CONSTRAINT PK_doc_subject PRIMARY KEY (id);
ALTER TABLE gp.subject ADD CONSTRAINT PK_subject PRIMARY KEY (id);
ALTER TABLE gp.subject_label ADD CONSTRAINT PK_subject_label PRIMARY KEY (id);
ALTER TABLE gp.md_type ADD CONSTRAINT PK_md_type PRIMARY KEY (id);
ALTER TABLE gp.any_text ADD CONSTRAINT PK_any_text PRIMARY KEY (id);
ALTER TABLE gp.access ADD CONSTRAINT PK_access PRIMARY KEY (id);


ALTER TABLE gp.document ADD CONSTRAINT FK_document_md_type_id FOREIGN KEY (md_type_id) REFERENCES gp.md_type (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gp.document ADD CONSTRAINT FK_document_access_id FOREIGN KEY (access_id) REFERENCES gp.access (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gp.doc_subject ADD CONSTRAINT FK_doc_subject_document_id FOREIGN KEY (document_id) REFERENCES gp.document (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gp.subject_label ADD CONSTRAINT FK_subject_label_subject_id FOREIGN KEY (subject_id) REFERENCES gp.subject (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gp.doc_subject ADD CONSTRAINT FK_doc_subject_subject_id FOREIGN KEY (subject_id) REFERENCES gp.subject (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gp.any_text ADD CONSTRAINT FK_any_text_document_id FOREIGN KEY (document_id) REFERENCES gp.document (id)
	ON DELETE CASCADE ON UPDATE CASCADE
;


# --- !Downs

DROP TABLE IF EXISTS gp.document CASCADE;
DROP TABLE IF EXISTS gp.doc_subject CASCADE;
DROP TABLE IF EXISTS gp.subject CASCADE;
DROP TABLE IF EXISTS gp.subject_label CASCADE;
DROP TABLE IF EXISTS gp.md_type CASCADE;
DROP TABLE IF EXISTS gp.any_text CASCADE;
DROP TABLE IF EXISTS gp.access CASCADE;
DROP SCHEMA IF EXISTS gp;