package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.Beans.Product;
import com.adobe.aem.sample.site.core.services.MoviesService;
import com.adobe.aem.sample.site.core.services.ProductDetailService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

/**
 * A ProductDetailModel is a sling model class which is used to retrieve a response from service, parses the json response and
 * prepare the list of  product .
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailModel {

    /**
     * The productDetailService - productDetailService object
     */
    @OSGiService
    private ProductDetailService productDetailService;

    /**
     * The heading of the component.
     */
    @ValueMapValue
    private String heading;

    /**
     * The logger - Logger object
     * Used for logging messages related to ProductDetailModel.
     */
    private Logger logger = LoggerFactory.getLogger(ProductDetailModel.class);

    /**
     * The response message.
     */
    private String response;

    /**
     *The list - A list of  Product objects.
     * This list is used to store the products.
     */
    private ArrayList<Product> list = new ArrayList<>();

    /**
     * The selected heading tag for the component.
     */
    @ValueMapValue
    private String selectHeadingTag;

    /**
     * The number of products to display.
     */
    @ValueMapValue
    private long numberOfProducts;


    /**
     * used to get response.
     * @return a string response.
     */
    public String getResponse() {
        return response;
    }

    /**
     * used to retrieve a product list.
     * @return a list of products.
     */
    public ArrayList<Product> getList() {
        return list;
    }

    /**
     * used to retrieve heading for the component.
     * @return  a string contains heading.
     */
    public String getHeading() {
        return heading;
    }
    /**
     * used to retrieve heading Tag for the component.
     * @return a string contains HeadingTag.
     */
    public String getSelectHeadingTag() {
        return selectHeadingTag;
    }


    /**
     *  This method is automatically called by the Sling framework after the Sling Model object
     *   is created and all dependencies are injected.
     *  This method retrieves the response from the product detail service,
     *   parses the JSON response, and add to the list of products.
     */
    @PostConstruct
    protected void init() {
        response = productDetailService.getResponse();
        if (StringUtils.isNotBlank(response)) {
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            if (!jsonObject.isJsonNull() && !jsonObject.get("products").isJsonNull()) {
                JsonArray jsonArray = jsonObject.get("products").getAsJsonArray();
                if (jsonArray != null) {
                    for (int i = 0; i < numberOfProducts; i++) {
                        JsonElement jsonElement = jsonArray.get(i);
                        JsonObject asJsonObject = jsonElement.getAsJsonObject();
                        int id = asJsonObject.get("id").getAsInt();
                        String title = asJsonObject.get("title").getAsString();
                        String brand = asJsonObject.get("brand").getAsString();
                        long price = asJsonObject.get("price").getAsLong();
                        JsonArray images = asJsonObject.get("images").getAsJsonArray();
                        String image = images.get(0).getAsString();
                        Product product = new Product();
                        product.setId(id);
                        product.setTitle(title);
                        product.setBrand(brand);
                        product.setPrice(price);
                        product.setImage(image);
                        list.add(product);

                    }
                }
            }
        }
    }
}


