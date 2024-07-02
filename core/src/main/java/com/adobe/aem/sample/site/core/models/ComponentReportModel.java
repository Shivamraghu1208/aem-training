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

/**
 * The  ComponentReportModel class is a Sling Model used to get all the  components
 * present in the specified AEM path. It retrieves component details and prepares a list of component
 * reports.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ComponentReportModel {

    /**
     * The title - title of Component
     */
    @ValueMapValue
    private String title;

    /**
     * The dropDownPlaceHolder - Name of dropdown
     */
    @ValueMapValue
    private String dropDownPlaceHolder;

    /**
     * The request -  SlingHttpServletRequest object
     */
    @SlingObject
    private SlingHttpServletRequest request;

    /**
     * The buttonLabel - label of button
     */
    @ValueMapValue
    private String buttonLabel;

    /**
     * List containing ComponentReport objects.
     */
    private List<ComponentReport> listOfPojo = new ArrayList<>();

    /**
     * The resource - Resource object
     */
    @SlingObject
    private Resource resource;

    /**
     * This method is automatically called by the Sling after the Sling Model object
     * is created and all dependencies are injected.
     * It performs a query to retrieve all components present in the  path "/apps/aemtraining/components" of type "cq:Component".
     * For each component found, it creates a ComponentReport object with title and path information,
     * and adds it to  listOfPojo..
     * @throws RepositoryException
     */

    @PostConstruct
    protected void init() throws RepositoryException {
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", "/apps/aemtraining/components");
        predicateMap.put("type", "cq:Component");
        predicateMap.put("p.limit", "-1");

        PredicateGroup predicates = PredicateGroup.create(predicateMap);
        ResourceResolver resourceResolver = request.getResourceResolver();
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

                    }
                }
            }
        }

    }

    /**
     * Used to retrieve  list of component reports.
     *
     * @return List of ComponentReport objects.
     */
    public List<ComponentReport> getListOfPojo() {
        return listOfPojo;
    }
    
    /**
     * Used to get a placeholder text for a dropdown.
     * @return The placeholder string.
     */
    public String getDropDownPlaceHolder() {
        return dropDownPlaceHolder;
    }

    /**
     * Used to get label text for a button.
     *
     * @return The button label string.
     */
    public String getButtonLabel() {
        return buttonLabel;
    }


}
