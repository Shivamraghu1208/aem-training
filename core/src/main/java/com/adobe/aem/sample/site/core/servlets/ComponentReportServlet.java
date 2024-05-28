package com.adobe.aem.sample.site.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.repoinit.parser.operations.DeleteServiceUser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Servlet.class, property =
        {
                "sling.servlet.methods=GET",
                "sling.servlet.resourceTypes=aemtraining/components/componentreport",
                "sling.servlet.extensions=json"
        }

)
public class ComponentReportServlet extends SlingSafeMethodsServlet {

    private Logger log = LoggerFactory.getLogger("ComponentReportServlet");
    private List<String> list;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        list = new ArrayList<>();
        response.setContentType("application/json");
        String componentResource = request.getParameter("Path");
        if (StringUtils.isNotEmpty(componentResource)) {
            Map<String, String> predicateMap = new HashMap<>();
            predicateMap.put("path", "/content");
            predicateMap.put("type", "nt:unstructured");
            predicateMap.put("property", "sling:resourceType");
            predicateMap.put("property.value", componentResource);
            predicateMap.put("p.limit", "-1");
            PredicateGroup predicates = PredicateGroup.create(predicateMap);
            final Map<String, Object> params = new HashMap<>();
            params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
            try {
                ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
                Session session = resourceResolver.adaptTo(Session.class);
                if (session != null) {
                    QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
                    if (queryBuilder != null) {
                        Query query = queryBuilder.createQuery(predicates, session);
                        if (query != null) {
                            SearchResult result = query.getResult();
                            List<Hit> hits = result.getHits();
                            if (!hits.isEmpty()) {
                                for (Hit hit : hits) {
                                    try {
                                        String path = hit.getPath();
                                        list.add(path);
                                    } catch (RepositoryException e) {
                                        log.error("Exception is Rising"+ e);
                                    }
                                }

                            }

                        }

                    }
                }
            } catch (LoginException e) {
                log.error("Exception is rising " + e);
            }
            response.setStatus(200);
            response.getWriter().write(new ObjectMapper().writeValueAsString(list));
        } else {

            JsonObject jsonObject =  new JsonObject();
            jsonObject.addProperty("error","No Resource Found");
            response.setStatus(404);
            response.getWriter().write(jsonObject.toString());

        }
    }

}
