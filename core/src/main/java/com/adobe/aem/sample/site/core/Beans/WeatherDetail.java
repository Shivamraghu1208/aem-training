package com.adobe.aem.sample.site.core.Beans;

public class WeatherDetail {
    private String name;

    private String country;

    private String region;

    private String localtime;

    private String temp_c;

    private String image;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLocaltime() {
        return this.localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public String getTemp_c() {
        return this.temp_c;
    }

    public void setTemp_c(String temp_c) {
        this.temp_c = temp_c;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}