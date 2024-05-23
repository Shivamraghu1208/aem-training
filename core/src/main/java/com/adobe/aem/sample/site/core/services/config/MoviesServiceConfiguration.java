package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name= "Movies Service Configuration")
public @interface MoviesServiceConfiguration {
    @AttributeDefinition(name="Movies Name")
    String[] movies();
}
