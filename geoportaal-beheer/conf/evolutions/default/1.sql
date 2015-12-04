# --- !Ups

CREATE SCHEMA gb;

CREATE TABLE Defaults ( 
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

CREATE TABLE Subject ( 
	dataset_id char(50) NOT NULL,
	subject char(50) NOT NULL
)
;

CREATE TABLE Attachment ( 
	dataset_id char(50) NOT NULL,
	attachment char(200) NOT NULL
)
;

CREATE TABLE Dataset ( 
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


ALTER TABLE Dataset
	ADD CONSTRAINT UQ_Dataset_id UNIQUE (id)
;
ALTER TABLE Dataset ADD CONSTRAINT PK_Dataset 
	PRIMARY KEY (id)
;


INSERT INTO Defaults VALUES
    ('Provincie Overijssel', 'Provincie Overijssel', 'Nederlands', 5.791, 7.090, 52.115, 52.853, '2015-01-01', '2016-01-01');
    
# --- !Downs

DROP SCHEMA gb;
DROP TABLE Defaults;
DROP TABLE Subject;
DROP TABLE Attachment;
DROP TABLE Dataset;