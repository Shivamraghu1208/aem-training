package com.adobe.aem.sample.site.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.configuration.injection.MockInjection;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HomePageBannerModelTest {

    private final AemContext context = new AemContext();

    @Mock
    private SlingHttpServletRequest request;

    private HomePageBannerModel homePageBannerModel;
    @BeforeEach
    void setUp() {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/HomePageBannerModel/resource.json", "/content/aem-training/us/en/test");
        context.currentResource("/content/aem-training/us/en/test");
        homePageBannerModel = context.request().adaptTo(HomePageBannerModel.class);
        assertNotNull(homePageBannerModel);
    }

    @Test
    void getButtonLabel() {
        assertEquals("click me",homePageBannerModel.getButtonLabel());
    }

    @Test
    void getButtonLinkTo() {
        assertEquals("/content/we-retail/it.html",homePageBannerModel.getButtonLinkTo());
    }

    @Test
    void getDescription() {
        assertEquals("<p>Heyy Hows You</p>\r\n", homePageBannerModel.getDescription());
    }

    @Test
    void getTitle() {
        assertEquals("Home Page", homePageBannerModel.getTitle());
    }

    @Test
    void getFileReference() {
        assertEquals("/content/aem-training/us/en/test/image",homePageBannerModel.getFileReference());
    }

    @Test
    void isPublishMode() {
        assertTrue(homePageBannerModel.isPublishMode());

    }
}