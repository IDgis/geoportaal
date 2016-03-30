# --- !Ups

alter table gb.metadata drop column published;


# --- !Downs

alter table gb.metadata add column published boolean;
update gb.metadata set published = false;
alter table gb.metadata alter column published set not null;