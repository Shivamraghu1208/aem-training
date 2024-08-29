package com.adobe.aem.sample.site.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 *
 *Configuration interface - This interface defines the configuration for the Stock Data Service,
 * which provides a path where stock data can be stored.
 */

@ObjectClassDefinition(name= "Stock DataService",description = "Provide a Service Related stock data")
public @interface StockDataServiceConfiguration {

    /**
     * Defines the path where stock data can be stored.
     *
     * @return path.
     */
    @AttributeDefinition(name = "Path service")
    String path();
}
