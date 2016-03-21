# --- !Ups

INSERT INTO gb.md_format VALUES
	(9, 'arcmap'),
	(10, 'arcview');

INSERT INTO gb.md_format_label VALUES
	(9, 9, 'nl', 'ArcMap: MAP'),
	(10, 10, 'nl', 'Arc View Project: APR');

# --- !Downs

DELETE FROM gb.md_format WHERE id=9;
DELETE FROM gb.md_format WHERE id=10;
DELETE FROM gb.md_format_label WHERE id=9;
DELETE FROM gb.md_format_label WHERE id=10;