# --- !Ups

alter table gb.user add column archived boolean not null default false;
	
# --- !Downs

alter table gb.user drop column archived;