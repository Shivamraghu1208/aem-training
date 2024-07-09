package com.adobe.aem.sample.site.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CustomCarCodesServletTest {

   private final AemContext context=new AemContext();

   @Mock
   private MockSlingHttpServletRequest request;

   @Mock
   private MockSlingHttpServletResponse response;

   @Mock
   private ServletOutputStream servletOutputStream;

   @Captor
   private ArgumentCaptor<byte[]> captor;

   private CustomCarCodesServlet customCarCodesServlet;

   @Test
   void doGetTest() throws ServletException, IOException {
       customCarCodesServlet = context.registerInjectActivateService(CustomCarCodesServlet.class);
       when(response.getOutputStream()).thenReturn(servletOutputStream);
       customCarCodesServlet.doGet(request,response);
       verify(servletOutputStream).write(captor.capture());
       String result=new String(captor.getValue());
      assertEquals("{\"responseCodes\":[\"CA1234\",\"CA4521\",\"CA9875\"],\"status\":200}",result);

   }

}