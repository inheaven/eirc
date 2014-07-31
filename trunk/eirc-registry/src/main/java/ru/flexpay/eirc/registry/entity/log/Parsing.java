package ru.flexpay.eirc.registry.entity.log;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * @author Pavel Sknar
 */
@BaseName("ru.flexpay.eirc.registry.entity.log.parsing")
@LocaleData(defaultCharset = "UTF-8", value = {@Locale("ru"), @Locale("en")})
public enum Parsing {
    INNER_ERROR,
    INNER_ERROR_IN_REGISTRY,
    STARTING_UPLOAD_REGISTRIES,
    FILES_NOT_FOUND,
    STARTING_UPLOAD_REGISTRY,
    REGISTRY_FINISH_UPLOAD,
    FILE_IS_NOT_REGISTRY,
    REGISTRY_CREATED,
    REGISTRY_FAILED_UPLOAD,
    REGISTRY_FAILED_UPLOAD_WITH_ERROR,
    REGISTRY_CREATING,
    REGISTRY_RECORD_UPLOAD,
    UNKNOWN_REGISTRY_TYPE,
    HEADER_PARSE_ERROR,
    HEADER_INVALID_NUMBER_FIELDS,
    REGISTRY_WAS_ALREADY_UPLOADED,
    RECIPIENT_NOT_FOUND,
    SENDER_NOT_FOUND,
    ORGANIZATION_NOT_SERVICE_PROVIDER,
    ORGANIZATION_NOT_PAYMENT_COLLECTOR,
    REGISTRY_RECORDS_NUMBER_ERROR,
    TOTAL_AMOUNT_ERROR,
    FOOTER_INVALID_NUMBER_FIELDS,
    RECIPIENT_NOT_EIRC_ORGANIZATION,
    EIRC_ORGANIZATION_ID_NOT_DEFINED,
    RECORD_INCORRECT_NUMBER_FIELDS,

}
