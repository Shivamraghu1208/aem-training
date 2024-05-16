package com.adobe.aem.sample.site.core.servlets;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.method=GET",
                "sling.servlet.paths=/bin/getChildPage",
                "sling.servlet.extensions=json"
        }

)

public class PathServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ArrayList<String> list = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        String path = request.getParameter("path");
        if (path != null) {
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                Iterator<Resource> resourceIterator = resource.listChildren();
                while (resourceIterator.hasNext()) {
                    Resource next = resourceIterator.next();
                    if (next != null && !next.getName().equals("jcr:content")) {
                        String path1 = next.getPath();
                        list.add(path1);
                    }
                }
                Gson gson = new Gson();
                // convert your list to json
                String jsonResponse = gson.toJson(list);
                response.getWriter().write(jsonResponse);
            }
        }
    }
}
