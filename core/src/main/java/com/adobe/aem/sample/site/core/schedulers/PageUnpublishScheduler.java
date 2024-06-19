package com.adobe.aem.sample.site.core.schedulers;

import com.adobe.aem.sample.site.core.services.config.PageUnpublishSchedulerConfiguration;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd = PageUnpublishSchedulerConfiguration.class)
public class PageUnpublishScheduler implements Runnable {

    private Logger log = LoggerFactory.getLogger(PageUnpublishScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    protected Replicator replicator;

    @Activate
    protected void activate(PageUnpublishSchedulerConfiguration config) {

        log.info("\n  Demooo  Scheduler Activated");
    }

    @Override
    public void run() {
        String apiUrl = "http://localhost:4502/bin/getCarCodes";
        JsonObject asJsonObject = fetchApiResponse(apiUrl);
        if(asJsonObject!=null)
        {
            List<String> listOfPageResource = buildAndExecuteQuery(asJsonObject);
            if(listOfPageResource!=null) {
                unPublishPage(listOfPageResource);
            }

        }


        log.info("\n ==========================Run From Demo Method running=======================");

    }

    private void unPublishPage(List<String> listOfPageResource) {



                    ResourceResolver resourceResolver = getResourceResolver();
                    if (resourceResolver != null) {
                        for (String pageResource:listOfPageResource) {
                            try {
                                replicator.replicate(getSession(resourceResolver), ReplicationActionType.DEACTIVATE, pageResource);
                                log.info("\n ==========================PATH OF PAGES WHERE EXPIRE CODE IS PRESENT =======================" + pageResource);
                            } catch (ReplicationException e) {
                                e.printStackTrace();
                            }

                        }

                    }


    }

    private List<String> buildAndExecuteQuery(JsonObject asJsonObject) {

        List<String> listOfPageResource=new ArrayList<>();
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", "/content/aemtraining");
        predicateMap.put("type", "cq:PageContent");
        predicateMap.put("property.operation", "equals");
        predicateMap.put("property", "carcodes");
        predicateMap.put("p.limit", "-1");
        if (!asJsonObject.isJsonNull() && !asJsonObject.get("responseCodes").isJsonNull()) {
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("responseCodes");
            int temp = 1;
            if (asJsonArray != null) {
                for (int i = 0; i < asJsonArray.size(); i++) {
                    JsonElement jsonElement = asJsonArray.get(i);
                    String code = jsonElement.getAsString();
                    predicateMap.put("property." + temp++ + "_value", code);
                    log.info("\n ==========================Code Present =======================" + code);
                }
            }
        }
        PredicateGroup predicates = PredicateGroup.create(predicateMap);
        ResourceResolver resourceResolver = getResourceResolver();
        if (resourceResolver != null) {
            Session session=getSession(resourceResolver);
            if (session != null) {
                QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
                if (queryBuilder != null) {
                    Query query = queryBuilder.createQuery(predicates, session);
                    if (query != null) {
                        SearchResult result = query.getResult();
                        List<Hit> hits = result.getHits();
                        if (hits != null) {
                            for (Hit hit : hits) {
                                try {
                                    String path = hit.getPath();
                                    path = path.replace("/jcr:content", "");
                                    listOfPageResource.add(path);
                                } catch (RepositoryException repositoryException) {
                                    repositoryException.printStackTrace();
                                }
                            }
                            return listOfPageResource;
                        }
                    }
                }

                }
            }
        return null;
        }

    private Session getSession(ResourceResolver resourceResolver) {
        return resourceResolver.adaptTo(Session.class);
    }

    private ResourceResolver getResourceResolver () {

            final Map<String, Object> params = new HashMap<>();
            params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
            try {
                ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
                return resourceResolver;
            } catch (LoginException e) {
                log.error("Login Exception : {}",e);
            }
            return null;
        }


        private JsonObject fetchApiResponse (String apiUrl){
            String username = "admin";
            String password = "admin";

            try {
                URL url = new URL(apiUrl);
                URLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
                InputStream inputStream = connection.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                JsonObject asJsonObject = JsonParser.parseString(response).getAsJsonObject();
                return asJsonObject;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



