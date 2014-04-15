package ru.flexpay.eirc.dictionary.web;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.web.component.DomainObjectComponentUtil;
import org.complitex.dictionary.web.model.AttributeStringModel;
import ru.flexpay.eirc.dictionary.strategy.ModuleInstanceStrategy;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import java.nio.charset.Charset;

/**
 * @author Pavel Sknar
 */
public class ModuleInstancePrivateKeyPanel extends AbstractComplexAttributesPanel {

    @EJB
    private StringCultureBean stringBean;

    @EJB
    private ModuleInstanceStrategy moduleInstanceStrategy;

    public ModuleInstancePrivateKeyPanel(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        DomainObject moduleInstance = getDomainObject();

        addAttributeContainer(ModuleInstanceStrategy.PRIVATE_KEY, false, moduleInstance, "privateKeyContainer", new CallbackButton() {
            @Override
            public void onSubmit(AttributeStringModel attributeStringModel) {
                try {
                    KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
                    SecretKey key = keyGen.generateKey();
                    // Generate a key for the HMAC-SHA1 keyed-hashing algorithm
                    keyGen = KeyGenerator.getInstance("HmacSHA1");
                    key = keyGen.generateKey();
                    attributeStringModel.setObject(new String(Base64.encodeBase64(key.getEncoded()), Charset.forName("UTF-8")));
                } catch (Exception ex) {
                    throw new RuntimeException("Inner error", ex);
                }
            }
        });
    }

    private WebMarkupContainer addAttributeContainer(final long attributeTypeId, boolean disabled,
                                                     DomainObject moduleInstance, String name, final CallbackButton callbackButton) {
        WebMarkupContainer container = new WebMarkupContainer(name);
        container.setOutputMarkupPlaceholderTag(true);
        add(container);
        Attribute attribute = moduleInstance.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setAttributeTypeId(attributeTypeId);
            attribute.setObjectId(moduleInstance.getId());
            attribute.setAttributeId(1L);
            attribute.setLocalizedValues(stringBean.newStringCultures());
        }
        final AttributeStringModel attributeModel = new AttributeStringModel(attribute);
        final EntityAttributeType attributeType =
                moduleInstanceStrategy.getEntity().getAttributeType(attributeTypeId);
        container.add(new Label("label",
                DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
        container.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

        final Component key =
                DomainObjectComponentUtil.newInputComponent("module_instance", getStrategyName(),
                        moduleInstance, attribute, getLocale(), disabled);
        key.setOutputMarkupId(true);
        container.add(key);

        container.add(
                new AjaxButton("update") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        callbackButton.onSubmit(attributeModel);
                        target.add(key);
                    }
                }
        );

        return container;
    }

    protected String getStrategyName() {
        return null;
    }

    public static interface CallbackButton {
        void onSubmit(AttributeStringModel attributeStringModel);
    }
}

