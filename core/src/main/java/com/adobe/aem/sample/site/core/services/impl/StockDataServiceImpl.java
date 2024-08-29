package com.adobe.aem.sample.site.core.services.impl;

import com.adobe.aem.sample.site.core.services.StockDataService;
import com.adobe.aem.sample.site.core.services.config.StockDataServiceConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

/**
 * Implementation of the {@link StockDataService} interface.
 * This service provides the path where stock data can be stored.
 */
@Component(
        service = StockDataService.class,
        immediate = true
)
@Designate(ocd = StockDataServiceConfiguration.class)
public class StockDataServiceImpl implements StockDataService {

    private String path;

    /**
     * Returns the path.
     *
     * @return path.
     */
    @Override
    public String getPath() {
        return path;
    }

    /**
     * Updates the path based on the provided configuration.
     *
     * @param stockDataServiceConfiguration the  configuration.
     */
    @Activate
    @Modified
    protected void activate(StockDataServiceConfiguration stockDataServiceConfiguration) {
        this.path = stockDataServiceConfiguration.path();
    }
}
