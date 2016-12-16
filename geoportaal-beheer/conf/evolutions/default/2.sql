# --- !Ups

DROP materialized VIEW if EXISTS gb.metadata_search;
CREATE materialized VIEW gb.metadata_search AS
SELECT 
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
	to_tsvector('dutch', coalesce((string_agg(mfl.label, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) tsv
FROM gb.metadata m
LEFT JOIN gb.md_attachment ma ON m.id=ma.metadata_id
LEFT JOIN gb.type_information_label til ON m.type_information=til.type_information_id
LEFT JOIN gb.creator_label cl ON m.creator=cl.creator_id
LEFT JOIN gb.rights_label rl ON m.rights=rl.rights_id
LEFT JOIN gb.use_limitation_label ull ON m.use_limitation=ull.use_limitation_id
LEFT JOIN gb.md_format_label mfl ON m.md_format=mfl.md_format_id
LEFT JOIN gb.md_subject ms ON m.id=ms.metadata_id
LEFT JOIN gb.subject_label sl ON ms.subject=sl.subject_id
GROUP BY m.id;

CREATE INDEX metadata_search_tsv_idx ON gb.metadata_search USING gin(tsv);
CREATE UNIQUE INDEX metadata_search_metadata_id_idx ON gb.metadata_search USING btree(metadata_id);

# --- !Downs

DROP materialized VIEW if EXISTS gb.metadata_search;
CREATE materialized VIEW gb.metadata_search AS
SELECT 
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
FROM gb.metadata m
LEFT JOIN gb.md_attachment ma ON m.id=ma.metadata_id
JOIN gb.type_information ti ON m.type_information=ti.id
JOIN gb.type_information_label til ON ti.id=til.id
JOIN gb.creator c ON m.creator=c.id
JOIN gb.creator_label cl ON c.id=cl.id
JOIN gb.rights r ON m.rights=r.id
JOIN gb.rights_label rl ON r.id=rl.id
JOIN gb.use_limitation ul ON m.use_limitation=ul.id
JOIN gb.use_limitation_label ull ON ul.id=ull.id
LEFT JOIN gb.md_subject ms ON m.id=ms.metadata_id
JOIN gb.subject s ON ms.subject=s.id
JOIN gb.subject_label sl ON s.id=sl.id
GROUP BY m.id;

CREATE INDEX metadata_search_tsv_idx ON gb.metadata_search USING gin(tsv);
CREATE UNIQUE INDEX metadata_search_metadata_id_idx ON gb.metadata_search USING btree(metadata_id);