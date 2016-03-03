# --- !Ups

DROP TABLE gb.supplier CASCADE;

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_user 
	FOREIGN KEY (supplier) REFERENCES gb.user (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

ALTER TABLE gb.user ALTER COLUMN username TYPE varchar(50);

UPDATE gb.user SET username = 'g.nienhuis@overijssel.nl' WHERE id = 1;
UPDATE gb.user SET username = 'nrj.eilers@overijssel.nl' WHERE id = 2;
UPDATE gb.user SET username = 'g.jurjens@overijssel.nl' WHERE id = 3;
UPDATE gb.user SET username = 'pa.nicolaij@overijssel.nl' WHERE id = 4;
UPDATE gb.user SET username = 'am.weijmer@overijssel.nl' WHERE id = 5;
UPDATE gb.user SET username = 'mja.kalter@overijssel.nl' WHERE id = 6;
UPDATE gb.user SET username = 'gj.v.vilsteren@overijssel.nl' WHERE id = 7;
UPDATE gb.user SET username = 'r.hoekstra@zwolle.nl' WHERE id = 8;
UPDATE gb.user SET username = 'wh.maarse@overijssel.nl' WHERE id = 9;
UPDATE gb.user SET username = 'l.zandman@overijssel.nl' WHERE id = 10;
UPDATE gb.user SET username = 'd.vd.veen@overijssel.nl' WHERE id = 11;
UPDATE gb.user SET username = 'j.kerts-bolkestein@overijssel.nl' WHERE id = 12;
UPDATE gb.user SET username = 'rj.vd.anker@overijssel.nl' WHERE id = 13;
UPDATE gb.user SET username = 'r.kreft@overijssel.nl' WHERE id = 14;
UPDATE gb.user SET username = 'sandro.neumann@idgis.nl' WHERE id = 15;

ALTER TABLE gb.user ADD COLUMN label varchar(50);

UPDATE gb.user SET label = 'Nienhuis' WHERE id = 1;
UPDATE gb.user SET label = 'Eilers' WHERE id = 2;
UPDATE gb.user SET label = 'Jurjens' WHERE id = 3;
UPDATE gb.user SET label = 'Nicolaij' WHERE id = 4;
UPDATE gb.user SET label = 'Weijmer' WHERE id = 5;
UPDATE gb.user SET label = 'Kalter' WHERE id = 6;
UPDATE gb.user SET label = 'van Vilsteren' WHERE id = 7;
UPDATE gb.user SET label = 'Hoekstra' WHERE id = 8;
UPDATE gb.user SET label = 'Maarse' WHERE id = 9;
UPDATE gb.user SET label = 'Zandman' WHERE id = 10;
UPDATE gb.user SET label = 'van der Veen' WHERE id = 11;
UPDATE gb.user SET label = 'Kets-Bolkestein' WHERE id = 12;
UPDATE gb.user SET label = 'van den Anker' WHERE id = 13;
UPDATE gb.user SET label = 'Kreft' WHERE id = 14;
UPDATE gb.user SET label = 'Test' WHERE id = 15;




# --- !Downs

CREATE TABLE gb.supplier ( 
	id serial NOT NULL,
	name varchar(30) NOT NULL
)
;
ALTER TABLE gb.supplier
	ADD CONSTRAINT UQ_supplier_name UNIQUE (name)
;
ALTER TABLE gb.supplier ADD CONSTRAINT PK_supplier 
	PRIMARY KEY (id)
;
;
INSERT INTO gb.supplier VALUES
	(1, 'Nienhuis'),
	(2, 'Eilers'),
	(3, 'Jurjens'),
	(4, 'Nicolaij'),
	(5, 'Weijmer'),
	(6, 'Kalter'),
	(7, 'Vilsteren'),
	(8, 'Hoekstra'),
	(9, 'Maarse'),
	(10, 'Zandman'),
	(11, 'Veen'),
	(12, 'KetsBolkestein'),
	(13, 'Anker'),
	(14, 'Kreft'),
	(15, 'Test');

ALTER TABLE gb.metadata ADD CONSTRAINT FK_metadata_supplier 
	FOREIGN KEY (supplier) REFERENCES gb.supplier (id)
ON DELETE NO ACTION ON UPDATE NO ACTION
;

UPDATE gb.user SET username = 'Nienhuis' WHERE id = 1;
UPDATE gb.user SET username = 'Eilers' WHERE id = 2;
UPDATE gb.user SET username = 'Jurjens' WHERE id = 3;
UPDATE gb.user SET username = 'Nicolaij' WHERE id = 4;
UPDATE gb.user SET username = 'Weijmer' WHERE id = 5;
UPDATE gb.user SET username = 'Kalter' WHERE id = 6;
UPDATE gb.user SET username = 'Vilsteren' WHERE id = 7;
UPDATE gb.user SET username = 'Hoekstra' WHERE id = 8;
UPDATE gb.user SET username = 'Maarse' WHERE id = 9;
UPDATE gb.user SET username = 'Zandman' WHERE id = 10;
UPDATE gb.user SET username = 'Veen' WHERE id = 11;
UPDATE gb.user SET username = 'KetsBolkestein' WHERE id = 12;
UPDATE gb.user SET username = 'Anker' WHERE id = 13;
UPDATE gb.user SET username = 'Kreft' WHERE id = 14;
UPDATE gb.user SET username = 'Test' WHERE id = 15;

ALTER TABLE gb.user ALTER COLUMN username TYPE varchar(20);

ALTER TABLE gb.user DROP COLUMN label;