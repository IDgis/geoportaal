# --- !Ups

UPDATE gb.status_label SET label = 'Gepubliceerd' WHERE id = 4;
UPDATE gb.status_label SET label = 'In prullebak' WHERE id = 5;

# --- !Downs

UPDATE gb.status_label SET label = 'In publicatie' WHERE id = 4;
UPDATE gb.status_label SET label = 'Verwijderd' WHERE id = 5;