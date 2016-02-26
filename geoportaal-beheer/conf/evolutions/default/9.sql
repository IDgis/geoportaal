# --- !Ups

drop materialized view if exists gb.metadata_search;
ALTER TABLE gb.metadata ALTER COLUMN file_id SET NOT NULL;
ALTER TABLE gb.metadata ALTER COLUMN file_id TYPE VARCHAR(200);
ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(200);
ALTER TABLE gb.metadata ALTER COLUMN creator_other TYPE VARCHAR(200);
ALTER TABLE gb.metadata ALTER COLUMN source TYPE VARCHAR(200);
ALTER TABLE gb.metadata ALTER COLUMN last_revision_user TYPE VARCHAR(200);
create materialized view gb.metadata_search as
select 
	m.id metadata_id,
	to_tsvector('dutch', coalesce((string_agg(m.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.description, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.location, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.file_id, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.uuid, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(ma.attachment_name, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(til.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(cl.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.creator_other, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(rl.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(ull.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) tsv
from gb.metadata m
left join gb.md_attachment ma on m.id=ma.metadata_id
join gb.type_information ti on m.type_information=ti.id
join gb.type_information_label til on ti.id=til.id
join gb.creator c on m.creator=c.id
join gb.creator_label cl on c.id=cl.id
join gb.rights r on m.rights=r.id
join gb.rights_label rl on r.id=rl.id
join gb.use_limitation ul on m.use_limitation=ul.id
join gb.use_limitation_label ull on ul.id=ull.id
left join gb.md_subject ms on m.id=ms.metadata_id
join gb.subject s on ms.subject=s.id
join gb.subject_label sl on s.id=sl.id
group by m.id;

create index metadata_search_tsv_idx ON gb.metadata_search USING gin(tsv);
create unique index metadata_search_metadata_id_idx ON gb.metadata_search USING btree(metadata_id);

# --- !Downs


drop materialized view if exists gb.metadata_search;
ALTER TABLE gb.metadata ALTER COLUMN file_id DROP NOT NULL;
ALTER TABLE gb.metadata ALTER COLUMN file_id TYPE VARCHAR(20);
ALTER TABLE gb.metadata ALTER COLUMN title TYPE VARCHAR(199);
ALTER TABLE gb.metadata ALTER COLUMN creator_other TYPE VARCHAR(30);
ALTER TABLE gb.metadata ALTER COLUMN source TYPE VARCHAR(50);
ALTER TABLE gb.metadata ALTER COLUMN last_revision_user TYPE VARCHAR(50);
create materialized view gb.metadata_search as
select 
	m.id metadata_id,
	to_tsvector('dutch', coalesce((string_agg(m.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.description, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.location, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.file_id, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.uuid, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(ma.attachment_name, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(til.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(cl.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.creator_other, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(rl.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(ull.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) tsv
from gb.metadata m
left join gb.md_attachment ma on m.id=ma.metadata_id
join gb.type_information ti on m.type_information=ti.id
join gb.type_information_label til on ti.id=til.id
join gb.creator c on m.creator=c.id
join gb.creator_label cl on c.id=cl.id
join gb.rights r on m.rights=r.id
join gb.rights_label rl on r.id=rl.id
join gb.use_limitation ul on m.use_limitation=ul.id
join gb.use_limitation_label ull on ul.id=ull.id
left join gb.md_subject ms on m.id=ms.metadata_id
join gb.subject s on ms.subject=s.id
join gb.subject_label sl on s.id=sl.id
group by m.id;

create index metadata_search_tsv_idx ON gb.metadata_search USING gin(tsv);
create unique index metadata_search_metadata_id_idx ON gb.metadata_search USING btree(metadata_id);