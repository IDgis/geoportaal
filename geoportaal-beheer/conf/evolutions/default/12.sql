# --- !Ups

INSERT INTO gb.user VALUES
	(16, 'sandro.neumann@idgis.nl', '$2a$10$Yr.IIP0Mp1YSocYsYko/BOvcTF/DWD9AQGaFmQhCQJ9VQqjKivvEe', '1', 'IDgis');

# --- !Downs

DELETE FROM gb.user WHERE id=16;