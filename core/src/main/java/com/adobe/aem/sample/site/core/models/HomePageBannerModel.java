package com.adobe.aem.sample.site.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomePageBannerModel {
    @ValueMapValue
    @Default(values = "")
    private String buttonLabel;

    @ValueMapValue
    @Default(values = "")
    private String buttonLinkTo;

    @ValueMapValue
    @Default(values = "")
    private String description;

    @ValueMapValue
    @Default(values = "")
    private String title;

    @ValueMapValue
    @Default(values = "")
    private String fileReference;


    @SlingObject
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private Resource resource;


    private boolean isPublishMode;

    @PostConstruct
    protected void init() {

        if(StringUtils.isBlank(title)){
            title = currentPage.getTitle();
        }
        if (!buttonLinkTo.isEmpty() && buttonLinkTo.contains("/content")) {
            if (!buttonLinkTo.contains(".html")) {
                buttonLinkTo = buttonLinkTo + ".html";
            }
        } else {
            buttonLinkTo = "#";
        }
        if (StringUtils.isBlank(fileReference)) {
            Resource image = resource.getChild("image");
            if (image != null) {
                fileReference = image.getPath();
            }
        }

        WCMMode wcmMode = WCMMode.fromRequest((ServletRequest) this.request);
        if (wcmMode != null)
            this.isPublishMode = (wcmMode == WCMMode.DISABLED);

    }

    public String getButtonLabel() {
        return this.buttonLabel;
    }

    public String getButtonLinkTo() {
        return buttonLinkTo;
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