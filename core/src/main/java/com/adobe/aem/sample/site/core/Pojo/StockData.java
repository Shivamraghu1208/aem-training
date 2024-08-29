package com.adobe.aem.sample.site.core.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockData {
    private String date;
    private String company;
    private String description;
    private double opening_price;
    private double closing_price;
    private String symbol;

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOpening_price() {
        return opening_price;
    }

    public void setOpening_price(double opening_price) {
        this.opening_price = opening_price;
    }

    public double getClosing_price() {
        return closing_price;
    }

    public void setClosing_price(double closing_price) {
        this.closing_price = closing_price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

   
}

