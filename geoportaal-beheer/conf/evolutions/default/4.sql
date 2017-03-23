# --- !Ups

INSERT INTO gb.status VALUES
	(6, 'archived');
	

INSERT INTO gb.status_label VALUES
	(6, 6, 'nl', 'In archief');

# --- !Downs

DELETE FROM gb.status WHERE id = 6;
DELETE FROM gb.status_label WHERE id = 6;