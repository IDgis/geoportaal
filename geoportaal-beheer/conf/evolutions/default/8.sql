# --- !Ups

ALTER TABLE gb.user ADD CONSTRAINT UQ_user_username UNIQUE (username);
ALTER TABLE gb.user ADD CONSTRAINT UQ_user_label UNIQUE (label);
ALTER TABLE gb.user DROP CONSTRAINT UQ_user_username_password;

# --- !Downs

ALTER TABLE gb.user DROP CONSTRAINT UQ_user_username;
ALTER TABLE gb.user DROP CONSTRAINT UQ_user_label;
ALTER TABLE gb.user ADD CONSTRAINT UQ_user_username_password UNIQUE (username, password);
