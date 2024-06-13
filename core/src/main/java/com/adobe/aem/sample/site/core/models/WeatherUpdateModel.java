package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.Beans.WeatherDetail;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherUpdateModel {
    private String response;

    @ValueMapValue
    private String location;

    private String name;

    public String getName() {
        return this.name;
    }

    private WeatherDetail weatherDetail = new WeatherDetail();

    private Logger logger = LoggerFactory.getLogger(WeatherUpdateModel.class);

    public WeatherDetail getWeatherDetail() {
        return this.weatherDetail;
    }

    public String getResponse() {
        return this.response;
    }

    public String getLocation() {
        return this.location;
    }

    @PostConstruct
    protected void init() {
        String apiUrl = "https://api.weatherapi.com/v1/current.json?key=a1d83949d50a4bb08b185602242005&q=" + this.location + "&aqi=no";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream inputStream = connection.getInputStream();
            this.response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            this.logger.error("Incorrect url : {}, {}", e.getMessage(), e);
        } catch (IOException e) {
            this.logger.error("Exception : IOException {}", e);
        }
        JsonObject asJsonObject = JsonParser.parseString(this.response).getAsJsonObject();
        JsonObject location = asJsonObject.get("location").getAsJsonObject();
        String name = location.get("name").getAsString();
        String country = location.get("country").getAsString();
        String region = location.get("region").getAsString();
        String localtime = location.get("localtime").getAsString();
        JsonObject current = asJsonObject.get("current").getAsJsonObject();
        String temp_c = current.get("temp_c").getAsString();
        JsonObject condition = current.get("condition").getAsJsonObject();
        String image = condition.get("icon").getAsString();
        this.weatherDetail.setName(name);
        this.weatherDetail.setImage(image);
        this.weatherDetail.setCountry(country);
        this.weatherDetail.setTemp_c(temp_c);
        this.weatherDetail.setRegion(region);
        this.weatherDetail.setLocaltime(localtime);
    }
}