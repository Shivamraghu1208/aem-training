package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.services.ProductDetailService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ProductDetailModelTest {

    private final AemContext context = new AemContext();

    private ProductDetailModel productDetailModel;
    @Mock
    private ProductDetailService productDetailService;


    @Test
    void testProductDetailModel() throws IOException {
        context.registerService(ProductDetailService.class, productDetailService);
        String response = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/com/adobe/aem/sample/site/core/models/ProductDetailModel/response.json")), StandardCharsets.UTF_8).toString();
        when(productDetailService.getResponse()).thenReturn(response);
        context.load().json("/com/adobe/aem/sample/site/core/models/ProductDetailModel/resource.json", "/content/aem-training/us/en/test");
        context.currentResource("/content/aem-training/us/en/test");
        productDetailModel = context.currentResource().adaptTo(ProductDetailModel.class);
        assertNotNull(this.productDetailModel);
        assertEquals("shivam", this.productDetailModel.getHeading());
        assertEquals("h2", productDetailModel.getSelectHeadingTag());
        assertEquals(4, productDetailModel.getList().size());
        assertEquals(false, productDetailModel.getResponse().isEmpty());

    }

    @Test
    void testWithNullValue() throws IOException {
        context.registerService(ProductDetailService.class, productDetailService);
        when(productDetailService.getResponse()).thenReturn(null);
        context.load().json("/com/adobe/aem/sample/site/core/models/ProductDetailModel/resource1.json", "/content/aem-training/us/en/test");
        context.currentResource("/content/aem-training/us/en/test");
        productDetailModel = context.currentResource().adaptTo(ProductDetailModel.class);
        assertNotNull(this.productDetailModel);
        assertEquals(null, productDetailModel.getSelectHeadingTag());
        assertEquals(null, productDetailModel.getHeading());
        assertEquals(0, productDetailModel.getList().size());
        assertEquals(true, productDetailModel.getList().isEmpty());
        assertEquals(null, productDetailModel.getResponse());
    }
}
