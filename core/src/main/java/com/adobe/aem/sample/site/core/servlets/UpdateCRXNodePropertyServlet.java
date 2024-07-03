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
/**
 * UpdateCRXNodePropertyServlet is a Sling Servlet that handles GET requests to modify
 * properties of a CRXNode in AEM based on Path and value.
 * It uses ResourceResolverFactory for resolving resources
 * and modifying their properties.
 *
 * Parameters:
 * - value: The value to set for the property named "Shivam".
 * - path: The path of the resource where property need to be modified.
 */
@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/shivam/runmode", "sling.servlet.extensions=json"})
public class UpdateCRXNodePropertyServlet extends SlingSafeMethodsServlet {
    Logger logger = LoggerFactory.getLogger(UpdateCRXNodePropertyServlet.class);


    /**
     * The resourceResolverFactory - Object of resourceResolverFactory.
     * used for resolving resources and modifying their properties.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * This method handles GET request and use resourceResolverFactory to resolve the resource and
     * modifying the properties.
     * @param request - SlingHttpServletRequest object
     * @param response - SlingHttpServletResponse object
     * @throws IOException
     */
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
