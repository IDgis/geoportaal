# --- !Ups

UPDATE gb.status_label SET label = 'In prullenbak' WHERE id = 5;

# --- !Downs

UPDATE gb.status_label SET label = 'In prullebak' WHERE id = 5;