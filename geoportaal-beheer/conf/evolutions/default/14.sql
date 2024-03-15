# --- !Ups

delete from gb.theme_label where theme_id between 1 and 17;
delete from gb.theme where id between 1 and 17;

# --- !Downs

insert into gb.theme values
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

insert into gb.theme_label values
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
