package com.adobe.aem.sample.site.core.services.impl;

import com.adobe.aem.sample.site.core.services.FindDetailService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = FindDetailService.class, immediate = true)
public class FindDetailServiceImpl implements FindDetailService {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

   private Logger logger = LoggerFactory.getLogger(FindDetailServiceImpl.class);
   private Map<String,Map<String,String>> mapOfToken=new HashMap<>();
   private Map<String,String> mapOfNameAndEmail=new HashMap<>();

    @Activate
    @Modified
    protected void activate() {
        final Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "aem-training-content-reader");
        try {
            ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            Resource resource = resourceResolver.getResource("/etc/myvalues");
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
                                mapOfNameAndEmail.put("name",name);
                                mapOfNameAndEmail.put("email",email);
                                mapOfToken.put(token,mapOfNameAndEmail);

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
    {       mapOfNameAndEmail.put("name",name);
            mapOfNameAndEmail.put("email",email);
            mapOfToken.put(token,mapOfNameAndEmail);
    }

    @Override
    public Map<String,String> getMapOfToken(String token){
        return mapOfToken.get(token);
    }
}
