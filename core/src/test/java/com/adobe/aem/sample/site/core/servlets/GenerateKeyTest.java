package com.adobe.aem.sample.site.core.servlets;

import com.adobe.aem.sample.site.core.services.TokenDetailService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class GenerateKeyTest {

    private final AemContext context = new AemContext();

    private GenerateKey generateKey;

    @Mock
    private TokenDetailService tokenDetailService;

    @Mock
    private MockSlingHttpServletRequest request;

    @Mock
    private MockSlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private ServletOutputStream servletOutputStream;

    @Captor
    private ArgumentCaptor<byte[]> captor;

    @BeforeEach
    void setUp() {
        context.registerService(TokenDetailService.class, tokenDetailService);
        generateKey = context.registerInjectActivateService(GenerateKey.class);

    }

    @Test
    void doGetTestWithNameAndEmail() throws ServletException, IOException, LoginException {
        context.load().json("/com/adobe/aem/sample/site/core/servlets/GenerateKeyServlet/resource.json", "/etc/aem");
        Resource resource = context.currentResource("/etc/aem");
        when(request.getParameter("name")).thenReturn("shivam");
        when(request.getParameter("email")).thenReturn("abhinweQEQWEav@gmail.com");
        when(tokenDetailService.getResourcePath()).thenReturn("/etc/aem");
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        generateKey.doGet(request, response);
        verify(servletOutputStream).write(captor.capture());
        String result = new String(captor.getValue());
        assertNotNull(generateKey);
        assertTrue(result.contains("New Token"));
    }
    @Test
    void doGetTestTokenAlreadyPresent() throws ServletException, IOException {
        context.load().json("/com/adobe/aem/sample/site/core/servlets/GenerateKeyServlet/resource.json", "/etc/aem");
        Resource resource = context.currentResource("/etc/aem");
        when(request.getParameter("name")).thenReturn("shivam");
        when(request.getParameter("email")).thenReturn("abhinav@gmail.com");
        when(tokenDetailService.getResourcePath()).thenReturn("/etc/aem");
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        generateKey.doGet(request, response);
        verify(servletOutputStream).write(captor.capture());
        String result = new String(captor.getValue());
        assertNotNull(generateKey);
        assertEquals("{\"result\":{\"name\":\"abhinav\",\"email\":\"abhinav@gmail.com\",\"token\":\"b48fb6bc-3ced-4ebc-ab32-5e96ac755176\",\"status\":\"Already present\"}}",result);
    }

    @Test
    void doGetTestWithToken() throws ServletException, IOException {

        when(request.getParameter(eq("name"))).thenReturn("");
        when(request.getParameter(eq("email"))).thenReturn("");
        when(request.getParameter(eq("token"))).thenReturn("767786");
        Map<String, String> map = new HashMap<>();
        map.put("name", "shivam");
        map.put("email", "shivam@123");
        map.put("token", "767786");
        when(tokenDetailService.getTokenDetails("767786")).thenReturn(map);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        generateKey.doGet(request, response);
        verify(servletOutputStream).write(captor.capture());
        String result = new String(captor.getValue());
        assertNotNull(generateKey);
        assertEquals("{\"result\":{\"name\":\"shivam\",\"email\":\"shivam@123\",\"status\":\"Valid Token\"}}",result);

    }

    @Test
    void doGetTestWithInvalidToken() throws ServletException, IOException {
        when(request.getParameter(eq("name"))).thenReturn("");
        when(request.getParameter(eq("email"))).thenReturn("");
        when(request.getParameter(eq("token"))).thenReturn("767786");
        Map<String, String> map = new HashMap<>();
        when(tokenDetailService.getTokenDetails("767786")).thenReturn(map);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        generateKey.doGet(request, response);
        verify(servletOutputStream).write(captor.capture());
        String result = new String(captor.getValue());
        assertNotNull(generateKey);
        assertEquals("{\"result\":{\"status\":\"Error\",\"message\":\"Invalid Token\"}}",result);


    }
}
