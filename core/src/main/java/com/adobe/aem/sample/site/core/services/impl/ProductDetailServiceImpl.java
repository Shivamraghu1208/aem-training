package com.adobe.aem.sample.site.core.services.impl;


import com.adobe.aem.sample.site.core.models.ProductDetailModel;
import com.adobe.aem.sample.site.core.services.ProductDetailService;

import com.adobe.aem.sample.site.core.services.config.ProductDetailServiceConfiguration;
import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * ProductDetailServiceImpl is an implementation of the ProductDetailService interface.
 * It retrieve data from an Api specified by configuration and return response.
 */
@Component(service = ProductDetailService.class,immediate = true)
@Designate(ocd= ProductDetailServiceConfiguration.class)
public class ProductDetailServiceImpl implements ProductDetailService{

   private String response;

    private Logger logger = LoggerFactory.getLogger(ProductDetailServiceImpl.class);


    /**
     * Retrieves response from the API.
     * @return  The response from the API as a String.
     */
    @Override
    public String getResponse() {
        return response;
    }
    /**
     * Retrieves data from the API URL specified in the configuration.
     * @param configuration The configuration object.
     */
    @Activate
    @Modified
    protected void activate(ProductDetailServiceConfiguration configuration){
        String apiUrl = configuration.api_url();

        try {
          URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream inputStream = connection.getInputStream();
            response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            logger.error("Incorrect url : {}, {}", e.getMessage(), e);
        } catch (IOException e) {
           logger.error("Exception : IOException {}",e);
        }


    }
}
