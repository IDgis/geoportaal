# --- !Ups

ALTER TABLE gp.document ADD COLUMN type_information text;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN type_information;
