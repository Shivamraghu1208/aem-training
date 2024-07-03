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
/**
 * This servlet compares two different tags Structure based on there titles and returns matched tags.
 *
 * The servlet fetches tags from "/content/cq:tags" and "/etc/tags" paths, compares their titles,
 * and add the matched tags  in listOfMatched.
 */

@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/match", "sling.servlet.extensions=json"})
public class MatcherServlet extends SlingSafeMethodsServlet {
    /**
     * The listOfMatched - A list contains a Matched tags.
     */
    List<String> listOfMatched = new ArrayList<>();

    /**
     * Handles GET request to get matched tags from two tag structures.
     *
     * @param request  SlingHttpServletRequest object
     * @param response SlingHttpServletResponse object
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ObjectMapper map = new ObjectMapper();
        this.listOfMatched = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource contentResource = resourceResolver.getResource("/content/cq:tags");
        Resource etcResource = resourceResolver.getResource("/etc/tags");
        List<Resource> contentChild = getChild(contentResource);
        List<Resource> etcChild = getChild(etcResource);
        List<String> list = executeProcess(contentChild, etcChild);
        response.getWriter().write(map.writeValueAsString("Matched " + list));
    }
    /**
     * Executes the comparison process between two lists of tag and their child.
     *
     * @param contentChild List of resources from "/content/cq:tags".
     * @param etcChild     List of resources from "/etc/tags".
     * @return List of paths of matched tags.
     */

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
    /**
     * Retrieves children resources of a given resource.
     *
     * @param resource  whose children are to be retrieved.
     * @return List of child resources.
     */
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