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

/**
 * Model class representing a home page banner component.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomePageBannerModel {
    /**
     * The label for the button.
     */
    @ValueMapValue
    @Default(values = "")
    private String buttonLabel;

    /**
     * The link for the button.
     */
    @ValueMapValue
    @Default(values = "")
    private String buttonLinkTo;

    /**
     * The description text for the banner.
     */
    @ValueMapValue
    @Default(values = "")
    private String description;

    /**
     * The title of the banner.
     */
    @ValueMapValue
    @Default(values = "")
    private String title;

    /**
     * The reference to the banner image file.
     */
    @ValueMapValue
    @Default(values = "")
    private String fileReference;


    /**
     * The request - SlingHttpServletRequest object.
     */
    @SlingObject
    private SlingHttpServletRequest request;

    /**
     * The currentPage - Page object.
     */
    @ScriptVariable
    private Page currentPage;

    /**
     * The resource - Resource object.
     */
    @SlingObject
    private Resource resource;

    /**
     * Indicates if the current mode is publish mode.
     */
    private boolean isPublishMode;

    /**
     *This method is automatically called by the Sling framework after the Sling Model object
     * is created and all dependencies are injected.
     * Sets the title and file reference if they are not specified,
     * and adjusts the button link format and check the current mode.
     *
     */
    @PostConstruct
    protected void init() {
        WCMMode wcmMode = WCMMode.fromRequest((ServletRequest) this.request);
        if (wcmMode != null)
            this.isPublishMode = (wcmMode == WCMMode.DISABLED);
        if (isPublishMode) {
            if (StringUtils.isBlank(title)) {
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
        }
    }
    /**
     * Gets the button label.
     *
     * @return the button label.
     */
    public String getButtonLabel() {
        return this.buttonLabel;
    }
    /**
     * Gets the button link.
     *
     * @return the button link.
     */
    public String getButtonLinkTo() {
        return buttonLinkTo;
    }
    /**
     * Gets the description.
     *
     * @return the description.
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Gets the title.
     *
     * @return the title.
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Gets the file reference.
     *
     * @return the file reference.
     */
    public String getFileReference() {
        return this.fileReference;
    }
    /**
     * Checks if the current mode is publish mode.
     *
     * @return true if in publish mode, false otherwise.
     */

    public boolean isPublishMode() {
        return this.isPublishMode;
    }
}