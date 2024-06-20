package com.adobe.aem.sample.site.core.schedulers;

import com.adobe.aem.sample.site.core.services.config.PageUnpublishSchedulerConfiguration;
import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
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

    private static final String AEM_TRAINING_CONTENT_READER = "aem-training-content-reader";
    private static final String ENCRYPTED_USER_NAME = "{bf369c8b28cee021f9f00e23b2ca9321bc279b60e408df065fc6a820d12f90f8}";
    private static final String ENCRYPTED_USER_PASSWORD = "{bf369c8b28cee021f9f00e23b2ca9321bc279b60e408df065fc6a820d12f90f8}";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    CryptoSupport cryptoSupport;

    @Reference
    protected Replicator replicator;

    private String apiUrl;

    @Activate
    protected void activate(PageUnpublishSchedulerConfiguration config) {
        apiUrl=config.api_url();
        log.debug("Unpublished page Scheduler Activated ");
    }

    @Override
    public void run() {
        log.debug("Run Method Started");
        JsonObject jsonResponse = fetchApiResponse(apiUrl);
        if (jsonResponse != null) {
            List<String> listOfPagePath = getPagePathList(jsonResponse);
            if (listOfPagePath != null) {
                unPublishPage(listOfPagePath);
            }
        }
        log.debug("End of run Method");
    }
    private void unPublishPage(List<String> listOfPagePath) {
        ResourceResolver resourceResolver = getResourceResolver();
        if (resourceResolver != null) {
            for (String pageResource : listOfPagePath) {
                try {
                    replicator.replicate(getSession(resourceResolver), ReplicationActionType.DEACTIVATE, pageResource);
                    log.debug("PATH OF PAGES WHERE EXPIRE CODE IS PRESENT" + pageResource);
                } catch (ReplicationException e) {
                    log.error("ReplicationException {} {}", e, e.getMessage());
                }

            }

        }

    }
     private List<String> getPagePathList(JsonObject jsonResponse) {
        List<String> listOfPagePath = new ArrayList<>();
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", "/content/aemtraining");
        predicateMap.put("type", "cq:PageContent");
        predicateMap.put("property.operation", "equals");
        predicateMap.put("property", "carcodes");
        predicateMap.put("p.limit", "-1");
        if (!jsonResponse.isJsonNull() && !jsonResponse.get("responseCodes").isJsonNull()) {
            JsonArray asJsonArray = jsonResponse.getAsJsonArray("responseCodes");
            int temp = 1;
            if (asJsonArray != null) {
                for (int i = 0; i < asJsonArray.size(); i++) {
                    JsonElement jsonElement = asJsonArray.get(i);
                    String code = jsonElement.getAsString();
                    predicateMap.put("property." + temp++ + "_value", code);
                    log.debug("Code Present" + code);
                }
            }
        }
        PredicateGroup predicates = PredicateGroup.create(predicateMap);
        ResourceResolver resourceResolver = getResourceResolver();
        if (resourceResolver != null) {
            Session session = getSession(resourceResolver);
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
                                    listOfPagePath.add(path);
                                } catch (RepositoryException e) {
                                    log.error("RepositoryException {} {}", e, e.getMessage());
                                }
                            }
                            return listOfPagePath;
                        }
                    }
                }

            }
        }
        return listOfPagePath;
    }

    private Session getSession(ResourceResolver resourceResolver) {
        return resourceResolver.adaptTo(Session.class);
    }

    private ResourceResolver getResourceResolver() {

        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, AEM_TRAINING_CONTENT_READER);
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            return resourceResolver;
        } catch (LoginException e) {
            log.error("Login Exception : {} {}", e, e.getMessage());
        }
        return null;
    }
    public String getDecryptedValue(final String encryptedText) {
        try {
            return cryptoSupport.isProtected(encryptedText)
                    ? cryptoSupport.unprotect(encryptedText)
                    : encryptedText;
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }

    private JsonObject fetchApiResponse(String apiUrl) {
        String username = getDecryptedValue(ENCRYPTED_USER_NAME);
        String password = getDecryptedValue(ENCRYPTED_USER_PASSWORD);

        try {
            URL url = new URL(apiUrl);
            URLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
            InputStream inputStream = connection.getInputStream();
            String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse;
        } catch (MalformedURLException e) {
            log.error("MalformedURLException {} {}", e, e.getMessage());
        } catch (IOException e) {
            log.error("IOException {} {}", e, e.getMessage());
        }
        return null;
    }
}



