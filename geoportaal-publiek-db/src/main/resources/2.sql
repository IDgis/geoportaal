# --- !Ups

INSERT INTO gp.subject VALUES
		(1, 'inlandWaters'),
		(2, 'structure'),
		(3, 'society'),
		(4, 'economy'),
		(5, 'biota'),
		(6, 'geoscientificInformation'),
		(7, 'health'),
		(8, 'boundaries'),
		(9, 'elevation'),
		(10, 'climatologyMeteorologyAtmosphere'),
		(11, 'farming'),
		(12, 'location'),
		(13, 'intelligenceMilitary'),
		(14, 'environment'),
		(15, 'utilitiesCommunication'),
		(16, 'oceans'),
		(17, 'imageryBaseMapsEarthCover'),
		(18, 'planningCadastre'),
		(19, 'transportation')
;

INSERT INTO gp.subject_label VALUES
		(1, 1, 'nl', 'Binnenwater'),
		(2, 2, 'nl', 'Civiele structuren'),
		(3, 3, 'nl', 'Cultuur, maatschappij en demografie'),
		(4, 4, 'nl', 'Economie'),
		(5, 5, 'nl', 'Flora en fauna'),
		(6, 6, 'nl', 'Geowetenschappelijke data'),
		(7, 7, 'nl', 'Gezondheid'),
		(8, 8, 'nl', 'Grenzen'),
		(9, 9, 'nl', 'Hoogte'),
		(10, 10, 'nl', 'Klimatologie, metereologie en atmosfeer'),
		(11, 11, 'nl', 'Landbouw en veeteelt'),
		(12, 12, 'nl', 'Locatie'),
		(13, 13, 'nl', 'Militair'),
		(14, 14, 'nl', 'Natuur en milieu'),
		(15, 15, 'nl', 'Nutsvoorieningen en communicatie'),
		(16, 16, 'nl', 'Oceanen'),
		(17, 17, 'nl', 'Referentiemateriaal aardbedekking'),
		(18, 18, 'nl', 'Ruimtelijke ordening en kadaster'),
		(19, 19, 'nl', 'Transport en logistiek')
;

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

# --- !Downs

DELETE FROM gp.subject_label;
DELETE FROM gp.subject;
DROP MATERIALIZED VIEW IF EXISTS gp.document_search;
