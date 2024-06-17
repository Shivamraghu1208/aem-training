package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Detail Service Configuration ")
public @interface FindDetailServiceConfiguration {

    @AttributeDefinition(name = "Path")
    String path();

}
