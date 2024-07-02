package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 *This ProductDetailServiceConfiguration is a  Configuration interface for defining API URL for the ProductDetailService.
 */
@ObjectClassDefinition(name="Product detail configuration")
public @interface ProductDetailServiceConfiguration {

    /**
     * get the API URL configured for the service.
     * @return The API URL as a String.
     */
    @AttributeDefinition(name="ApiUrl")
    String api_url();

}
