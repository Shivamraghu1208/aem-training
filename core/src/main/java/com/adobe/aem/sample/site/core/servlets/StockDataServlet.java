package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.Pojo.StockData;
import com.adobe.aem.sample.site.core.Pojo.StockDataWrapper;
import com.adobe.aem.sample.site.core.services.StockDataService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.adobe.aem.sample.site.core.servlets.StockDataServlet.SLING_SERVLET_EXTENSIONS;
import static com.adobe.aem.sample.site.core.servlets.StockDataServlet.SLING_SERVLET_METHODS;
import static com.adobe.aem.sample.site.core.servlets.StockDataServlet.SLING_SERVLET_PATHS;

/**
 * The StockDataServlet is a Sling Servlet that handles a post request to create the node and store the stock data based on the Dates.
 */
@Component(service = {Servlet.class}, immediate = true,
        property = {SLING_SERVLET_METHODS,
                SLING_SERVLET_PATHS,
                SLING_SERVLET_EXTENSIONS}
)
public class StockDataServlet extends SlingAllMethodsServlet {

    public static final String SLING_SERVLET_METHODS = "sling.servlet.methods=POST";
    public static final String SLING_SERVLET_PATHS = "sling.servlet.paths=/bin/stocks/data";
    public static final String SLING_SERVLET_EXTENSIONS = "sling.servlet.extensions=json";
    /**
     * This is used for generating random strings.
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * The key used to identify stock data in request.
     * It represents the data related to stocks.
     */
    private static final String STOCK_DATA = "stock-data";

    /**
     * It represents the date related to the data .
     */
    private static final String DATE = "date";

    /**
     * It represents the unique symbol assigned to a stock.
     */
    private static final String SYMBOL = "symbol";

    /**
     * The user service.
     */
    private static final String SUB_SERVICE_USER = "aem-training-content-writer";

    /**
     * The company name.
     */
    private static final String COMPANY = "company";

    /**
     * A brief description of the stock.
     */
    private static final String DESCRIPTION = "description";

    /**
     * The opening price of the stock.
     */
    private static final String OPENING_PRICE = "opening_price";

    /**
     * The closing price of the stock.
     */
    private static final String CLOSING_PRICE = "closing_price";

    /**
     * The string constant representing a hyphen.
     */
    public static final String HYPHEN = "-";

    /**
     * The string constant representing a slash.
     */
    public static final String SLASH = "/";

    /**
     * The GetPathService - Object of Service Used to get a Path .
     */
    @Reference
    private StockDataService stockDataService;

    /**
     * The resourceResolverFactory - Object of ResourceResolverFactory
     */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The log - Logger object
     * Used for logging messages related to StockDataServlet.
     */
    private final Logger log = LoggerFactory.getLogger(StockDataServlet.class);

    /**
     * A Method handle a Post request and use to create the Nodes Based on the Dates on given Path and
     * Used to save the Stock Data.
     *
     * @param request  SlingHttpServletRequest
     * @param response SlingHttpServletResponse
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        log.debug("Start of doPost method of StockDataServlet Class");
        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE_USER);
        JsonObject responseJsonObject = new JsonObject();
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params)) {
            responseJsonObject = fetchInputData(request, response, resourceResolver);
            log.debug("After calling fetchInputData");
            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage(), e);
            responseJsonObject.addProperty("error", "JSON Syntax Error");
            responseJsonObject.addProperty("message", "Invalid Json Body");
            response.setStatus(404);
            response.getWriter().write(responseJsonObject.toString());
        }
        log.debug("End of doPost method");
    }

    /**
     * This method used to create a node  in a hierarchical way under the given path.
     * Used to store a data in proper node structure.
     *
     * @param resourceResolver The ResourceResolver Object
     * @param stockData        The StockData object
     * @throws PersistenceException It throws exception at the time we Create a node or commit.
     */
    protected void createNodes(ResourceResolver resourceResolver, StockData stockData) throws PersistenceException {
        log.debug("Start of createNodes method of StockDataServlet");
        String path = stockDataService.getPath();
        if (resourceResolver != null) {
            log.debug("After getting ResourceResolver object");
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                Map<String, Object> map = new HashMap<>();
                String date = stockData.getDate();
                String[] split = date.split(HYPHEN);
                for (int arrayIndex = split.length - 1; arrayIndex >= 0; arrayIndex--) {
                    Resource nextResource = resourceResolver.getResource(resource.getPath() + SLASH + split[arrayIndex]);
                    if (nextResource == null) {
                        nextResource = resourceResolver.create(resource, split[arrayIndex], map);
                        resourceResolver.commit();
                    }
                    resource = nextResource;
                    if (arrayIndex == 0) {
                        map.put(COMPANY, stockData.getCompany());
                        map.put(DESCRIPTION, stockData.getDescription());
                        map.put(OPENING_PRICE, stockData.getOpening_price());
                        map.put(CLOSING_PRICE, stockData.getClosing_price());
                        map.put(SYMBOL, stockData.getSymbol());
                        String randomString = generateRandomString(6);
                        if (resourceResolver.getResource(resource.getPath() + SLASH + randomString) != null) {
                            randomString = generateRandomString(6);
                        }
                        resourceResolver.create(resource, randomString, map);
                        resourceResolver.commit();
                        log.debug("after created nodes ");
                    }
                }
            }
        }
        log.debug("end of createNodes method of StockDataServlet");
    }

    /**
     * The method used to fetch the input data and store in stock Data List .
     * The method reads the input stream as a JSON string, parses it, and converts it into a list of  StockData
     * based on the provided JSON structure.
     *
     * @param request The SlingHttpServletRequest Object
     * @return The List of Stock Data
     */
    protected JsonObject fetchInputData(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver) {
        log.debug("Start of fetchInputData method of StockDataServlet");
        JsonObject responseJsonObject = new JsonObject();
        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int character;
            while ((character = inputStream.read()) != -1) {
                stringBuilder.append((char) character);
            }
            String jsonString = stringBuilder.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try {
                JsonElement jsonElement = JsonParser.parseString(stringBuilder.toString());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray stockDataArray = jsonObject.getAsJsonArray(STOCK_DATA);
                if (stockDataArray != null && !stockDataArray.isEmpty()) {
                    for (int index = 0; index < stockDataArray.size(); index++) {
                        JsonElement stockDataElement = stockDataArray.get(index);
                        if (stockDataElement.isJsonObject()) {
                            JsonObject stockDataObject = stockDataElement.getAsJsonObject();
                            StockData stockData = new StockData();
                            stockData.setDate(getAsString(stockDataObject, DATE));
                            stockData.setCompany(getAsString(stockDataObject, COMPANY));
                            stockData.setClosing_price(getAsDouble(stockDataObject, CLOSING_PRICE));
                            stockData.setDescription(getAsString(stockDataObject, DESCRIPTION));
                            stockData.setOpening_price(getAsDouble(stockDataObject, OPENING_PRICE));
                            stockData.setSymbol(getAsString(stockDataObject, SYMBOL));
                            log.debug("Before calling createNodes method of StockDataServlet");
                            createNodes(resourceResolver, stockData);
                            log.debug("After calling createNodes method of StockDataServlet");
                        }
                    }
                } else {
                    responseJsonObject.addProperty("error", "Invalid JSON format");
                    responseJsonObject.addProperty("message", "The Stock Data is not properly provided OR stock-data is not present in Json Body");
                    response.setStatus(404);
                    return responseJsonObject;
                }
            } catch (JsonSyntaxException e) {
                log.error("Json Syntax Exception {}", e.getMessage(), e);
                responseJsonObject.addProperty("error", "JSON Syntax Error");
                responseJsonObject.addProperty("message", "Invalid JSON Body ");
                response.setStatus(404);
                return responseJsonObject;
            }
        } catch (IOException e) {
            log.error("IO Exception {}", e.getMessage(), e);
        }

        log.debug("end of fetchInputData method of StockDataServlet");
        responseJsonObject.addProperty("Success", "Node Created Successfully");
        response.setStatus(200);
        return responseJsonObject;
    }

    /**
     * This method is used to retrieve a json element from a object and check that a object contains key or not.
     * If key is present then it returns a String value or return a null.
     *
     * @param object A JsonObject
     * @param key    A String Key
     * @return String value or null.
     */
    private String getAsString(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && object.has(key) ? element.getAsString() : "";
    }

    /**
     * This method is used to retrieve a json element from a object and check that a object contains key or not.
     * If key is present then it returns a Double value or return a null.
     *
     * @param object JsonObject
     * @param key    JsonObject
     * @return Double value or null.
     */
    private Double getAsDouble(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element != null && object.has(key) ? element.getAsDouble() : 0;
    }

    /**
     * This method is used to generate a random String.
     *
     * @param length of a String.
     * @return String
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
