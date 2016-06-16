# --- !Ups

ALTER TABLE gb.metadata DROP COLUMN date_source_revision;

# --- !Downs

ALTER TABLE gb.metadata ADD COLUMN date_source_revision timestamp;