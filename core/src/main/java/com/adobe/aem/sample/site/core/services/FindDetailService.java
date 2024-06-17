package com.adobe.aem.sample.site.core.services;

import org.json.JSONObject;

import java.util.Map;

public interface FindDetailService {

    Map<String,String> getData(String token);
    public void addData(String token, String name,String email);
    public String getPath();
}
