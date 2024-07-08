package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.services.TokenDetailService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A servlet that generates unique token and retrieves token details.
 * If the name and email parameters are provided, it generates a new token or get an existing one.
 * If the token parameter is provided, it retrieves the token details.
 */
@Component(service = {Servlet.class}, property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=aemtraining/components/getkey",
        "sling.servlet.paths=/bin/getToken",
        "sling.servlet.extensions=json",
        "sling.servlet.selectors=key"
})
public class GenerateKey extends SlingSafeMethodsServlet {

    /**
     * The tokenDetailService - A TokenDetailService object
     */
    @Reference
    private transient TokenDetailService tokenDetailService;

    /**
     * The resourceResolverFactory - A ResourceResolverFactory object
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The log - A logger instance used for Logging messages related to ComponentReportServlet.
     */
    private Logger log = LoggerFactory.getLogger(ComponentReportServlet.class);

    /**
     * Handles GET requests and response will be token details or generates a new token.
     *
     * @param request  the  SlingHttpServletRequest object
     * @param response the SlingHttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String responseObj = "";
        JsonObject responseJson = new JsonObject();
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String path = tokenDetailService.getResourcePath();
        String token = request.getParameter("token");
        
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(email)) {
            Map<String, Object> params = new HashMap<>();
            params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
            ResourceResolver resourceResolver = null;
            try {
                resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            } catch (LoginException e) {
                log.error("Login Exception {}", e.getMessage(), e);
            }
            if (resourceResolver != null) {
                Resource resource = resourceResolver.getResource(path);
                if (resource != null) {
                    String uniqueToken = UUID.randomUUID().toString();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("token", uniqueToken);
                        jsonObject.put("email", email);
                        jsonObject.put("name", name);
                        ValueMap valueMap = resource.getValueMap();
                        responseJson = checkForTokenInNode(email, resource, valueMap, resourceResolver, jsonObject, response, uniqueToken, name);
                        resourceResolver.close();
                    } catch (JSONException e) {
                        log.error("Json Exception {}", e.getMessage(), e);
                    }

                }
            }

            } else if (StringUtils.isNotBlank(token)) {
                JsonObject jsonObjectValidation = new JsonObject();
                Map<String, String> stringValueMap = tokenDetailService.getTokenDetails(token);
                if (stringValueMap != null && !stringValueMap.isEmpty()) {
                    String name1 = stringValueMap.get("name");
                    String email1 = stringValueMap.get("email");

                    jsonObjectValidation.addProperty("name", name1);
                    jsonObjectValidation.addProperty("email", email1);
                    jsonObjectValidation.addProperty("status", "Valid Token");
                    responseJson.add("result", jsonObjectValidation);

                } else {

                    jsonObjectValidation.addProperty("status", "Error");
                    jsonObjectValidation.addProperty("message", "Invalid Token");
                    responseJson.add("result", jsonObjectValidation);
                }
            }

            response.setContentLength(responseJson.toString().getBytes().length);
            response.setContentType("application/json");
            response.getOutputStream().write(responseJson.toString().getBytes());
        }

    /**
     * This method check weather token is already present or not
     * if token is not present the it generate a unique token or
     * if it is present then return the existing token.
     * @param email - The email address to check for an existing token.
     * @param resource - The resource where the token information is stored
     * @param valueMap - The value map containing the existing token details.
     * @param resourceResolver - The resource resolver for committing changes.
     * @param jsonObject - The JSON object containing the token details to store.
     * @param token - The token to store if it is not already present.
     * @param name - The name associated with the token.
     * @throws IOException
     * @return JsonObject
     */

    private synchronized JsonObject checkForTokenInNode(String email, Resource resource, ValueMap valueMap, ResourceResolver resourceResolver, JSONObject jsonObject, SlingHttpServletResponse response, String token, String name) throws IOException, JSONException {
        JsonObject responseJsonObject = new JsonObject();
        JsonObject tokenDetailsJson = new JsonObject();
        if (!valueMap.containsKey(email)) {
            resource.adaptTo(ModifiableValueMap.class).put(email, jsonObject.toString());
            resourceResolver.commit();
            tokenDetailService.storeTokenDetails(token, name, email);
            tokenDetailsJson.addProperty("new_Token",token);
            tokenDetailsJson.addProperty("status","New Token");
            responseJsonObject.add("result", tokenDetailsJson);
        } else {

            String existingTokenJsonString = valueMap.get(email, " ");
            tokenDetailsJson= JsonParser.parseString(existingTokenJsonString).getAsJsonObject();
            tokenDetailsJson.addProperty("status","Already present");
            responseJsonObject.add("result", tokenDetailsJson);
        }
        return responseJsonObject;
    }
}


