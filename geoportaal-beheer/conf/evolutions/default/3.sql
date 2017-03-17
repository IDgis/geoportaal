# --- !Ups

INSERT INTO gb.use_limitation VALUES
	(3, 'internPrivacy');

INSERT INTO gb.use_limitation_label VALUES
	(3, 3, 'nl', 'Alleen voor intern gebruik. Extern leveren met bewerkingsovereenkomst (privacy-gevoelige informatie).');

# --- !Downs

DELETE FROM gb.use_limitation WHERE id = 3;
DELETE FROM gb.use_limitation_label WHERE id = 3;