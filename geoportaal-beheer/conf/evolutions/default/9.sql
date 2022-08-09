# --- !Ups

CREATE TABLE gb.theme (
    id serial NOT NULL,
    name text NOT NULL,
    CONSTRAINT pk_theme PRIMARY KEY (id),
    CONSTRAINT uq_theme_name UNIQUE (name)
);

CREATE TABLE gb.theme_label (
    id serial NOT NULL,
    theme_id integer NOT NULL,
    locale text NOT NULL,
    label text NOT NULL,
    CONSTRAINT pk_theme_label PRIMARY KEY (id),
    CONSTRAINT uq_theme_label_theme_id_locale UNIQUE (theme_id, locale),
    CONSTRAINT fk_theme_label_theme FOREIGN KEY (theme_id) REFERENCES gb.theme (id)
);

CREATE TABLE gb.md_theme ( 
    id serial NOT NULL,
    metadata_id integer NOT NULL,
    theme integer NOT NULL,
    CONSTRAINT pk_data_theme PRIMARY KEY (id),
    CONSTRAINT uq_md_theme_metadata_id_theme UNIQUE (metadata_id, theme),
    CONSTRAINT fk_md_theme_metadata FOREIGN KEY (metadata_id) REFERENCES gb.metadata (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_md_theme_theme FOREIGN KEY (theme) REFERENCES gb.theme (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE gb.type_research (
    id serial NOT NULL,
    name text NOT NULL,
    CONSTRAINT pk_type_research PRIMARY KEY (id),
    CONSTRAINT uq_type_research_name UNIQUE (name)
);

CREATE TABLE gb.type_research_label (
    id serial NOT NULL,
    type_research_id integer NOT NULL,
    locale text NOT NULL,
    label text NOT NULL,
    CONSTRAINT pk_type_research_label PRIMARY KEY (id),
    CONSTRAINT uq_type_research_label_type_research_id_locale UNIQUE (type_research_id, locale),
    CONSTRAINT fk_type_research_label_type_research FOREIGN KEY (type_research_id) REFERENCES gb.type_research (id)
);

INSERT INTO gb.theme VALUES
    (1, 'agroFood'),
    (2, 'jobMarket'),
    (3, 'culture'),
    (4, 'economy'),
    (5, 'energy'),
    (6, 'agriculture'),
    (7, 'environment'),
    (8, 'nature'),
    (9, 'publicAdministration'),
    (10, 'retail'),
    (11, 'spatialPlanning'),
    (12, 'social'),
    (13, 'toerism'),
    (14, 'traffic'),
    (15, 'transport'),
    (16, 'water'),
    (17, 'inhabit');

INSERT INTO gb.theme_label VALUES
    (1, 1, 'nl', 'Agro-food'),
    (2, 2, 'nl', 'Arbeidsmarkt'),
    (3, 3, 'nl', 'Cultuur'),
    (4, 4, 'nl', 'Economie'),
    (5, 5, 'nl', 'Energie'),
    (6, 6, 'nl', 'Landbouw'),
    (7, 7, 'nl', 'Milieu'),
    (8, 8, 'nl', 'Natuur'),
    (9, 9, 'nl', 'Openbaar bestuur'),
    (10, 10, 'nl', 'Retail'),
    (11, 11, 'nl', 'Ruimtelijke ordening'),
    (12, 12, 'nl', 'Sociaal'),
    (13, 13, 'nl', 'Toerisme'),
    (14, 14, 'nl', 'Verkeer'),
    (15, 15, 'nl', 'Vervoer'),
    (16, 16, 'nl', 'Water'),
    (17, 17, 'nl', 'Wonen');

INSERT INTO gb.type_research VALUES
    (1, 'none'),
    (2, 'policyReport'),
    (3, 'researchReport'),
    (4, 'note');

INSERT INTO gb.type_research_label VALUES
    (1, 1, 'nl', ''),
    (2, 2, 'nl', 'Beleidsrapport'),
    (3, 3, 'nl', 'Onderzoeksrapport'),
    (4, 4, 'nl', 'Notitie');

ALTER TABLE gb.metadata ADD COLUMN type_research integer NOT NULL default 1;
ALTER TABLE gb.metadata ADD CONSTRAINT fk_metadata_type_research FOREIGN KEY (type_research) REFERENCES gb.type_research (id);

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
    to_tsvector('dutch', coalesce((string_agg(trl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(cl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(m.creator_other, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(rl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(ull.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(mfl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(tl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(u.label, ' ')), '')) tsv
FROM gb.metadata m
LEFT JOIN gb.md_attachment ma ON m.id = ma.metadata_id
LEFT JOIN gb.type_information_label til ON m.type_information = til.type_information_id
LEFT JOIN gb.type_research_label trl ON m.type_research = trl.type_research_id
LEFT JOIN gb.creator_label cl ON m.creator = cl.creator_id
LEFT JOIN gb.rights_label rl ON m.rights = rl.rights_id
LEFT JOIN gb.use_limitation_label ull ON m.use_limitation = ull.use_limitation_id
LEFT JOIN gb.md_format_label mfl ON m.md_format = mfl.md_format_id
LEFT JOIN gb.md_subject ms ON m.id = ms.metadata_id
LEFT JOIN gb.subject_label sl ON ms.subject = sl.subject_id
LEFT JOIN gb.md_theme mt ON m.id = mt.metadata_id
LEFT JOIN gb.theme_label tl ON mt.theme = tl.theme_id
LEFT JOIN gb.user u ON m.supplier=u.id
GROUP BY m.id;

CREATE INDEX metadata_search_tsv_idx ON gb.metadata_search USING gin (tsv);
CREATE UNIQUE INDEX metadata_search_metadata_id_idx ON gb.metadata_search USING btree (metadata_id);

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
    to_tsvector('dutch', coalesce((string_agg(mfl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(m.source, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(sl.label, ' ')), '')) ||
    to_tsvector('dutch', coalesce((string_agg(u.label, ' ')), '')) tsv
FROM gb.metadata m
LEFT JOIN gb.md_attachment ma ON m.id = ma.metadata_id
LEFT JOIN gb.type_information_label til ON m.type_information = til.type_information_id
LEFT JOIN gb.creator_label cl ON m.creator = cl.creator_id
LEFT JOIN gb.rights_label rl ON m.rights = rl.rights_id
LEFT JOIN gb.use_limitation_label ull ON m.use_limitation = ull.use_limitation_id
LEFT JOIN gb.md_format_label mfl ON m.md_format = mfl.md_format_id
LEFT JOIN gb.md_subject ms ON m.id = ms.metadata_id
LEFT JOIN gb.subject_label sl ON ms.subject = sl.subject_id
LEFT JOIN gb.user u ON m.supplier = u.id
GROUP BY m.id;

CREATE INDEX metadata_search_tsv_idx ON gb.metadata_search USING gin (tsv);
CREATE UNIQUE INDEX metadata_search_metadata_id_idx ON gb.metadata_search USING btree (metadata_id);

DROP TABLE gb.md_theme CASCADE;
DROP TABLE gb.theme_label CASCADE;
DROP TABLE gb.theme CASCADE;

ALTER TABLE gb.metadata DROP CONSTRAINT fk_metadata_type_research;
ALTER TABLE gb.metadata DROP COLUMN type_research;

DROP TABLE gb.type_research_label CASCADE;
DROP TABLE gb.type_research CASCADE;
