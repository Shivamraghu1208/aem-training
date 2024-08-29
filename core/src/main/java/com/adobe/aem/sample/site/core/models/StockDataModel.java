package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.Pojo.CompanyDataResponse;
import com.adobe.aem.sample.site.core.services.StockDataService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * The  StockDataModel class is a Sling Model used to retrieve the stock data based on given Date.
 * It retrieves Stock details.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StockDataModel {

    /**
     * The user service.
     */
    private static final String SUBSERVICE_USER = "aem-training-content-writer";

    /**
     * The company name.
     */
    private static final String COMPANY = "company";

    /**
     * The opening price of the Stock.
     */
    private static final String OPENING_PRICE = "opening_price";

    /**
     * The closing price of the Stock.
     */
    private static final String CLOSING_PRICE = "closing_price";
    /**
     * The string constant representing the default value.
     */
    public static final String DEFAULT = "default";
    /**
     * The string constant representing a hyphen.
     */
    public static final String HYPHEN = "-";

    /**
     * The string constant representing the letter 'T'.
     */
    public static final String T = "T";
    /**
     * The string constant representing a slash.
     */
    public static final String SLASH = "/";


    /**
     * A date - Based on date in retrieve the stock data
     */
    @ValueMapValue
    private String date;

    /**
     * The resourceResolverFactory - ResourceResolverFactory service used to create instances of
     * ResourceResolver.
     */
    @OSGiService
    private ResourceResolverFactory resourceResolverFactory;

    /**
     * The getPathService - Object of GetPathService Used to get a Path .
     */
    @OSGiService
    private StockDataService stockDataService;

    /**
     * The log - Logger object for logging messages specific to the
     * StockDataModel class.
     */
    private final Logger log = LoggerFactory.getLogger(StockDataModel.class);


    /**
     * Used to retrieve a date.
     *
     * @return date
     */
    public String getDate() {
        return date;
    }


    /**
     * A listOfCompanyDataResponse - list of Company Data.
     * Used to store the Object of CompanyDataResponse.
     */
    private List<CompanyDataResponse> listOfCompanyDataResponse;

    /**
     * used to get a list of  CompanyDataResponse.
     *
     * @return listOfCompanyDataResponse.
     */
    public List<CompanyDataResponse> getListOfCompanyDataResponse() {
        return listOfCompanyDataResponse;
    }


    /**
     * This method automatically called by sling Framework.
     * This method is used to retrieve the data from a Node based on the given date
     * and add to the list of CompanyDataResponse.
     */
    @PostConstruct
    protected void init() {
        log.debug("Start of init method");
        listOfCompanyDataResponse = new ArrayList<>();
        if (StringUtils.isNotBlank(date)) {
            String[] dateParts = date.split(T);
            date = dateParts[0];

            String[] dateSplit = date.split(HYPHEN);
            String path = stockDataService.getPath();
            if (StringUtils.isNotBlank(path)) {
                for (String nodeName : dateSplit) {
                    path = path + SLASH + nodeName;
                }
                final Map<String, Object> params = new HashMap<>();
                params.put(ResourceResolverFactory.SUBSERVICE, SUBSERVICE_USER);
                ResourceResolver resourceResolver = null;
                try {
                    resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
                    if (resourceResolver != null) {
                        log.debug("after getting ResourceResolver Object");
                        Resource resource = resourceResolver.getResource(path);
                        if (resource != null && resource.hasChildren()) {
                            Iterator<Resource> resourceIterator = resource.listChildren();
                            while (resourceIterator.hasNext()) {
                                ValueMap valueMap = resourceIterator.next().getValueMap();
                                CompanyDataResponse companyDataResponse = new CompanyDataResponse();
                                companyDataResponse.setCompanyName(valueMap.get(COMPANY, DEFAULT));
                                companyDataResponse.setOpeningPrice(valueMap.get(OPENING_PRICE, 0.0));
                                companyDataResponse.setClosingPrice(valueMap.get(CLOSING_PRICE, 0.0));
                                listOfCompanyDataResponse.add(companyDataResponse);
                            }
                        }
                    }
                } catch (LoginException e) {
                    log.error("Login Exception : {}", e.getMessage(), e);
                } finally {
                    if (resourceResolver != null) {
                        resourceResolver.close();
                    }
                }

            }
        }
        log.debug("End of init method");
    }
}
