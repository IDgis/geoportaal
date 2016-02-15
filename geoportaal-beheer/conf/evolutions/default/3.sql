# --- !Ups

ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(150);

# --- !Downs

ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(50);