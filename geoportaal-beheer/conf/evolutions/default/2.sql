# --- !Ups

INSERT INTO gb.supplier VALUES
	(14, 'Kreft');

INSERT INTO gb.user VALUES
	(14, 'Kreft', '$2a$10$U8pT177qIPwKIkAe4iXbLeRIv0j60o.1FQgWZ3m0u5lwmn2h9Ykw2', 2);

# --- !Downs

DELETE FROM gb.supplier WHERE id=14;
DELETE FROM gb.user WHERE id=14;