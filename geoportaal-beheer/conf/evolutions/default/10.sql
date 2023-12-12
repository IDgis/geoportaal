# --- !Ups

update gb.type_research set name = 'research' where (id = 2);
update gb.type_research_label set label = 'Onderzoek' where (type_research_id = 2);

update gb.type_research set name = 'advice' where (id = 3);
update gb.type_research_label set label = 'Advies' where (type_research_id = 3);

update gb.type_research set name = 'policyEvaluation' where (id = 4);
update gb.type_research_label set label = 'Beleidsevaluatie' where (type_research_id = 4);

insert into gb.type_research values
    (5, 'policyPreparingResearch'),
    (6, 'auditsInternal'),
    (7, 'inventory'),
    (8, 'environmentalImpactReport'),
    (9, 'prognosis'),
    (10, 'monitoring'),
    (11, 'analysis'),
    (12, 'wooDocument');

insert into gb.type_research_label values
    (5, 5, 'nl', 'Beleidsvoorbereidend onderzoek'),
    (6, 6, 'nl', 'Audits (intern)'),
    (7, 7, 'nl', 'Inventarisatie'),
    (8, 8, 'nl', 'MER (Milieueffectrapportage)'),
    (9, 9, 'nl', 'Prognose'),
    (10, 10, 'nl', 'Monitoring'),
    (11, 11, 'nl', 'Analyse'),
    (12, 12, 'nl', 'WOO-document');

# --- !Downs

update gb.type_research set name = 'policyReport' where (id = 2);
update gb.type_research_label set label = 'Beleidsrapport' where (type_research_id = 2);

update gb.type_research set name = 'researchReport' where (id = 3);
update gb.type_research_label set label = 'Onderzoeksrapport' where (type_research_id = 3);

update gb.type_research set name = 'note' where (id = 4);
update gb.type_research_label set label = 'Notitie' where (type_research_id = 4);

delete from gb.type_research_label where type_research_id between 5 and 12;
delete from gb.type_research where id between 5 and 12;
