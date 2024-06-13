package com.adobe.aem.sample.site.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/match", "sling.servlet.extensions=json"})
public class MatcherServlet extends SlingSafeMethodsServlet {
    List<String> listOfMatched = new ArrayList<>();

    List<String> listOfUnMatched = new ArrayList<>();

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ObjectMapper map = new ObjectMapper();
        this.listOfMatched = new ArrayList<>();
        this.listOfUnMatched = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource contentResource = resourceResolver.getResource("/content/cq:tags");
        Resource etcResource = resourceResolver.getResource("/etc/tags");
        List<Resource> contentChild = getChild(contentResource);
        List<Resource> etcChild = getChild(etcResource);
        List<String> list = executeProcess(contentChild, etcChild);
        response.getWriter().write(map.writeValueAsString("Matched " + list));
    }

    List<String> executeProcess(List<Resource> contentChild, List<Resource> etcChild) {
        if (contentChild != null && etcChild != null)
            for (Resource resource : contentChild) {
                for (Resource value : etcChild) {
                    if (((String)resource.getValueMap().get("jcr:title", "")).equals(value.getValueMap().get("jcr:title", ""))) {
                        this.listOfMatched.add(resource.getPath() + "==" + value.getPath());
                        if (resource.hasChildren() && value.hasChildren()) {
                            List<Resource> contentChildResource = getChild(resource);
                            List<Resource> etcChildResource = getChild(value);
                            executeProcess(contentChildResource, etcChildResource);
                        }
                    }
                }
            }
        return this.listOfMatched;
    }

    List<Resource> getChild(Resource resource) {
        if (resource == null)
            return new ArrayList<>();
        List<Resource> listOfChildren = new ArrayList<>();
        Iterator<Resource> resourceIterator = resource.listChildren();
        while (resourceIterator.hasNext()) {
            Resource next = resourceIterator.next();
            listOfChildren.add(next);
        }
        return listOfChildren;
    }
}