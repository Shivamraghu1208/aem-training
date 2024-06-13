package com.adobe.aem.sample.site.core.services;

import org.json.JSONObject;

import java.util.Map;

public interface FindDetailService {

    Map<String,String> getMapOfToken(String token);
    public void addData(String token, String name,String email);
}
