# --- !Ups

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

CREATE TABLE dataset ( 
	id serial NOT NULL,
	uuid char(50) NOT NULL,
	location char(100) NOT NULL,
	file_id char(20),
	title char(50) NOT NULL,
	description text NOT NULL,
	type_info char(20),
	creator char(50) NOT NULL,
	rights char(30),
	use_limitation char(20) NOT NULL,
	format char(20),
	source char(50),
	date_source_creation timestamp,
	date_source_publication timestamp,
	date_source_revision timestamp,
	date_source_valid_from timestamp,
	date_source_valid_until timestamp,
	supplier char(20) NOT NULL,
	status char(20) NOT NULL,
	published boolean NOT NULL,
	last_revision_user char(50) NOT NULL,
	last_revision_date timestamp NOT NULL
)
;

CREATE TABLE data_attachment ( 
	dataset_id integer NOT NULL,
	attachment_name char(200) NOT NULL,
	attachment_content text NOT NULL
)
;

CREATE TABLE data_subject ( 
	dataset_id integer NOT NULL,
	subject char(50) NOT NULL
)
;

CREATE TABLE type_information ( 
	identification char(20),
	label char(20)
)
;

CREATE TABLE creator ( 
	identification char(20),
	label char(75)
)
;

CREATE TABLE rights ( 
	identification char(30),
	label char(50)
)
;

CREATE TABLE use_limitation ( 
	identification char(10),
	label char(60)
)
;

CREATE TABLE info_format ( 
	identification char(30),
	label char(30)
)
;

CREATE TABLE subject ( 
	identification char(50),
	label char(50)
)
;

CREATE TABLE status ( 
	identification char(20),
	label char(20)
)
;

CREATE TABLE supplier ( 
	id serial NOT NULL,
	name char(30)
)
;


ALTER TABLE dataset
	ADD CONSTRAINT UQ_dataset_uuid UNIQUE (uuid)
;
ALTER TABLE dataset ADD CONSTRAINT PK_dataset 
	PRIMARY KEY (id)
;


ALTER TABLE data_attachment ADD CONSTRAINT PK_data_attachment 
	PRIMARY KEY (attachment_name)
;


ALTER TABLE data_subject ADD CONSTRAINT PK_data_subject 
	PRIMARY KEY (subject)
;


ALTER TABLE supplier ADD CONSTRAINT PK_supplier 
	PRIMARY KEY (id)
;




ALTER TABLE data_attachment ADD CONSTRAINT FK_data_attachment_dataset 
	FOREIGN KEY (dataset_id) REFERENCES dataset (id)
ON DELETE CASCADE ON UPDATE CASCADE
;

ALTER TABLE data_subject ADD CONSTRAINT FK_data_subject_dataset 
	FOREIGN KEY (dataset_id) REFERENCES dataset (id)
ON DELETE CASCADE ON UPDATE CASCADE
;

INSERT INTO defaults VALUES
    ('Provincie Overijssel', 'Provincie Overijssel', 'Nederlands', 5.791, 7.090, 52.115, 52.853, '2015-01-01', '2016-01-01');

    
INSERT INTO type_information VALUES
	('none', NULL),
	('map', 'Kaart'),
	('database', 'Database'),
	('table', 'Tabel'),
	('application', 'Applicatie'),
	('drawing', 'Tekening'),
	('report', 'Rapport'),
	('website', 'Website');
	
INSERT INTO creator VALUES
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
	
INSERT INTO use_limitation VALUES
	('intern', 'Alleen voor intern gebruik'),
	('extern', 'De bron mag ook voor externe partijen vindbaar zijn');
	
INSERT INTO info_format VALUES
	('none', NULL),
	('adobeIllustrator', 'Adobe Illustrator: AI'),
	('freehand', 'Freehand: FH'),
	('adobeAcrobat', 'Adobe Acrobat: PDF'),
	('microstation', 'Microstation: DGN'),
	('autocad', 'Autocad: DWG DXF'),
	('paintshop', 'PaintShop: PSP'),
	('arcgis', 'ArcGIS: MXD');
	
INSERT INTO subject VALUES
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
	
INSERT INTO status VALUES
	('none', NULL),
	('concept', 'Concept'),
	('approval', 'Ter goedkeuring'),
	('published', 'In publicatie'),
	('deleted', 'Verwijderd');
	
INSERT INTO supplier VALUES
	(1, 'Nienhuis'),
	(2, 'Eilers');

# --- !Downs

DROP TABLE defaults CASCADE
;
DROP TABLE dataset CASCADE
;
DROP TABLE data_attachment CASCADE
;
DROP TABLE data_subject CASCADE
;
DROP TABLE type_information CASCADE
;
DROP TABLE creator CASCADE
;
DROP TABLE rights CASCADE
;
DROP TABLE use_limitation CASCADE
;
DROP TABLE info_format CASCADE
;
DROP TABLE subject CASCADE
;
DROP TABLE status CASCADE
;
DROP TABLE supplier CASCADE
;