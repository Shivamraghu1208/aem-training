package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Page UnPublish Scheduler",
        description = "Page UnPublish Scheduler Configuration"
)
public @interface PageUnpublishSchedulerConfiguration {

    @AttributeDefinition(name = "Scheduler name")
    public String scheduler_name();

    @AttributeDefinition(name = "cron name")
    public String scheduler_expression();

    @AttributeDefinition(name = "ApiUrl")
    public String api_url();
}
