# --- !Ups

CREATE TABLE gp.harvest_session (
		id bigserial,
		type text not null,
		intern_count integer not null,
		extern_count integer not null,
		create_time timestamp not null
)
;

ALTER TABLE gp.harvest_session ADD CONSTRAINT PK_harvest_session PRIMARY KEY (id);

# --- !Downs

DROP TABLE IF EXISTS gp.harvest_session CASCADE;