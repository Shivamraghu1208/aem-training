package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.services.FindDetailService;
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
    private transient FindDetailService findDetailService;

    private Logger log = LoggerFactory.getLogger(ComponentReportServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String path = findDetailService.getPath();
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
                boolean isPresent = checkForTokenInNode(email, resource, valueMap, resourceResolver, jsonObject,response,uniqueToken,name);
                if(isPresent) {
                    String tokenPresent = valueMap.get(email, "");
                    response.getWriter().write("already Token present: " + tokenPresent); }
            }
        } else if (token != null) {
            Map<String, String> stringValueMap = findDetailService.getData(token);
            if (stringValueMap != null && !stringValueMap.isEmpty()) {
                String name1 = stringValueMap.get("name");
                String email1 = stringValueMap.get("email");
                response.getWriter().write("Name:  " + name1 + " Email:  " + email1);
            }
            else {
                response.getWriter().write("Invalid Token");
            }
        }
    }

    private synchronized boolean checkForTokenInNode(String email, Resource resource, ValueMap valueMap, ResourceResolver resourceResolver, JSONObject jsonObject, SlingHttpServletResponse response,String token,String name) throws IOException {
           boolean isPresent=true;
        if (!valueMap.containsKey(email)) {
            resource.adaptTo(ModifiableValueMap.class).put(email, jsonObject.toString());
            resourceResolver.commit();
            findDetailService.addData(token,name,email);
            response.getWriter().write("New Token: " + token);
            isPresent=false;
        }
        return isPresent;
    }
}
