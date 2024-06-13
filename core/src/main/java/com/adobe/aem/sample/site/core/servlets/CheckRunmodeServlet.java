package com.adobe.aem.sample.site.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/shivam/runmode", "sling.servlet.extensions=json"})
public class CheckRunmodeServlet extends SlingSafeMethodsServlet {
    Logger logger = LoggerFactory.getLogger(CheckRunmodeServlet.class);

    @Reference
    private transient SlingSettingsService service;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("sling.service.subservice", "aem-training-content-writer");
        try {
            ResourceResolver resourceResolver = this.resourceResolverFactory.getServiceResourceResolver(params);
            String value = request.getParameter("value");
            String path = request.getParameter("path");
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                ModifiableValueMap modifiableValueMap = (ModifiableValueMap)resource.adaptTo(ModifiableValueMap.class);
                if (modifiableValueMap != null) {
                    modifiableValueMap.put("Shivam", value);
                    try {
                        resourceResolver.commit();
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (LoginException e) {
            this.logger.error("Exception come : {}", (Throwable)e);
        }
        response.getWriter().write("Servlet Called Successfully");
    }
}
