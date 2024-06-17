package com.adobe.aem.sample.site.core.services.impl;

import com.adobe.aem.sample.site.core.services.FindDetailService;
import com.adobe.aem.sample.site.core.services.config.FindDetailServiceConfiguration;
import com.adobe.aem.sample.site.core.services.config.ProductDetailServiceConfiguration;
import com.google.gson.*;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = FindDetailService.class, immediate = true)
@Designate(ocd= FindDetailServiceConfiguration.class)
public class FindDetailServiceImpl implements FindDetailService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;


   String path;
   private Logger logger = LoggerFactory.getLogger(FindDetailServiceImpl.class);
   private Map<String,Map<String,String>> tokenMap=new HashMap<>();
   private Map<String,String> nameEmailMap=new HashMap<>();

    @Activate
    @Modified
    protected void activate(FindDetailServiceConfiguration configuration) {
        path=configuration.path();
        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            Resource resource = resourceResolver.getResource(path);
            if (resource != null) {
                ValueMap valueMap = resource.getValueMap();
                if (valueMap != null) {
                    valueMap.forEach((key, value) -> {
                        if(!key.equals("jcr:primaryType"))
                        {
                            String jsonString = new Gson().toJson(value);
                            if(JsonParser.parseString(JsonParser.parseString(jsonString).getAsString()).isJsonObject()){
                                JsonObject jsonObject = JsonParser.parseString(JsonParser.parseString(jsonString).getAsString()).getAsJsonObject();
                                String name = jsonObject.get("name").getAsString();
                                String email = jsonObject.get("email").getAsString();
                                String token = jsonObject.get("token").getAsString();
                                nameEmailMap.put("name",name);
                                nameEmailMap.put("email",email);
                                tokenMap.put(token,nameEmailMap);

                            }
                        }

                    });
                }
            }
        } catch (LoginException e) {
            logger.error("Login Exception {}",e);
        }
    }
    public void addData(String token, String name,String email)
    {       nameEmailMap.put("name",name);
            nameEmailMap.put("email",email);
            tokenMap.put(token,nameEmailMap);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String,String> getData(String token){
        return tokenMap.get(token);
    }
}
