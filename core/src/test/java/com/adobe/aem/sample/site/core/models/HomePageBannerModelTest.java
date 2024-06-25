package com.adobe.aem.sample.site.core.models;

import com.day.cq.wcm.api.Page;
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
    @Mock
    private Page currentPage;

    private HomePageBannerModel homePageBannerModel;
    @Test
    void testHomePageBanner() {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/HomePageBannerModel/resource.json", "/content/aem-training/us/en/test");
        context.currentResource("/content/aem-training/us/en/test");
        homePageBannerModel = context.request().adaptTo(HomePageBannerModel.class);
        assertNotNull(homePageBannerModel);
        assertEquals("click me",homePageBannerModel.getButtonLabel());
        assertEquals("/content/we-retail/it.html",homePageBannerModel.getButtonLinkTo());
        assertEquals("<p>Heyy Hows You</p>\r\n", homePageBannerModel.getDescription());
        assertEquals("Home Page", homePageBannerModel.getTitle());
        assertEquals("/content/aem-training/us/en/test/image",homePageBannerModel.getFileReference());
        assertTrue(homePageBannerModel.isPublishMode()); }
    @Test
    void testWithNull()
    { context.load(true).json("/com/adobe/aem/sample/site/core/models/HomePageBannerModel/resource1.json", "/content/aem-training/us/en/test/homePageBanner");
        context.currentResource("/content/aem-training/us/en/test/homePageBanner");
        Page page = context.create().page("/content/aem-training/us/en/test");
        context.currentPage(page);
        homePageBannerModel = context.request().adaptTo(HomePageBannerModel.class);
        assertNotNull(homePageBannerModel);
        assertEquals("",homePageBannerModel.getDescription());
        assertEquals("",homePageBannerModel.getButtonLabel());
        assertEquals("",homePageBannerModel.getFileReference());
        assertEquals("test",homePageBannerModel.getTitle());
        assertEquals("#",homePageBannerModel.getButtonLinkTo());
    }
}