# --- !Ups

ALTER TABLE gb.metadata ALTER COLUMN file_id SET NOT NULL;

# --- !Downs

ALTER TABLE gb.metadata ALTER COLUMN file_id DROP NOT NULL;