package ru.flexpay.eirc.service.web.edit;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.complitex.dictionary.entity.Locale;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flexpay.eirc.service.entity.Service;
import ru.flexpay.eirc.service.service.ServiceBean;
import ru.flexpay.eirc.service.web.list.ServiceList;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceEdit extends FormTemplatePage {

    @EJB
    private ServiceBean serviceBean;

    @EJB
    private LocaleBean localeBean;

    private Service service;
    private Service parentService;

    private static final Logger log = LoggerFactory.getLogger(ServiceEdit.class);

    public ServiceEdit() {
        init();
    }

    public ServiceEdit(PageParameters parameters) {
        StringValue serviceId = parameters.get("serviceId");
        if (serviceId != null && !serviceId.isNull()) {
            service = serviceBean.getService(serviceId.toLong());
            if (service == null) {
                throw new RuntimeException("Service by id='" + serviceId + "' not found");
            } else if (service.getParentId() != null) {
                parentService = serviceBean.getService(service.getParentId());
            }
        }
        init();
    }

    private void init() {

        if (service == null) {
            service = new Service();
        }

        final Locale locale = localeBean.convert(getLocale());

        IModel<String> labelModel = new ResourceModel("label");

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");
        add(form);

        // Service code field
        form.add(new TextField<>("code", new PropertyModel<String>(service, "code")));

        // Russian service name
        final Locale localeRu = localeBean.convert(java.util.Locale.forLanguageTag("ru"));
        form.add(new TextField<>("nameRu", new Model<String>() {

            @Override
            public String getObject() {
                return service.getName(localeRu);
            }

            @Override
            public void setObject(String name) {
                service.addName(localeRu, name);
            }
        }).setRequired(true));

        // Ukrainian service name
        final Locale localeUk = localeBean.convert(java.util.Locale.forLanguageTag("uk"));
        form.add(new TextField<>("nameUk", new Model<String>() {

            @Override
            public String getObject() {
                return service.getName(localeUk);
            }

            @Override
            public void setObject(String name) {
                service.addName(localeUk, name);
            }
        }));

        // Parent service
        List<Service> services = serviceBean.getServices(null);
        if (service.getId() != null) {
            services.remove(service);
        }
        if (parentService != null) {
            services.add(null);
        }
        form.add(new DropDownChoice<>("parent", new Model<Service>() {
            @Override
            public Service getObject() {
                return parentService;
            }

            @Override
            public void setObject(Service object) {
                parentService = object;
            }
        }, services, new IChoiceRenderer<Service>() {
            @Override
            public Object getDisplayValue(Service object) {
                return object != null? object.getName(locale) : "";
            }

            @Override
            public String getIdValue(Service object, int index) {
                return object != null? object.getId().toString(): "-1";
            }
        }));

        // save button
        Button save = new Button("save") {

            @Override
            public void onSubmit() {

                if (parentService != null) {
                    service.setParentId(parentService.getId());
                } else {
                    service.setParentId(null);
                }

                if (service.getId() == null) {
                    serviceBean.save(service);
                } else {
                    serviceBean.update(service);
                }

                info(getString("saved"));
            }
        };
        form.add(save);

        // cancel button
        Link<String> cancel = new Link<String>("cancel") {

            @Override
            public void onClick() {
                setResponsePage(ServiceList.class);
            }
        };
        form.add(cancel);
    }
}
