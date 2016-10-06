# --- !Ups

ALTER TABLE gp.document ADD COLUMN type_service text;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN type_service;