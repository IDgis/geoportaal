# --- !Ups

INSERT INTO gb.user VALUES
	(17, 'mi.zomerdijk@overijssel.nl', '$2a$10$u6J3Q9/PQzlf8zao8I/68esBqM.xYZ6emRPR1ODkHx1h0r82iPg3q', '1', 'Zomerdijk');

# --- !Downs

DELETE FROM gb.user WHERE id=17;