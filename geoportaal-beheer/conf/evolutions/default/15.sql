# --- !Ups

alter table gb.md_attachment add column attachment_length integer;
update gb.md_attachment set attachment_length = (octet_length(attachment_content));
alter table gb.md_attachment alter column attachment_length set not null;

# --- !Downs

alter table gb.md_attachment drop column attachment_length;