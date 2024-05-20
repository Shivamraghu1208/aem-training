package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.Beans.Product;
import com.adobe.aem.sample.site.core.services.MoviesService;
import com.adobe.aem.sample.site.core.services.ProductDetailService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailModel {

    @OSGiService
    ProductDetailService productDetailService;
    @ValueMapValue
    String heading;

    private Logger logger = LoggerFactory.getLogger(ProductDetailModel.class);
    

    String response;

    public ArrayList<Product> getList() {
        return list;
    }

    ArrayList<Product> list = new ArrayList<>();

    public String getResponse() {
        return response;
    }


    @ValueMapValue
    String selectHeadingTag;

    @ValueMapValue
    long numberOfProducts;

    public String getHeading() {
        return heading;
    }

    public String getSelectHeadingTag() {
        return selectHeadingTag;
    }


    @PostConstruct
    protected void init() {

        response = productDetailService.getResponse();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        if (!jsonObject.isJsonNull()) {
            if (!jsonObject.get("products").isJsonNull()) {
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
                        Product p = new Product();
                        p.setId(id);
                        p.setTitle(title);
                        p.setBrand(brand);
                        p.setPrice(price);
                        p.setImage(image);

                        list.add(p);

                    }
                }
            }
        }
    }


}