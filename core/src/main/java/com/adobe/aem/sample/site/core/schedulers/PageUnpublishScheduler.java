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

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private CryptoSupport cryptoSupport;

    @Reference
    private Replicator replicator;

    private String apiUrl;

    private String username;

    private String password;
    @Activate
    protected void activate(PageUnpublishSchedulerConfiguration config) {
        apiUrl=config.api_url();
        username = getDecryptedValue(config.username());
        password = getDecryptedValue(config.password());
        log.debug("Start of activate method in PageUnpublishScheduler with apiUrl : {}",apiUrl);
    }

    @Override
    public void run() {
        log.debug("Start of run method in PageUnpublishScheduler");
        JsonObject jsonResponse = fetchApiResponse();
        if (jsonResponse != null) {
            List<String> listOfPagePath = getPagePathList(jsonResponse);
            if (listOfPagePath != null) {
                unPublishPage(listOfPagePath);
            }
        }
        log.debug("End of run Method PageUnpublishScheduler");
    }
    private void unPublishPage(List<String> listOfPagePath) {
        ResourceResolver resourceResolver = getResourceResolver();
        if (resourceResolver != null) {
            for (String pageResource : listOfPagePath) {
                try {
                    replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.DEACTIVATE, pageResource);
                    log.debug(" Path of pages where expire code is present {}", pageResource);
                } catch (ReplicationException e) {
                    log.error("ReplicationException {}",  e.getMessage() , e);
                }

            }
          resourceResolver.close();
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
            Session session = resourceResolver.adaptTo(Session.class);
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
                                    log.error("RepositoryException {}", e.getMessage() ,e);
                                }
                            }
                            return listOfPagePath;
                        }
                    }
                }

            }
            resourceResolver.close();
        }
        return listOfPagePath;
    }

    private ResourceResolver getResourceResolver() {

        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, AEM_TRAINING_CONTENT_READER);
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            return resourceResolver;
        } catch (LoginException e) {
            log.error("Login Exception : {}" , e.getMessage() , e);
        }
        return null;
    }
    public String getDecryptedValue(final String encryptedText) {
        try {
            return cryptoSupport.isProtected(encryptedText)
                    ? cryptoSupport.unprotect(encryptedText)
                    : encryptedText;
        } catch (CryptoException e) {
            log.error("CryptoException {} ", e.getMessage() , e);
        }
        return encryptedText;
    }

    private JsonObject fetchApiResponse() {
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
            log.error("MalformedURLException {} ", e.getMessage(), e);
        } catch (IOException e) {
            log.error("IOException {} ", e.getMessage(), e);
        }
        return null;
    }
}



