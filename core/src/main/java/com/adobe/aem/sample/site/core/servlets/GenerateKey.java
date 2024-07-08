package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.services.TokenDetailService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
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

@Component(service = {Servlet.class}, property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=aemtraining/components/getkey",
        "sling.servlet.paths=/bin/getToken",
        "sling.servlet.extensions=json",
        "sling.servlet.selectors=key"
})
public class GenerateKey extends SlingSafeMethodsServlet {

    @Reference
    private transient TokenDetailService tokenDetailService;

    private Logger log = LoggerFactory.getLogger(ComponentReportServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        String responseObj = "";
        JsonObject responseJson = new JsonObject();
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String path = tokenDetailService.getResourcePath();
        String token = request.getParameter("token");

        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(email)) {
            ResourceResolver resourceResolver = request.getResourceResolver();
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
                    log.error("Json Exception {}",e.getMessage(),e);
                }

            }

        } else if (StringUtils.isNotBlank(token)) {
            JsonObject jsonObjectValidation =new JsonObject();
            Map<String, String> stringValueMap = tokenDetailService.getTokenDetails(token);
            if (stringValueMap != null && !stringValueMap.isEmpty()) {
                String name1 = stringValueMap.get("name");
                String email1 = stringValueMap.get("email");

                jsonObjectValidation.addProperty("name",name1);
                jsonObjectValidation.addProperty("email",email1);
                jsonObjectValidation.addProperty("status","Valid Token");
                responseJson.add("result",jsonObjectValidation);

            } else {

                jsonObjectValidation.addProperty("status","Error");
                jsonObjectValidation.addProperty("message","Invalid Token");
                responseJson.add("result", jsonObjectValidation);
            }
        }

        response.setContentLength(responseJson.toString().getBytes().length);
        response.setContentType("application/json");
        response.getOutputStream().write(responseJson.toString().getBytes());
    }

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
