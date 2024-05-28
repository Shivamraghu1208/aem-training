package com.adobe.aem.sample.site.core.models;


import com.adobe.aem.sample.site.core.Beans.ComponentReport;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ComponentReportModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String dropDownPlaceHolder;

    @ValueMapValue
    private String buttonLabel;


    private List<ComponentReport> listOfPojo = new ArrayList<>();

    private List<String> listOfTitle = new ArrayList<>();

    @SlingObject
    private Resource resource;


    @PostConstruct
    protected void init() throws RepositoryException {
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", "/apps/aemtraining/components");
        predicateMap.put("type", "cq:Component");
        predicateMap.put("p.limit", "-1");

        PredicateGroup predicates = PredicateGroup.create(predicateMap);
        ResourceResolver resourceResolver = resource.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        if (session != null) {
            QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            if (queryBuilder != null) {
                Query query = queryBuilder.createQuery(predicates, session);
                SearchResult result = query.getResult();
                List<Hit> hits = result.getHits();
                if (!hits.isEmpty()) {
                    for (Hit hit : hits) {
                        Resource resource = hit.getResource();
                        ValueMap valueMap = resource.getValueMap();
                        String title = valueMap.get("jcr:title", "");
                        String path = resource.getPath();
                        path = path.replace("/apps/", "");
                        ComponentReport componentReportPojo = new ComponentReport();
                        componentReportPojo.setTitle(title);
                        componentReportPojo.setPath(path);
                        listOfPojo.add(componentReportPojo);
                        listOfTitle.add(path);
                    }
                }
            }
        }

    }

    public List<ComponentReport> getListOfPojo() {
        return listOfPojo;
    }

    public List<String> getListOfTitle() {
        return listOfTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getDropDownPlaceHolder() {
        return dropDownPlaceHolder;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }


}
