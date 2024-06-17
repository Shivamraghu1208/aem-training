package com.adobe.aem.sample.site.core.services;

import org.json.JSONObject;

import java.util.Map;

public interface TokenDetailService {

    Map<String,String> getTokenDetails(String token);
    void storeTokenDetails(String token, String name,String email);
    String getResourcePath();
}
