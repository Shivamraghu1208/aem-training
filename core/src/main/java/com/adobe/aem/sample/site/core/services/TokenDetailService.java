package com.adobe.aem.sample.site.core.services;

import org.json.JSONObject;

import java.util.Map;
/**
 * Service interface for managing token details.
 */
public interface TokenDetailService {

    /**
     * retrieve a token details.
     * @param token token to lookup.
     * @return A map object with name and email.
     */
    Map<String,String> getTokenDetails(String token);

    /**
     *To store a token details.
     * @param token
     * @param name
     * @param email
     */
    void storeTokenDetails(String token, String name,String email);

    /**
     * retrieve a resource path where token details is present.
     * @return resource path as String.
     */
    String getResourcePath();
}
