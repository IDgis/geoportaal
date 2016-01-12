# --- !Ups

CREATE SCHEMA gb;

CREATE TABLE gb.constants ( 
	publisher varchar(50),
	contributor varchar(50),
	language varchar(50),
	west_bound_longitude decimal(10,3),
	east_bound_longitude decimal(10,3),
	south_bound_longitude decimal(10,3),
	north_bound_longitude decimal(10,3)
)
;

CREATE TABLE gb.creator ( 
	id serial NOT NULL,
	name varchar(20) NOT NULL
)
;

CREATE TABLE gb.creator_label ( 
	id serial NOT NULL,
	creator_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(75) NOT NULL
)
;

CREATE TABLE gb.md_attachment ( 
	id serial NOT NULL,
	metadata_id integer NOT NULL,
	attachment_name varchar(200) NOT NULL,
	attachment_content bytea NOT NULL,
	attachment_mimetype text NOT NULL
)
;

CREATE TABLE gb.md_format ( 
	id serial NOT NULL,
	name varchar(30) NOT NULL
)
;

CREATE TABLE gb.md_format_label ( 
	id serial NOT NULL,
	md_format_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(30) NOT NULL
)
;

CREATE TABLE gb.md_subject ( 
	id serial NOT NULL,
	metadata_id integer NOT NULL,
	subject integer NOT NULL
)
;

CREATE TABLE gb.metadata ( 
	id serial NOT NULL,
	uuid char(36) NOT NULL,
	location varchar(100) NOT NULL,
	file_id varchar(20),
	title varchar(50) NOT NULL,
	description text NOT NULL,
	type_information integer,
	creator integer NOT NULL,
	creator_other varchar(30),
	rights integer,
	use_limitation integer NOT NULL,
	md_format integer,
	source varchar(50),
	date_source_creation timestamp,
	date_source_publication timestamp,
	date_source_revision timestamp,
	date_source_valid_from timestamp,
	date_source_valid_until timestamp,
	supplier integer NOT NULL,
	status integer NOT NULL,
	published boolean NOT NULL,
	last_revision_user varchar(50) NOT NULL,
	last_revision_date timestamp NOT NULL
)
;

CREATE TABLE gb.rights ( 
	id serial NOT NULL,
	name varchar(30) NOT NULL
)
;

CREATE TABLE gb.rights_label ( 
	id serial NOT NULL,
	rights_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(50) NOT NULL
)
;

CREATE TABLE gb.status ( 
	id serial NOT NULL,
	name varchar(20) NOT NULL
)
;

CREATE TABLE gb.status_label ( 
	id serial NOT NULL,
	status_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(20) NOT NULL
)
;

CREATE TABLE gb.subject ( 
	id serial NOT NULL,
	name varchar(50) NOT NULL
)
;

CREATE TABLE gb.subject_label ( 
	id serial NOT NULL,
	subject_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(50) NOT NULL
)
;

CREATE TABLE gb.supplier ( 
	id serial NOT NULL,
	name varchar(30) NOT NULL
)
;

CREATE TABLE gb.type_information ( 
	id serial NOT NULL,
	name varchar(20) NOT NULL
)
;

CREATE TABLE gb.type_information_label ( 
	id serial NOT NULL,
	type_information_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(20) NOT NULL
)
;

CREATE TABLE gb.use_limitation ( 
	id serial NOT NULL,
	name varchar(10) NOT NULL
)
;

CREATE TABLE gb.use_limitation_label ( 
	id serial NOT NULL,
	use_limitation_id integer NOT NULL,
	locale varchar(10) NOT NULL,
	label varchar(60) NOT NULL
)
;


ALTER TABLE gb.creator
	ADD CONSTRAINT UQ_creator_name UNIQUE (name)
;
ALTER TABLE gb.creator_label
	ADD CONSTRAINT UQ_creator_label_creator_id_locale UNIQUE (creator_id, locale)
;
ALTER TABLE gb.md_attachment
	ADD CONSTRAINT UQ_md_attachment_metadata_id_name UNIQUE (metadata_id, attachment_name)
;
ALTER TABLE gb.md_format
	ADD CONSTRAINT UQ_info_format_name UNIQUE (name)
;
ALTER TABLE gb.md_format_label
	ADD CONSTRAINT UQ_md_format_label_md_format_id_locale UNIQUE (md_format_id, locale)
;
ALTER TABLE gb.md_subject
	ADD CONSTRAINT UQ_md_subject_metadata_id_subject UNIQUE (metadata_id, subject)
;
ALTER TABLE gb.metadata
	ADD CONSTRAINT UQ_dataset_uuid UNIQUE (uuid)
;
ALTER TABLE gb.rights
	ADD CONSTRAINT UQ_rights_name UNIQUE (name)
;
ALTER TABLE gb.rights_label
	ADD CONSTRAINT UQ_rights_label_rights_id_locale UNIQUE (rights_id, locale)
;
ALTER TABLE gb.status
	ADD CONSTRAINT UQ_status_name UNIQUE (name)
;
ALTER TABLE gb.status_label
	ADD CONSTRAINT UQ_status_label_status_id_locale UNIQUE (status_id, locale)
;
ALTER TABLE gb.subject
	ADD CONSTRAINT UQ_subject_name UNIQUE (name)
;
ALTER TABLE gb.subject_label
	ADD CONSTRAINT UQ_subject_label_subject_id_locale UNIQUE (subject_id, locale)
;
ALTER TABLE gb.supplier
	ADD CONSTRAINT UQ_supplier_name UNIQUE (name)
;
ALTER TABLE gb.type_information
	ADD CONSTRAINT UQ_type_information_name UNIQUE (name)
;
ALTER TABLE gb.type_information_label
	ADD CONSTRAINT UQ_type_information_label_type_information_id_locale UNIQUE (type_information_id, locale)
;
ALTER TABLE gb.use_limitation
	ADD CONSTRAINT UQ_use_limitation_name UNIQUE (name)
;
ALTER TABLE gb.use_limitation_label
	ADD CONSTRAINT UQ_use_limitation_label_use_limitation_id_locale UNIQUE (use_limitation_id, locale)
;
ALTER TABLE gb.creator ADD CONSTRAINT PK_creator 
	PRIMARY KEY (id)
;


ALTER TABLE gb.creator_label ADD CONSTRAINT PK_creator_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.md_attachment ADD CONSTRAINT PK_data_attachment 
	PRIMARY KEY (id)
;


ALTER TABLE gb.md_format ADD CONSTRAINT PK_md_format 
	PRIMARY KEY (id)
;


ALTER TABLE gb.md_format_label ADD CONSTRAINT PK_md_format_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.md_subject ADD CONSTRAINT PK_data_subject 
	PRIMARY KEY (id)
;


ALTER TABLE gb.metadata ADD CONSTRAINT PK_dataset 
	PRIMARY KEY (id)
;


ALTER TABLE gb.rights ADD CONSTRAINT PK_rights 
	PRIMARY KEY (id)
;


ALTER TABLE gb.rights_label ADD CONSTRAINT PK_rights_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.status ADD CONSTRAINT PK_status 
	PRIMARY KEY (id)
;


ALTER TABLE gb.status_label ADD CONSTRAINT PK_status_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.subject ADD CONSTRAINT PK_subject 
	PRIMARY KEY (id)
;


ALTER TABLE gb.subject_label ADD CONSTRAINT PK_subject_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.supplier ADD CONSTRAINT PK_supplier 
	PRIMARY KEY (id)
;


ALTER TABLE gb.type_information ADD CONSTRAINT PK_type_information 
	PRIMARY KEY (id)
;


ALTER TABLE gb.type_information_label ADD CONSTRAINT PK_type_information_label 
	PRIMARY KEY (id)
;


ALTER TABLE gb.use_limitation ADD CONSTRAINT PK_use_limitation 
	PRIMARY KEY (id)
;


ALTER TABLE gb.use_limitation_label ADD CONSTRAINT PK_use_limitation_label 
	PRIMARY KEY (id)
;




ALTER TABLE gb.creator_label ADD CONSTRAINT FK_creator_label_creator 
	FOREIGN KEY (creator_id) REFERENCES gb.creator (id)
;

ALTER TABLE gb.md_attachment ADD CONSTRAINT FK_md_attachment_metadata 
	FOREIGN KEY (metadata_id) REFERENCES gb.metadata (id)
ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gb.md_format_label ADD CONSTRAINT FK_md_format_label_md_format 
	FOREIGN KEY (md_format_id) REFERENCES gb.md_format (id)
;

ALTER TABLE gb.md_subject ADD CONSTRAINT FK_md_subject_metadata 
	FOREIGN KEY (metadata_id) REFERENCES gb.metadata (id)
ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE gb.md_subject ADD CONSTRAINT FK_md_subject_subject 
	FOREIGN KEY (subject) REFERENCES gb.subject (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_creator 
	FOREIGN KEY (creator) REFERENCES gb.creator (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_md_format 
	FOREIGN KEY (md_format) REFERENCES gb.md_format (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_rights 
	FOREIGN KEY (rights) REFERENCES gb.rights (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_status 
	FOREIGN KEY (status) REFERENCES gb.status (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_supplier 
	FOREIGN KEY (supplier) REFERENCES gb.supplier (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_type_information 
	FOREIGN KEY (type_information) REFERENCES gb.type_information (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_use_limitation 
	FOREIGN KEY (use_limitation) REFERENCES gb.use_limitation (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.rights_label ADD CONSTRAINT FK_rights_label_rights 
	FOREIGN KEY (rights_id) REFERENCES gb.rights (id)
;

ALTER TABLE gb.status_label ADD CONSTRAINT FK_status_label_status 
	FOREIGN KEY (status_id) REFERENCES gb.status (id)
;

ALTER TABLE gb.subject_label ADD CONSTRAINT FK_subject_label_subject 
	FOREIGN KEY (subject_id) REFERENCES gb.subject (id)
;

ALTER TABLE gb.type_information_label ADD CONSTRAINT FK_type_information_label_type_information 
	FOREIGN KEY (type_information_id) REFERENCES gb.type_information (id)
;

ALTER TABLE gb.use_limitation_label ADD CONSTRAINT FK_use_limitation_label_use_limitation 
	FOREIGN KEY (use_limitation_id) REFERENCES gb.use_limitation (id)
;

INSERT INTO gb.constants VALUES
    ('Provincie Overijssel', 'Provincie Overijssel', 'Nederlands', 5.791, 7.090, 52.115, 52.853);

    
INSERT INTO gb.type_information VALUES
	(1, 'none'),
	(2, 'map'),
	(3, 'database'),
	(4, 'table'),
	(5, 'application'),
	(6, 'drawing'),
	(7, 'report'),
	(8, 'website');
	
INSERT INTO gb.type_information_label VALUES
	(1, 1, 'nl', ''),
	(2, 2, 'nl', 'Kaart'),
	(3, 3, 'nl', 'Database'),
	(4, 4, 'nl', 'Tabel'),
	(5, 5, 'nl', 'Applicatie'),
	(6, 6, 'nl', 'Tekening'),
	(7, 7, 'nl', 'Rapport'),
	(8, 8, 'nl', 'Website');
	
INSERT INTO gb.creator VALUES
	(1, 'OvUnitEconomy'),
	(2, 'OvUnitPolicy'),
	(3, 'OvUnitManagement'),
	(4, 'OvUnitProject'),
	(5, 'OvUnitSpatial'),
	(6, 'OvUnitNature'),
	(7, 'OvUnitRoads'),
	(8, 'OvUnitPublic'),
	(9, 'other');

INSERT INTO gb.creator_label VALUES
	(1, 1, 'nl', 'Provincie Overijssel: eenheid Economie en Cultuur'),
	(2, 2, 'nl', 'Provincie Overijssel: eenheid Bestuurs- en Concernzaken'),
	(3, 3, 'nl', 'Provincie Overijssel: eenheid Bedrijfsvoering'),
	(4, 4, 'nl', 'Provincie Overijssel: eenheid Project- en Programmamanagement'),
	(5, 5, 'nl', 'Provincie Overijssel: eenheid Ruimte en Bereikbaarheid'),
	(6, 6, 'nl', 'Provincie Overijssel: eenheid Natuur en Milieu'),
	(7, 7, 'nl', 'Provincie Overijssel: eenheid Wegen en Kanalen'),
	(8, 8, 'nl', 'Provincie Overijssel: eenheid Publieke Dienstverlening'),
	(9, 9, 'nl', 'Andere');

INSERT INTO gb.rights VALUES
	(1, 'none'),
	(2, 'noRestrictions'),
	(3, 'copyright'),
	(4, 'patent'),
	(5, 'patentFuture'),
	(6, 'brandName'),
	(7, 'license'),
	(8, 'ip'),
	(9, 'notAccessible'),
	(10, 'dataWithDeclaration'),
	(11, 'dataClassified');
	
INSERT INTO gb.rights_label VALUES
	(1, 1, 'nl', ''),
	(2, 2, 'nl', 'Geen restricties'),
	(3, 3, 'nl', 'Copyright'),
	(4, 4, 'nl', 'Patent'),
	(5, 5, 'nl', 'Patent in wording'),
	(6, 6, 'nl', 'Merknaam'),
	(7, 7, 'nl', 'Licentie'),
	(8, 8, 'nl', 'Intellectueel eigendom'),
	(9, 9, 'nl', 'Niet toegankelijk'),
	(10, 10, 'nl', 'Data mag alleen met verklaring uitgeleverd worden'),
	(11, 11, 'nl', 'Data mag niet uitgeleverd worden');
	
INSERT INTO gb.use_limitation VALUES
	(1, 'intern'),
	(2, 'extern');
	
INSERT INTO gb.use_limitation_label VALUES
	(1, 1, 'nl', 'Alleen voor intern gebruik'),
	(2, 2, 'nl', 'De bron mag ook voor externe partijen vindbaar zijn');
	
INSERT INTO gb.md_format VALUES
	(1, 'none'),
	(2, 'adobeIllustrator'),
	(3, 'freehand'),
	(4, 'adobeAcrobat'),
	(5, 'microstation'),
	(6, 'autocad'),
	(7, 'paintshop'),
	(8, 'arcgis');
	
INSERT INTO gb.md_format_label VALUES
	(1, 1, 'nl', ''),
	(2, 2, 'nl', 'Adobe Illustrator: AI'),
	(3, 3, 'nl', 'Freehand: FH'),
	(4, 4, 'nl', 'Adobe Acrobat: PDF'),
	(5, 5, 'nl', 'Microstation: DGN'),
	(6, 6, 'nl', 'Autocad: DWG DXF'),
	(7, 7, 'nl', 'PaintShop: PSP'),
	(8, 8, 'nl', 'ArcGIS: MXD');
	
INSERT INTO gb.subject VALUES
	(1, 'inlandWaters'),
	(2, 'structure'),
	(3, 'society'),
	(4, 'economy'),
	(5, 'biota'),
	(6, 'geoscientificInformation'),
	(7, 'health'),
	(8, 'boundaries'),
	(9, 'elevation'),
	(10, 'climatologyMeteorologyAtmosphere'),
	(11, 'farming'),
	(12, 'location'),
	(13, 'intelligenceMilitary'),
	(14, 'environment'),
	(15, 'utilitiesCommunication'),
	(16, 'oceans'),
	(17, 'imageryBaseMapsEarthCover'),
	(18, 'planningCadastre'),
	(19, 'transportation');

INSERT INTO gb.subject_label VALUES
	(1, 1, 'nl', 'Binnenwater'),
	(2, 2, 'nl', 'Civiele structuren'),
	(3, 3, 'nl', 'Cultuur, maatschappij en demografie'),
	(4, 4, 'nl', 'Economie'),
	(5, 5, 'nl', 'Flora en fauna'),
	(6, 6, 'nl', 'Geowetenschappelijke data'),
	(7, 7, 'nl', 'Gezondheid'),
	(8, 8, 'nl', 'Grenzen'),
	(9, 9, 'nl', 'Hoogte'),
	(10, 10, 'nl', 'Klimatologie, metereologie en atmosfeer'),
	(11, 11, 'nl', 'Landbouw en veeteelt'),
	(12, 12, 'nl', 'Locatie'),
	(13, 13, 'nl', 'Militair'),
	(14, 14, 'nl', 'Natuur en milieu'),
	(15, 15, 'nl', 'Nutsvoorieningen en communicatie'),
	(16, 16, 'nl', 'Oceanen'),
	(17, 17, 'nl', 'Referentiemateriaal aardbedekking'),
	(18, 18, 'nl', 'Ruimtelijke ordening en kadaster'),
	(19, 19, 'nl', 'Transport en logistiek');

INSERT INTO gb.status VALUES
	(1, 'none'),
	(2, 'concept'),
	(3, 'approval'),
	(4, 'published'),
	(5, 'deleted');
	
INSERT INTO gb.status_label VALUES
	(1, 1, 'nl', ''),
	(2, 2, 'nl', 'Concept'),
	(3, 3, 'nl', 'Ter goedkeuring'),
	(4, 4, 'nl', 'In publicatie'),
	(5, 5, 'nl', 'Verwijderd');
	
INSERT INTO gb.supplier VALUES
	(1, 'Nienhuis'),
	(2, 'Eilers'),
	(3, 'Jurjens');

	
# --- !Downs

DROP TABLE gb.constants CASCADE
;
DROP TABLE gb.creator CASCADE
;
DROP TABLE gb.creator_label CASCADE
;
DROP TABLE gb.md_attachment CASCADE
;
DROP TABLE gb.md_format CASCADE
;
DROP TABLE gb.md_format_label CASCADE
;
DROP TABLE gb.md_subject CASCADE
;
DROP TABLE gb.metadata CASCADE
;
DROP TABLE gb.rights CASCADE
;
DROP TABLE gb.rights_label CASCADE
;
DROP TABLE gb.status CASCADE
;
DROP TABLE gb.status_label CASCADE
;
DROP TABLE gb.subject CASCADE
;
DROP TABLE gb.subject_label CASCADE
;
DROP TABLE gb.supplier CASCADE
;
DROP TABLE gb.type_information CASCADE
;
DROP TABLE gb.type_information_label CASCADE
;
DROP TABLE gb.use_limitation CASCADE
;
DROP TABLE gb.use_limitation_label CASCADE
;
DROP SCHEMA gb CASCADE
;