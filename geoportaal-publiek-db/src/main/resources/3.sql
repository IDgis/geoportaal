# --- !Ups

ALTER TABLE gp.document ADD COLUMN downloadable boolean;
ALTER TABLE gp.document ADD COLUMN spatial_schema text;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN downloadable;
ALTER TABLE gp.document DROP COLUMN spatial_schema;