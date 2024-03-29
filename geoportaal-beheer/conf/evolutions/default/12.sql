# --- !Ups

CREATE TABLE gb.woo_theme (
    id serial NOT NULL,
    name text NOT NULL,
    CONSTRAINT pk_woo_theme PRIMARY KEY (id),
    CONSTRAINT uq_woo_theme_name UNIQUE (name)
);

CREATE TABLE gb.woo_theme_label (
    id serial NOT NULL,
    woo_theme_id integer NOT NULL,
    locale text NOT NULL,
    label text NOT NULL,
    CONSTRAINT pk_woo_theme_label PRIMARY KEY (id),
    CONSTRAINT uq_woo_theme_label_woo_theme_id_locale UNIQUE (woo_theme_id, locale),
    CONSTRAINT fk_woo_theme_label_woo_theme FOREIGN KEY (woo_theme_id) REFERENCES gb.woo_theme (id)
);

CREATE TABLE gb.md_woo_theme ( 
    id serial NOT NULL,
    metadata_id integer NOT NULL,
    woo_theme integer NOT NULL,
    CONSTRAINT pk_data_woo_theme PRIMARY KEY (id),
    CONSTRAINT uq_md_woo_theme_metadata_id_woo_theme UNIQUE (metadata_id, woo_theme),
    CONSTRAINT fk_md_woo_theme_metadata FOREIGN KEY (metadata_id) REFERENCES gb.metadata (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_md_woo_theme_woo_theme FOREIGN KEY (woo_theme) REFERENCES gb.woo_theme (id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO gb.woo_theme VALUES
    (1, 'counsel'),
    (2, 'agendaExecutiveCouncil'),
    (3, 'accessibilityData'),
    (4, 'order'),
    (5, 'documentReceivedRepresentativeBody'),
    (6, 'covenant'),
    (7, 'annualPlanReport'),
    (8, 'complaintAssessment'),
    (9, 'researchReport'),
    (10, 'draftLegislationRegulations'),
    (11, 'organizationWorkingMethod'),
    (12, 'otherDecisionGeneral'),
    (13, 'subsidyObligation'),
    (14, 'meetingDocumentLowerRepresentativePublicBody'),
    (15, 'lawGenerallyBindingRegulation'),
    (16, 'wooRequest');

INSERT INTO gb.woo_theme_label VALUES
    (1, 1, 'nl', 'advies'),
    (2, 2, 'nl', 'agenda of besluitenlijst bestuurscollege'),
    (3, 3, 'nl', 'bereikbaarheidsgegevens'),
    (4, 4, 'nl', 'beschikking'),
    (5, 5, 'nl', 'bij vertegenwoordigend lichaam ingekomen stuk'),
    (6, 6, 'nl', 'convenant'),
    (7, 7, 'nl', 'jaarplan of jaarverslag'),
    (8, 8, 'nl', 'klachtoordeel'),
    (9, 9, 'nl', 'onderzoeksrapport'),
    (10, 10, 'nl', 'ontwerp wet- en regelgeving'),
    (11, 11, 'nl', 'organisatie en werkwijze'),
    (12, 12, 'nl', 'overig besluit van algemene strekking'),
    (13, 13, 'nl', 'subsidieverplichting'),
    (14, 14, 'nl', 'vergaderstuk lager vertegenwoordigend of openbaar lichaam'),
    (15, 15, 'nl', 'wet of algemeen verbindend voorschrift'),
    (16, 16, 'nl', 'woo-verzoek');

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
    to_tsvector('dutch', coalesce((string_agg(wtl.label, ' ')), '')) ||
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
LEFT JOIN gb.md_woo_theme mwt ON m.id = mwt.metadata_id
LEFT JOIN gb.woo_theme_label wtl ON mwt.woo_theme = wtl.woo_theme_id
LEFT JOIN gb.user u ON m.supplier = u.id
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
LEFT JOIN gb.user u ON m.supplier = u.id
GROUP BY m.id;

CREATE INDEX metadata_search_tsv_idx ON gb.metadata_search USING gin (tsv);
CREATE UNIQUE INDEX metadata_search_metadata_id_idx ON gb.metadata_search USING btree (metadata_id);

DROP TABLE gb.md_woo_theme CASCADE;
DROP TABLE gb.woo_theme_label CASCADE;
DROP TABLE gb.woo_theme CASCADE;
