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
/**
 * The CustomCarCodesServlet - servlet handles the GET request and returns a list of car codes.
 */
@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET",
        "sling.servlet.paths=/bin/getCarCodes",
        "sling.servlet.extensions=json"})
public class CustomCarCodesServlet extends SlingSafeMethodsServlet {


    /**
     * Handles GET requests and send response with a JSON object containing car codes and a status.
     *
     * @param request  the  SlingHttpServletRequest
     * @param response the SlingHttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        JsonObject responseJson = new JsonObject();

        JsonArray responseCodes = new JsonArray();
        responseCodes.add("CA1234");
        responseCodes.add("CA4521");
        responseCodes.add("CA9875");
        responseJson.add("responseCodes", responseCodes);
        responseJson.addProperty("status", 200);
        response.setContentLength(responseJson.toString().getBytes().length);
        response.setContentType("application/json");
        response.getOutputStream().write(responseJson.toString().getBytes());
    }
}
