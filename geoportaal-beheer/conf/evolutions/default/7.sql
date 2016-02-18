# --- !Ups

UPDATE gb.user SET role_id = 1 WHERE id = 8;

# --- !Downs

UPDATE gb.user SET role_id = 2 WHERE id = 8;