package com.adobe.aem.sample.site.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET",
        "sling.servlet.paths=/bin/getCarCodes",
        "sling.servlet.extensions=json"})
public class CustomCarCodesServlet extends SlingSafeMethodsServlet {


    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        JsonObject object = new JsonObject();

        JsonArray responseCodes = new JsonArray();
        responseCodes.add("CA1234");
        responseCodes.add("CA4521");
        responseCodes.add("CA9875");

        object.add("responseCodes", responseCodes);

        object.addProperty("status", 200);

        response.getWriter().write(object.toString());
    }
}
