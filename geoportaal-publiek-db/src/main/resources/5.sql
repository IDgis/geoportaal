# --- !Ups

ALTER TABLE gp.document ADD COLUMN viewer_url text;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN viewer_url;