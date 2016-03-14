# --- !Ups

UPDATE gb.user SET username = 'nienhuisg@yahoo.com' WHERE id = 15;

# --- !Downs

UPDATE gb.user SET username = 'sandro.neumann@idgis.nl' WHERE id = 15;