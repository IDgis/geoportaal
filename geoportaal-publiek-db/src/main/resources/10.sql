# --- !Ups

ALTER TABLE gp.harvest_session ADD COLUMN archived_count integer;

# --- !Downs

ALTER TABLE gp.harvest_session DROP COLUMN archived_count;