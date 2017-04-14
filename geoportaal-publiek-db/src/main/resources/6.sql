# --- !Ups

ALTER TABLE gp.document ADD COLUMN wms_only boolean;

# --- !Downs

ALTER TABLE gp.document DROP COLUMN wms_only;