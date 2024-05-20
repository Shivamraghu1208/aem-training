package com.adobe.aem.sample.site.core.services.impl;


import com.adobe.aem.sample.site.core.services.ProductDetailService;
import com.adobe.aem.sample.site.core.services.config.MoviesServiceConfiguration;
import com.adobe.aem.sample.site.core.services.config.ProductDetailServiceConfiguration;
import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component(service = ProductDetailService.class,immediate = true)
@Designate(ocd= ProductDetailServiceConfiguration.class)
public class ProductDetailServiceImpl implements ProductDetailService{

    String response;

    @Override
    public String getResponse() {
        return response;
    }

    @Activate
    @Modified
    protected void activate(ProductDetailServiceConfiguration configuration){
        String apiUrl = configuration.apiUrl();

        try {
          URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream inputStream = connection.getInputStream();
            response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
