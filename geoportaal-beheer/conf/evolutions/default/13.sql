# --- !Ups

insert into gb.theme values
	(45, 'natura2000');

insert into gb.theme_label values
	(45, 45, 'nl', '3.5 Natura 2000');

update gb.theme_label set label = '1.1 Bedrijfsvoering en organisatie' where (label = 'Bedrijfsvoering en organisatie');
update gb.theme_label set label = '1.2 Bestuurlijke inrichting en samenwerking' where (label = 'Bestuurlijke inrichting en samenwerking');
update gb.theme_label set label = '1.3 Interbestuurlijk toezicht' where (label = 'Interbestuurlijk toezicht');
update gb.theme_label set label = '2.1 Landelijk gebied' where (label = 'Landelijk gebied');
update gb.theme_label set label = '2.2 Verstedelijking, demografie en wonen' where (label = 'Verstedelijking, demografie en wonen');
update gb.theme_label set label = '2.3 Ruimtelijke plannen' where (label = 'Ruimtelijke plannen');
update gb.theme_label set label = '2.4 Vastgoed en ruimtegebruik' where (label = 'Vastgoed en ruimtegebruik');
update gb.theme_label set label = '3.1 Landbouw en agrofood' where (label = 'Landbouw en agrofood');
update gb.theme_label set label = '3.2 Milieubeleid en handhaving' where (label = 'Milieubeleid en handhaving');
update gb.theme_label set label = '3.3 Natuur en landschap' where (label = 'Natuur en landschap');
update gb.theme_label set label = '3.4 Flora en fauna' where (label = 'Flora en fauna');
update gb.theme_label set label = '3.6 Stikstof' where (label = 'Stikstof');
update gb.theme_label set label = '3.7 Water' where (label = 'Water');
update gb.theme_label set label = '3.8 Bodem' where (label = 'Bodem');
update gb.theme_label set label = '3.9 Klimaat' where (label = 'Klimaat');
update gb.theme_label set label = '4.1 Bedrijfsleven, innovatie en ondernemerschap' where (label = 'Bedrijfsleven, innovatie en ondernemerschap');
update gb.theme_label set label = '4.2 Arbeidsmarkt en onderwijs' where (label = 'Arbeidsmarkt en onderwijs');
update gb.theme_label set label = '4.3 Samenwerking en internationalisering' where (label = 'Samenwerking en internationalisering');
update gb.theme_label set label = '4.4 Vrijetijdseconomie' where (label = 'Vrijetijdseconomie');
update gb.theme_label set label = '5.1 Energie' where (label = 'Energie');
update gb.theme_label set label = '5.2 Circulaire economie' where (label = 'Circulaire economie');
update gb.theme_label set label = '6.1 Openbaar vervoer' where (label = 'Openbaar vervoer');
update gb.theme_label set label = '6.2 Infrastructuur' where (label = 'Infrastructuur');
update gb.theme_label set label = '6.3 Mobiliteitsbeleid' where (label = 'Mobiliteitsbeleid');
update gb.theme_label set label = '7.1 Gezondheid, sport en zorg' where (label = 'Gezondheid, sport en zorg');
update gb.theme_label set label = '7.2 Sociale kwaliteit' where (label = 'Sociale kwaliteit');
update gb.theme_label set label = '7.3 Cultuur en erfgoed' where (label = 'Cultuur en erfgoed');

# --- !Downs

delete from gb.theme_label where id = 45;
delete from gb.theme where id = 45;

update gb.theme_label set label = 'Bedrijfsvoering en organisatie' where (label = '1.1 Bedrijfsvoering en organisatie');
update gb.theme_label set label = 'Bestuurlijke inrichting en samenwerking' where (label = '1.2 Bestuurlijke inrichting en samenwerking');
update gb.theme_label set label = 'Interbestuurlijk toezicht' where (label = '1.3 Interbestuurlijk toezicht');
update gb.theme_label set label = 'Landelijk gebied' where (label = '2.1 Landelijk gebied');
update gb.theme_label set label = 'Verstedelijking, demografie en wonen' where (label = '2.2 Verstedelijking, demografie en wonen');
update gb.theme_label set label = 'Ruimtelijke plannen' where (label = '2.3 Ruimtelijke plannen');
update gb.theme_label set label = 'Vastgoed en ruimtegebruik' where (label = '2.4 Vastgoed en ruimtegebruik');
update gb.theme_label set label = 'Landbouw en agrofood' where (label = '3.1 Landbouw en agrofood');
update gb.theme_label set label = 'Milieubeleid en handhaving' where (label = '3.2 Milieubeleid en handhaving');
update gb.theme_label set label = 'Natuur en landschap' where (label = '3.3 Natuur en landschap');
update gb.theme_label set label = 'Flora en fauna' where (label = '3.4 Flora en fauna');
update gb.theme_label set label = 'Stikstof' where (label = '3.6 Stikstof');
update gb.theme_label set label = 'Water' where (label = '3.7 Water');
update gb.theme_label set label = 'Bodem' where (label = '3.8 Bodem');
update gb.theme_label set label = 'Klimaat' where (label = '3.9 Klimaat');
update gb.theme_label set label = 'Bedrijfsleven, innovatie en ondernemerschap' where (label = '4.1 Bedrijfsleven, innovatie en ondernemerschap');
update gb.theme_label set label = 'Arbeidsmarkt en onderwijs' where (label = '4.2 Arbeidsmarkt en onderwijs');
update gb.theme_label set label = 'Samenwerking en internationalisering' where (label = '4.3 Samenwerking en internationalisering');
update gb.theme_label set label = 'Vrijetijdseconomie' where (label = '4.4 Vrijetijdseconomie');
update gb.theme_label set label = 'Energie' where (label = '5.1 Energie');
update gb.theme_label set label = 'Circulaire economie' where (label = '5.2 Circulaire economie');
update gb.theme_label set label = 'Openbaar vervoer' where (label = '6.1 Openbaar vervoer');
update gb.theme_label set label = 'Infrastructuur' where (label = '6.2 Infrastructuur');
update gb.theme_label set label = 'Mobiliteitsbeleid' where (label = '6.3 Mobiliteitsbeleid');
update gb.theme_label set label = 'Gezondheid, sport en zorg' where (label = '7.1 Gezondheid, sport en zorg');
update gb.theme_label set label = 'Sociale kwaliteit' where (label = '7.2 Sociale kwaliteit');
update gb.theme_label set label = 'Cultuur en erfgoed' where (label = '7.3 Cultuur en erfgoed');
