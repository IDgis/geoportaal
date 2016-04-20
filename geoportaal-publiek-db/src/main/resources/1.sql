# --- !Ups

CREATE SCHEMA gp;

CREATE TABLE gp.document (
		id serial,
		uuid varchar(36) not null,
		md_type_id integer not null,
		title varchar(200) not null,
		date timestamp not null,
		creator varchar(200),
		abstract text,
		thumbnail varchar(200)
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
		name varchar(50) not null
)
;

CREATE TABLE gp.subject_label (
		id serial,
		subject_id integer not null,
		language varchar(20) not null,
		title varchar(200) not null
)
;

CREATE TABLE gp.md_type (
		id serial,
		url varchar(200) not null,
		name varchar(20) not null
)
;

CREATE TABLE gp.md_type_label (
		id serial,
		md_type_id integer not null,
		language varchar(20) not null,
		title varchar(100) not null
)
;

CREATE TABLE gp.any_text (
		id serial,
		document_id integer not null,
		content varchar(200) not null
)
;

ALTER TABLE gp.document ADD CONSTRAINT UQ_document_uuid UNIQUE (uuid);
ALTER TABLE gp.md_type ADD CONSTRAINT UQ_md_type_url UNIQUE (url);
ALTER TABLE gp.doc_subject ADD CONSTRAINT UQ_doc_subject_document_id_subject_id UNIQUE (document_id, subject_id);
ALTER TABLE gp.subject ADD CONSTRAINT UQ_subject_name UNIQUE (name);
ALTER TABLE gp.subject_label ADD CONSTRAINT UQ_subject_label_subject_id_language UNIQUE (subject_id, language);
ALTER TABLE gp.md_type_label ADD CONSTRAINT UQ_md_type_label_md_type_id_language UNIQUE (md_type_id, language);

ALTER TABLE gp.document ADD CONSTRAINT PK_document PRIMARY KEY (id);
ALTER TABLE gp.doc_subject ADD CONSTRAINT PK_doc_subject PRIMARY KEY (id);
ALTER TABLE gp.subject ADD CONSTRAINT PK_subject PRIMARY KEY (id);
ALTER TABLE gp.subject_label ADD CONSTRAINT PK_subject_label PRIMARY KEY (id);
ALTER TABLE gp.md_type ADD CONSTRAINT PK_md_type PRIMARY KEY (id);
ALTER TABLE gp.md_type_label ADD CONSTRAINT PK_md_type_label PRIMARY KEY (id);
ALTER TABLE gp.any_text ADD CONSTRAINT PK_any_text PRIMARY KEY (id);


ALTER TABLE gp.document ADD CONSTRAINT FK_document_md_type_id FOREIGN KEY (md_type_id) REFERENCES gp.md_type (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gp.doc_subject ADD CONSTRAINT FK_doc_subject_document_id FOREIGN KEY (document_id) REFERENCES gp.document (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gp.subject_label ADD CONSTRAINT FK_subject_label_subject_id FOREIGN KEY (subject_id) REFERENCES gp.subject (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gp.doc_subject ADD CONSTRAINT FK_doc_subject_subject_id FOREIGN KEY (subject_id) REFERENCES gp.subject (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gp.any_text ADD CONSTRAINT FK_any_text_document_id FOREIGN KEY (document_id) REFERENCES gp.any_text (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gp.md_type_label ADD CONSTRAINT FK_md_type_label_md_type_id FOREIGN KEY (md_type_id) REFERENCES gp.md_type (id)
	ON DELETE NO ACTION ON UPDATE NO ACTION
; 

# --- !Downs

DROP TABLE IF EXISTS gp.document CASCADE;
DROP TABLE IF EXISTS gp.doc_subject CASCADE;
DROP TABLE IF EXISTS gp.subject CASCADE;
DROP TABLE IF EXISTS gp.subject_label CASCADE;
DROP TABLE IF EXISTS gp.md_type CASCADE;
DROP TABLE IF EXISTS gp.md_type_label CASCADE;
DROP TABLE IF EXISTS gp.any_text CASCADE;
DROP SCHEMA IF EXISTS gp;