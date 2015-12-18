# --- !Ups

CREATE SCHEMA gb;

CREATE TABLE defaults ( 
	publisher char(50),
	contributor char(50),
	language char(50),
	west_bound_longitude decimal(10,3),
	east_bound_longitude decimal(10,3),
	south_bound_longitude decimal(10,3),
	north_bound_longitude decimal(10,3),
	temporal_coverage_from timestamp,
	temporal_coverage_until timestamp
)
;

CREATE TABLE data_subject ( 
	dataset_id char(50) NOT NULL,
	subject char(50) NOT NULL
)
;

CREATE TABLE data_attachment ( 
	dataset_id char(50) NOT NULL,
	attachment_name char(200) NOT NULL,
	attachment_content text
)
;

CREATE TABLE dataset ( 
	id char(50) NOT NULL,
	location char(200) NOT NULL,
	file_id char(50),
	title char(50) NOT NULL,
	description text NOT NULL,
	type_info char(30),
	creator char(200) NOT NULL,
	rights char(200),
	use_limitation char(50) NOT NULL,
	format char(50),
	source char(200),
	date_source_creation timestamp,
	date_source_publication timestamp,
	date_source_revision timestamp,
	date_source_valid_from timestamp,
	date_source_valid_until timestamp,
	supplier char(50) NOT NULL,
	status char(50) NOT NULL,
	published boolean NOT NULL,
	last_revision_user char(50) NOT NULL,
	last_revision_date timestamp NOT NULL
)
;

CREATE TABLE type_informations (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE creators (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE rights (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE use_limitations (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE info_formats (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE subjects (
	identification char(50) NOT NULL,
	label char(200)
)
;

CREATE TABLE statuses (
	identification char(50) NOT NULL,
	label char(50)
)
;

CREATE TABLE suppliers (
	id serial,
	name char(100)
)
;

ALTER TABLE dataset
	ADD CONSTRAINT UQ_dataset_id UNIQUE (id)
;
ALTER TABLE dataset ADD CONSTRAINT PK_dataset 
	PRIMARY KEY (id)
;
ALTER TABLE suppliers
	ADD CONSTRAINT UQ_suppliers_id UNIQUE (id)
;
ALTER TABLE suppliers ADD CONSTRAINT PK_suppliers 
	PRIMARY KEY (id)
;


INSERT INTO defaults VALUES
    ('Provincie Overijssel', 'Provincie Overijssel', 'Nederlands', 5.791, 7.090, 52.115, 52.853, '2015-01-01', '2016-01-01');

    
INSERT INTO type_informations VALUES
	('none', NULL),
	('map', 'Kaart'),
	('database', 'Database'),
	('table', 'Tabel'),
	('application', 'Applicatie'),
	('drawing', 'Tekening'),
	('report', 'Rapport'),
	('website', 'Website');
	
INSERT INTO creators VALUES
	('OvUnitEconomy', 'Provincie Overijssel: eenheid Economie en Cultuur'),
	('OvUnitPolicy', 'Provincie Overijssel: eenheid Bestuurs- en Concernzaken'),
	('OvUnitManagement', 'Provincie Overijssel: eenheid Bedrijfsvoering'),
	('OvUnitProject', 'Provincie Overijssel: eenheid Project- en Programmamanagement'),
	('OvUnitSpatial', 'Provincie Overijssel: eenheid Ruimte en Bereikbaarheid'),
	('OvUnitNature', 'Provincie Overijssel: eenheid Natuur en Milieu'),
	('OvUnitRoads', 'Provincie Overijssel: eenheid Wegen en Kanalen'),
	('OvUnitPublic', 'Provincie Overijssel: eenheid Publieke Dienstverlening'),
	('other', 'Andere');
	
INSERT INTO rights VALUES
	('none', NULL),
	('noRestrictions', 'Geen restricties'),
	('copyright', 'Copyright'),
	('patent', 'Patent'),
	('patentFuture', 'Patent in wording'),
	('brandName', 'Merknaam'),
	('license', 'Licentie'),
	('ip', 'Intellectueel eigendom'),
	('notAccessible', 'Niet toegankelijk'),
	('dataWithDeclaration', 'Data mag alleen met verklaring uitgeleverd worden'),
	('dataClassified', 'Data mag niet uitgeleverd worden');
	
INSERT INTO use_limitations VALUES
	('intern', 'Alleen voor intern gebruik'),
	('extern', 'De bron mag ook voor externe partijen vindbaar zijn');
	
INSERT INTO info_formats VALUES
	('none', NULL),
	('adobeIllustrator', 'Adobe Illustrator: AI'),
	('freehand', 'Freehand: FH'),
	('adobeAcrobat', 'Adobe Acrobat: PDF'),
	('microstation', 'Microstation: DGN'),
	('autocad', 'Autocad: DWG DXF'),
	('paintshop', 'PaintShop: PSP'),
	('arcgis', 'ArcGIS: MXD');
	
INSERT INTO subjects VALUES
	('inlandWaters', 'Binnenwater'),
	('structure', 'Civiele structuren'),
	('society', 'Cultuur, maatschappij en demografie'),
	('economy', 'Economie'),
	('biota', 'Flora en fauna'),
	('geoscientificInformation', 'Geowetenschappelijke data'),
	('health', 'Gezondheid'),
	('boundaries', 'Grenzen'),
	('elevation', 'Hoogte'),
	('climatologyMeteorologyAtmosphere', 'Klimatologie, metereologie en atmosfeer'),
	('farming', 'Landbouw en veeteelt'),
	('location', 'Locatie'),
	('intelligenceMilitary', 'Militair'),
	('environment', 'Natuur en milieu'),
	('utilitiesCommunication', 'Nutsvoorieningen en communicatie'),
	('oceans', 'Oceanen'),
	('imageryBaseMapsEarthCover', 'Referentiemateriaal aardbedekking'),
	('planningCadastre', 'Ruimtelijke ordening en kadaster'),
	('transportation', 'Transport en logistiek');
	
INSERT INTO statuses VALUES
	('none', NULL),
	('concept', 'Concept'),
	('approval', 'Ter goedkeuring'),
	('published', 'In publicatie'),
	('deleted', 'Verwijderd');
	
INSERT INTO suppliers VALUES
	(1, 'Nienhuis'),
	(2, 'Eilers');

# --- !Downs

DROP TABLE defaults;
DROP TABLE data_subject;
DROP TABLE data_attachment;
DROP TABLE dataset;
DROP TABLE type_informations;
DROP TABLE creators;
DROP TABLE rights;
DROP TABLE use_limitations;
DROP TABLE info_formats;
DROP TABLE subjects;
DROP TABLE statuses;
DROP TABLE suppliers;
DROP SCHEMA gb;