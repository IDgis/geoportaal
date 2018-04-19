# --- !Ups

INSERT INTO gb.md_format VALUES
	(11, 'excel'),
	(12, 'photoshop'),
	(13, 'powerpoint'),
	(14, 'word');
	
INSERT INTO gb.md_format_label VALUES
	(11, 11, 'nl', 'Excel: XLS XLSX'),
	(12, 12, 'nl', 'Photoshop: PSD PSB'),
	(13, 13, 'nl', 'Powerpoint: PPT PPTX'),
	(14, 14, 'nl', 'Word: DOC DOCX');

update gb.md_format set name = 'paintshopPro' where name = 'paintshop';
update gb.md_format_label set label = 'PaintShopPro: PSP' where label = 'PaintShop: PSP';

INSERT INTO gb.type_information VALUES
	(9, 'dataset'),
	(10, 'chart'),
	(11, 'infographic'),
	(12, 'tableauDashboard'),
	(13, 'tableauPackagedWorkbook'),
	(14, 'text'),
	(15, 'collectionMultiple');
	
INSERT INTO gb.type_information_label VALUES
	(9, 9, 'nl', 'Dataset'),
	(10, 10, 'nl', 'Grafiek'),
	(11, 11, 'nl', 'Infographic'),
	(12, 12, 'nl', 'Tableau Dashboard'),
	(13, 13, 'nl', 'Tableau Packaged Workbook'),
	(14, 14, 'nl', 'Tekst'),
	(15, 15, 'nl', 'Verzameling van meerdere typen');
	
# --- !Downs

delete from gb.md_format_label where id >= 11 and id <= 14;
delete from gb.md_format where id >= 11 and id <= 14;
delete from gb.type_information_label where id >= 9 and id <= 15;
delete from gb.type_information where id >= 9 and id <= 15;

update gb.md_format set name = 'paintshop' where name = 'paintshopPro';
update gb.md_format_label set label = 'PaintShop: PSP' where label = 'PaintShopPro: PSP';