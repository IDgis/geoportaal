# --- !Ups

ALTER TABLE gp.document ADD COLUMN maintenance_frequency text;

DROP materialized VIEW if EXISTS gp.document_search;

CREATE materialized VIEW gp.document_search as
SELECT 
	d.id document_id,
	to_tsvector('dutch', coalesce((string_agg(d.uuid, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.creator, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.description, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.maintenance_frequency, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(sl.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(an.content, ' ')), '')) tsv
FROM gp.document d 
LEFT JOIN gp.doc_subject ds ON d.id=ds.document_id
LEFT JOIN gp.subject s ON ds.subject_id=s.id
LEFT JOIN gp.subject_label sl ON s.id=sl.subject_id
LEFT JOIN gp.any_text an ON d.id=an.document_id
GROUP BY d.id
;

CREATE INDEX document_search_tsv_idx ON gp.document_search USING gin(tsv);
CREATE UNIQUE INDEX document_search_document_id_idx ON gp.document_search USING btree(document_id);

# --- !Downs

DROP materialized VIEW if EXISTS gp.document_search;
ALTER TABLE gp.document DROP COLUMN maintenance_frequency;

CREATE materialized VIEW gp.document_search as
SELECT 
	d.id document_id,
	to_tsvector('dutch', coalesce((string_agg(d.uuid, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.creator, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(d.description, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(sl.title, ' ')), '')) ||
	to_tsvector('dutch', coalesce((string_agg(an.content, ' ')), '')) tsv
FROM gp.document d 
LEFT JOIN gp.doc_subject ds ON d.id=ds.document_id
LEFT JOIN gp.subject s ON ds.subject_id=s.id
LEFT JOIN gp.subject_label sl ON s.id=sl.subject_id
LEFT JOIN gp.any_text an ON d.id=an.document_id
GROUP BY d.id
;

CREATE INDEX document_search_tsv_idx ON gp.document_search USING gin(tsv);
CREATE UNIQUE INDEX document_search_document_id_idx ON gp.document_search USING btree(document_id);
