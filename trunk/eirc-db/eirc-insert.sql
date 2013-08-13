INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('eirc_account',1);

-- --------------------------------
-- Organization type
-- --------------------------------
INSERT INTO `organization_type`(`object_id`) VALUES (2);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`) VALUES (2, 1, UPPER('Поставщик услуг')), (2, 2,UPPER('Постачальник послуг'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES (1,2,2300,2,2300);

-- --------------------------------
-- Organization
-- --------------------------------

-- Reference to jdbc data source. It is calculation center only attribute. --
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (914, 1, UPPER('КПП')), (914, 2, UPPER('КПП'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (913, 900, 1, 914, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (913, 913, UPPER('string'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (915, 1, UPPER('ИНН')), (915, 2, UPPER('ІПН'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (914, 900, 1, 915, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (914, 914, UPPER('string'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (916, 1, UPPER('Примечание')), (916, 2, UPPER('Примітка'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (915, 900, 1, 916, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (915, 915, UPPER('string'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (917, 1, UPPER('Юридический адрес')), (917, 2, UPPER('Юридична адреса'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (916, 900, 1, 917, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (916, 916, UPPER('string'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (918, 1, UPPER('Почтовый адрес')), (918, 2, UPPER('Поштова адреса'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (917, 900, 1, 918, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (917, 917, UPPER('string'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (919, 1, UPPER('E-mail')), (919, 2, UPPER('E-mail'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (918, 900, 1, 919, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (918, 918, UPPER('string'));

-- Current database version
 INSERT INTO `update` (`version`) VALUE ('20130718_1_0.0.1');