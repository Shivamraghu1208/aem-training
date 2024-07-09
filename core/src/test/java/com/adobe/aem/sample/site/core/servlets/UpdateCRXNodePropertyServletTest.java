package com.adobe.aem.sample.site.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class UpdateCRXNodePropertyServletTest {

    private final AemContext context=new AemContext();
   private UpdateCRXNodePropertyServlet updateCRXNodePropertyServlet;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private Resource resource;

    @Mock
    private ServletOutputStream servletOutputStream;

    @Captor
    private ArgumentCaptor<byte[]> captor;

    @Test
    void doGetTest() throws IOException {
        context.registerService(ResourceResolverFactory.class,resourceResolverFactory);
        context.load().json("/com/adobe/aem/sample/site/core/servlets/GenerateKeyServlet/resource.json", "/etc/shivam");
         resource = context.currentResource("/etc/shivam");
        updateCRXNodePropertyServlet = context.registerInjectActivateService(UpdateCRXNodePropertyServlet.class);
       when(request.getParameter("value")).thenReturn("shivam");
       String path="/etc/shivam";
       when(request.getParameter("path")).thenReturn(path);
       when(response.getOutputStream()).thenReturn(servletOutputStream);
       updateCRXNodePropertyServlet.doGet(request,response);
       verify(servletOutputStream).write(captor.capture());
        String result =new String(captor.getValue());
        assertEquals("Success",result);

    }
    @Test
    void doGetTestWithNullPath() throws IOException {
        context.registerService(ResourceResolverFactory.class,resourceResolverFactory);
        context.load().json("/com/adobe/aem/sample/site/core/servlets/GenerateKeyServlet/resource.json", "/etc/shivam");
        resource = context.currentResource("/etc/shivam");
        updateCRXNodePropertyServlet = context.registerInjectActivateService(UpdateCRXNodePropertyServlet.class);
        when(request.getParameter("value")).thenReturn("shivam");
        String path=null;
        when(request.getParameter("path")).thenReturn(path);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        updateCRXNodePropertyServlet.doGet(request,response);
        verify(servletOutputStream).write(captor.capture());
        String result =new String(captor.getValue());
        assertEquals("Resource not found",result);

    }
}