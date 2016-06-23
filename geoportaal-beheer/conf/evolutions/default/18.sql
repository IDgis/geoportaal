# --- !Ups

INSERT INTO gb.user VALUES
	(18, 'AJM.Mulder@overijssel.nl', '$2a$06$m.09JtDrLgPtF.ITxQi5Iu8OE9CTVL1mqROjvnnQ.tKPOF08BwEeC', '2', 'Mulder'),
	(19, 'J.Bouwhuis@overijssel.nl', '$2a$06$anim9BO3v/IQLYVQ84PpKO5z.Ds4jG5GO/t42pc2yT1BeAN6ufv32', '2', 'Bouwhuis'),
	(20, 'MFM.Ellenbroek@overijssel.nl', '$2a$06$L7GxgLiKUniIOW7Ph3DiIOLtsV2jFtOFsPqHVWnMzVp/ISyeaFXhi', '2', 'Ellenbroek'),
	(21, 'MW.Brouwer@overijssel.nl', '$2a$06$uUdmbMBxtktikR8o7nSKg.Jj1a1v7.r94d50N/y7blhpvOr9B3R2S', '2', 'Brouwer'),
	(22, 'RG.Kelder@overijssel.nl', '$2a$06$SILojESxHDwxi9cdH2/klO76ziJc.yv1Xs8rUETk6h.ofdaYXr06O', '2', 'Kelder'),
	(23, 'H.Bendijk-Brinkman@overijssel.nl', '$2a$06$PjmqW99w3l.Tc0pQPDr1K.Bk71y.6e8rfjjr.JBzMm35ou03bDNoG', '2', 'Bendijk-Brinkman'),
	(24, 'B.Balster@overijssel.nl', '$2a$06$3UVTBe/Uyriiin915BmwweTlzNX9qQI8s4ruJG7Phd8zzKvs14/VO', '2', 'Balster'),
	(25, 'M.Broos@overijssel.nl', '$2a$06$GTOECgvd8izmJHF62r5gfeNQpD/51Dvx1zCIp7WIOCQTGlMZPbvuG', '2', 'Broos'),
	(26, 'RG.Groenewold@overijssel.nl', '$2a$06$6dPPB2cmcezFAAsx6eFpSuJVh.MMOKI7jHES/mQn914jjwMd9/0o2', '2', 'Groenewold');

# --- !Downs

DELETE FROM gb.user WHERE id=18;
DELETE FROM gb.user WHERE id=19;
DELETE FROM gb.user WHERE id=20;
DELETE FROM gb.user WHERE id=21;
DELETE FROM gb.user WHERE id=22;
DELETE FROM gb.user WHERE id=23;
DELETE FROM gb.user WHERE id=24;
DELETE FROM gb.user WHERE id=25;
DELETE FROM gb.user WHERE id=26;