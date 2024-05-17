package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="Product detail configuration")
public @interface ProductDetailServiceConfiguration {
    @AttributeDefinition(name="ApiUrl")
    String apiUrl();

}
