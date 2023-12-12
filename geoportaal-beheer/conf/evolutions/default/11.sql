# --- !Ups

insert into gb.theme values
	(18, 'businessOperationsOrganization'),
	(19, 'administrativeApparatus'),
	(20, 'interadministrativeSupervision'),
	(21, 'ruralArea'),
	(22, 'urbanizationDemographyHousing'),
	(23, 'spatialPlans'),
	(24, 'realEstateUseOfSpace'),
	(25, 'agricultureAgrofood'),
	(26, 'environmentalPolicyEnforcement'),
	(27, 'natureLandscape'),
	(28, 'floraFauna'),
	(29, 'nitrogen'),
	(30, 'waters'),
	(31, 'ground'),
	(32, 'businessInnovationEntrepeneurship'),
	(33, 'laborMarketEducation'),
	(34, 'collaborationInternationalization'),
	(35, 'leisureEconomy'),
	(36, 'energies'),
	(37, 'circularEconomy'),
	(38, 'climate'),
	(39, 'publicTransport'),
	(40, 'infrastructure'),
	(41, 'mobilityPolicy'),
	(42, 'healthSportsCare'),
	(43, 'socialQuality'),
	(44, 'cultureHeritage');

insert into gb.theme_label values
	(18, 18, 'nl', 'Bedrijfsvoering en organisatie'),
	(19, 19, 'nl', 'Bestuurlijke inrichting en samenwerking'),
	(20, 20, 'nl', 'Interbestuurlijk toezicht'),
	(21, 21, 'nl', 'Landelijk gebied'),
	(22, 22, 'nl', 'Verstedelijking, demografie en wonen'),
	(23, 23, 'nl', 'Ruimtelijke plannen'),
	(24, 24, 'nl', 'Vastgoed en ruimtegebruik'),
	(25, 25, 'nl', 'Landbouw en agrofood'),
	(26, 26, 'nl', 'Milieubeleid en handhaving'),
	(27, 27, 'nl', 'Natuur en landschap'),
	(28, 28, 'nl', 'Flora en fauna'),
	(29, 29, 'nl', 'Stikstof'),
	(30, 30, 'nl', 'Water'),
	(31, 31, 'nl', 'Bodem'),
	(32, 32, 'nl', 'Bedrijfsleven, innovatie en ondernemerschap'),
	(33, 33, 'nl', 'Arbeidsmarkt en onderwijs'),
	(34, 34, 'nl', 'Samenwerking en internationalisering'),
	(35, 35, 'nl', 'Vrijetijdseconomie'),
	(36, 36, 'nl', 'Energie'),
	(37, 37, 'nl', 'Circulaire economie'),
	(38, 38, 'nl', 'Klimaat'),
	(39, 39, 'nl', 'Openbaar vervoer'),
	(40, 40, 'nl', 'Infrastructuur'),
	(41, 41, 'nl', 'Mobiliteitsbeleid'),
	(42, 42, 'nl', 'Gezondheid, sport en zorg'),
	(43, 43, 'nl', 'Sociale kwaliteit'),
	(44, 44, 'nl', 'Cultuur en erfgoed');

# --- !Downs

delete from gb.theme_label where theme_id between 18 and 44;
delete from gb.theme where id between 18 and 44;
