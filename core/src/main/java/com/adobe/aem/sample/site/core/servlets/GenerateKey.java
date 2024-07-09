package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.services.TokenDetailService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String path = tokenDetailService.getResourcePath();
        String token = request.getParameter("token");

        if (name != null && email != null) {
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                String uniqueToken = UUID.randomUUID().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("token", uniqueToken);
                    jsonObject.put("email", email);
                    jsonObject.put("name", name);
                } catch (JSONException e) {
                    log.error("Json Exception {}", e);
                }
                ValueMap valueMap = resource.getValueMap();
                responseObj = checkForTokenInNode(email, resource, valueMap, resourceResolver, jsonObject, uniqueToken, name);
            }

        } else if (token != null) {
            Map<String, String> stringValueMap = tokenDetailService.getTokenDetails(token);
            if (stringValueMap != null && !stringValueMap.isEmpty()) {
                String name1 = stringValueMap.get("name");
                String email1 = stringValueMap.get("email");
                responseObj = "Name:  " + name1 + " Email:  " + email1;
            } else {
                responseObj = "Invalid Token";
            }
        }
        response.getWriter().write(responseObj);
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
     */
    private synchronized String checkForTokenInNode(String email, Resource resource, ValueMap valueMap, ResourceResolver resourceResolver, JSONObject jsonObject, String token, String name) throws IOException {
        if (!valueMap.containsKey(email)) {
            resource.adaptTo(ModifiableValueMap.class).put(email, jsonObject.toString());
            resourceResolver.commit();
            tokenDetailService.storeTokenDetails(token, name, email);
            return "New Token : " + token;
        } else {
            String existingToken = valueMap.get(email, " ");
            return "Already present : " + existingToken.toString();
        }

    }
}
