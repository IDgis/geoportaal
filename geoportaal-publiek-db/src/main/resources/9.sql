# --- !Ups

ALTER TABLE gp.document ADD COLUMN archived boolean;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN archived;