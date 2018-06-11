# --- !Ups

ALTER TABLE gp.document RENAME COLUMN date TO date_description;
ALTER TABLE gp.document ADD COLUMN date_dataset timestamp;

# --- !Downs

ALTER TABLE gp.document RENAME COLUMN date_description TO date;
ALTER TABLE gp.document DROP COLUMN date_dataset;