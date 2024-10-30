# --- !Ups

ALTER TABLE gb.woo_theme ADD COLUMN information_category text NOT NULL default '000';
UPDATE gb.woo_theme SET information_category = '010' WHERE name = 'counsel';
UPDATE gb.woo_theme SET information_category = '009' WHERE name = 'agendaExecutiveCouncil';
UPDATE gb.woo_theme SET information_category = '005' WHERE name = 'accessibilityData';
UPDATE gb.woo_theme SET information_category = '016' WHERE name = 'order';
UPDATE gb.woo_theme SET information_category = '006' WHERE name = 'documentReceivedRepresentativeBody';
UPDATE gb.woo_theme SET information_category = '011' WHERE name = 'covenant';
UPDATE gb.woo_theme SET information_category = '012' WHERE name = 'annualPlanReport';
UPDATE gb.woo_theme SET information_category = '017' WHERE name = 'complaintAssessment';
UPDATE gb.woo_theme SET information_category = '015' WHERE name = 'researchReport';
UPDATE gb.woo_theme SET information_category = '003' WHERE name = 'draftLegislationRegulations';
UPDATE gb.woo_theme SET information_category = '004' WHERE name = 'organizationWorkingMethod';
UPDATE gb.woo_theme SET information_category = '002' WHERE name = 'otherDecisionGeneral';
UPDATE gb.woo_theme SET information_category = '013' WHERE name = 'subsidyObligation';
UPDATE gb.woo_theme SET information_category = '008' WHERE name = 'meetingDocumentLowerRepresentativePublicBody';
UPDATE gb.woo_theme SET information_category = '001' WHERE name = 'lawGenerallyBindingRegulation';
UPDATE gb.woo_theme SET information_category = '014' WHERE name = 'wooRequest';


# --- !Downs

ALTER TABLE gb.woo_theme DROP COLUMN information_category;
