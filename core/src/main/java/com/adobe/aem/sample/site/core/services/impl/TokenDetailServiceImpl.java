package com.adobe.aem.sample.site.core.services.impl;

import com.adobe.aem.sample.site.core.services.TokenDetailService;
import com.adobe.aem.sample.site.core.services.config.TokenDetailServiceConfiguration;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the TokenDetailService.
 * This service is responsible for managing token details including storing and retrieving them.
 */
@Component(service = TokenDetailService.class, immediate = true)
@Designate(ocd = TokenDetailServiceConfiguration.class)
public class TokenDetailServiceImpl implements TokenDetailService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    /**
     * The path - Resource path where token details is stored.
     */
    private String path;

    /**
     * The logger - A logger instance used for Logging messages related to TokenDetailServiceImpl.
     */
    private Logger logger = LoggerFactory.getLogger(TokenDetailServiceImpl.class);

    /**
     * The tokenDetailsMap - A map object used to store a token and
     * token details which is another map object contains name and email.
     */
    private Map<String, Map<String, String>> tokenDetailsMap = new HashMap<>();

    /**
     * The nameEmailDetailsMap - A map object used to store a name and email.
     */
    private Map<String, String> nameEmailDetailsMap = new HashMap<>();

    /**
     * This method retrieves the values from the node and store in the tokenDetailsMap.
     * @param configuration - The configuration for this service.
     */
    @Activate
    @Modified
    protected void activate(TokenDetailServiceConfiguration configuration) {
        path = configuration.path();
        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                ValueMap valueMap = resource.getValueMap();
                if (valueMap != null) {
                    valueMap.forEach((key, value) -> {
                        if (!key.equals("jcr:primaryType")) {
                            String jsonString = new Gson().toJson(value);
                            if (JsonParser.parseString(JsonParser.parseString(jsonString).getAsString()).isJsonObject()) {
                                JsonObject jsonObject = JsonParser.parseString(JsonParser.parseString(jsonString).getAsString()).getAsJsonObject();
                                String name = jsonObject.get("name").getAsString();
                                String email = jsonObject.get("email").getAsString();
                                String token = jsonObject.get("token").getAsString();
                                nameEmailDetailsMap.put("name", name);
                                nameEmailDetailsMap.put("email", email);
                                tokenDetailsMap.put(token, nameEmailDetailsMap);

                            }
                        }

                    });
                }
            }
        } catch (LoginException e) {
            logger.error("Login Exception {}", e);
        }
    }

    /**
     * Stores the token details in tokenDetailsMap.
     *
     * @param token The token to store.
     * @param name The name associated with the token.
     * @param email The email associated with the token.
     */
    public void storeTokenDetails(String token, String name, String email) {
        nameEmailDetailsMap.put("name", name);
        nameEmailDetailsMap.put("email", email);
        tokenDetailsMap.put(token, nameEmailDetailsMap);
    }


    /**
     * Retrieves the resource path where token details are stored.
     *
     * @return The resource path.
     */
    @Override
    public String getResourcePath() {
        return path;
    }

    /**
     * Retrieves the token details for the given token.
     *
     * @param token The token to lookup.
     * @return A map with name and email linked with that token.
     */
    @Override
    public Map<String, String> getTokenDetails(String token) {
        return tokenDetailsMap.get(token);
    }
}
