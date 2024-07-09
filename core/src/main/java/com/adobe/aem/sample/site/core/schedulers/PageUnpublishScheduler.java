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

/**
 * The PageUnpublishScheduler - A scheduler that retrieves a list of page paths from an  API response
 * and unpublishes these pages in AEM.
 *
 * It uses a configured API URL, username, and password to fetch data, and then performs
 * replication to unpublish the  pages.
 */
@Component(immediate = true, service = Runnable.class)
@Designate(ocd = PageUnpublishSchedulerConfiguration.class)
public class PageUnpublishScheduler implements Runnable {


    /**
     * The log - A logger instance used for Logging messages related to PageUnpublishScheduler.
     */
    private Logger log = LoggerFactory.getLogger(PageUnpublishScheduler.class);

    /**
     * The AEM_TRAINING_CONTENT_READER - A string Constant which contains a Service User
     * used to get resource resolver
     */
    private static final String AEM_TRAINING_CONTENT_READER = "aem-training-content-reader";

    /**
     * The resourceResolverFactory - ResourceResolverFactory service used to create object of
     *  ResourceResolver.
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The cryptoSupport -  CryptoSupport service used for decrypting encrypted text.
     */
    @Reference
    private CryptoSupport cryptoSupport;
    /**
     * The replicator - To replicate the content.
     */
    @Reference
    private Replicator replicator;

    /**
     * Configured API URL for fetching data.
     */
    private String apiUrl;

    /**
     * username for authentication with the API.
     */
    private String username;
    /**
     *  password for authentication with the API.
     */
    private String password;

    /**
     * This method get apiUrl from Configuration service and
     * and Decrypt username and password.
     * @param config Configuration object
     */
    @Activate
    protected void activate(PageUnpublishSchedulerConfiguration config) {
        log.debug("Start of activate method in PageUnpublishScheduler");
        apiUrl=config.api_url();
        username = getDecryptedValue(config.username());
        password = getDecryptedValue(config.password());

        log.debug("End of activate method in PageUnpublishScheduler and ApiUrl is {}", apiUrl);
    }

    /**
     * Executes the logic. Fetches API response, extracts page paths, and unpublishes pages.
     */
    @Override
    public void run() {
        log.debug("Start of run method in PageUnpublishScheduler");
        JsonObject jsonResponse = fetchApiResponse();
        if (jsonResponse != null) {
            List<String> listOfPagePath = getPagePathList(jsonResponse);
            unPublishPage(listOfPagePath);

        }
        log.debug("End of run Method PageUnpublishScheduler");
    }
    /**
     * Unpublish the pages based on listOfPagePath.
     *
     * @param listOfPagePath List of page paths to unpublish.
     */
    private void unPublishPage(List<String> listOfPagePath) {
        ResourceResolver resourceResolver = getResourceResolver();
        if (resourceResolver != null) {
            for (String pagePath : listOfPagePath) {
                try {
                    replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.DEACTIVATE, pagePath);
                    log.debug(" Path of pages where expire code is present {}", pagePath);
                } catch (ReplicationException e) {
                    log.error("ReplicationException {}",  e.getMessage() , e);
                }

            }
          resourceResolver.close();
        }

    }
    /**
     * Retrieves a list of page paths based on the JSON response from the API.
     *
     * @param jsonResponse JSON response containing page data.
     * @return List of page paths.
     */
     private List<String> getPagePathList(JsonObject jsonResponse) {
        List<String> listOfPagePath = new ArrayList<>();
        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put("path", "/content/aemtraining");
        predicateMap.put("type", "cq:Page");
        predicateMap.put("property.operation", "equals");
        predicateMap.put("property", "jcr:content/carcodes");
        predicateMap.put("p.limit", "-1");
        if (!jsonResponse.isJsonNull() && !jsonResponse.get("responseCodes").isJsonNull()) {
            JsonArray responseArray = jsonResponse.getAsJsonArray("responseCodes");
            int temp = 1;
            if (responseArray != null) {
                for (int i = 0; i < responseArray.size(); i++) {
                    JsonElement jsonElement = responseArray.get(i);
                    String code = jsonElement.getAsString();
                    predicateMap.put("property." + temp++ + "_value", code);
                    log.debug("Code Present {}", code);
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

    /**
     * Retrieves a ResourceResolver instance using the AEM_TRAINING_CONTENT_READER service user.
     *
     * @return ResourceResolver instance or null if login fails.
     */
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
    /**
     * Decrypts an encrypted text using the CryptoSupport service.
     *
     * @param encryptedText Encrypted text to decrypt.
     * @return Decrypted text or  encrypted text if decryption fails.
     */
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

    /**
     * Fetches the API response from the configured API URL.
     *
     * @return JsonObject .
     */
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



