package ru.flexpay.eirc.dictionary;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Locale;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;

import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 */
public class Organization extends DomainObject {

    public Organization(DomainObject copy) {
        super(copy);
    }

    public List<OrganizationType> getOrganizationTypes() {
        List<Attribute> types = getAttributes(IOrganizationStrategy.ORGANIZATION_TYPE);
        List<OrganizationType> result = Lists.newArrayListWithExpectedSize(types.size());
        for (Attribute type : types) {
            if (type.getAttributeTypeId().equals(OrganizationType.SERVICE_PROVIDER.getId())) {
                result.add(OrganizationType.SERVICE_PROVIDER);
            } else if (type.getAttributeTypeId().equals(OrganizationType.USER_ORGANIZATION.getId())) {
                result.add(OrganizationType.USER_ORGANIZATION);
            }
        }
        return result;
    }

    public boolean isServiceProvider() {
        List<Attribute> types = getAttributes(IOrganizationStrategy.ORGANIZATION_TYPE);
        for (Attribute type : types) {
            if (type.getAttributeTypeId().equals(OrganizationType.SERVICE_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
}
