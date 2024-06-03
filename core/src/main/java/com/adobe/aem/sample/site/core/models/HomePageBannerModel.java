package com.adobe.aem.sample.site.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomePageBannerModel {
    @ValueMapValue
    private String buttonLabel;

    @ValueMapValue
    private String buttonLinkTo;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String fileReference;

    @SlingObject
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;

    private String path;

    private boolean isPublishMode;

    @PostConstruct
    protected void init() {
        if (this.title == null) {
            this.path = this.currentPage.getPath();
            this.path = this.path.concat("/jcr:content");
            if (this.resource != null) {
                ResourceResolver resourceResolver = this.resource.getResourceResolver();
                Resource pageResource = resourceResolver.getResource(this.path);
                if (pageResource != null) {
                    ValueMap valueMap = pageResource.getValueMap();
                    this.title = (String)valueMap.get("jcr:title", "");
                }
            }
        }
        WCMMode wcmMode = WCMMode.fromRequest((ServletRequest)this.request);
        if (wcmMode != null)
            this.isPublishMode = (wcmMode == WCMMode.DISABLED);
    }

    public String getButtonLabel() {
        return this.buttonLabel;
    }

    public String getButtonLinkTo() {
        return this.buttonLinkTo;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getFileReference() {
        return this.fileReference;
    }

    public boolean isPublishMode() {
        return this.isPublishMode;
    }
}