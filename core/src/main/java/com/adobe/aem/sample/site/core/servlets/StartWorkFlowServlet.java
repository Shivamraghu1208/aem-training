package com.adobe.aem.sample.site.core.servlets;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import java.io.IOException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StartWorkFlowServlet - this servlet is used to start a workflow in AEM.
 */
@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/checkWorkflow", "sling.servlet.extensions=json"})
public class StartWorkFlowServlet extends SlingSafeMethodsServlet {

    /**
     * The logger - A Logger object used to log message related to StartWorkFlowServlet .
     */
    Logger logger = LoggerFactory.getLogger(StartWorkFlowServlet.class);

    /**
     * The workflowService - workflowService object used to get workflowService.
     */
    @Reference
    protected WorkflowService workflowService;

    /**
     * Handles GET requests to start a workflow.
     *
     * @param request  SlingHttpServletRequest object.
     * @param response SlingHttpServletResponse object.
     * @throws ServletException
     * @throws IOException
     */
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = (Session)resourceResolver.adaptTo(Session.class);
        WorkflowSession workflowSession = this.workflowService.getWorkflowSession(session);
        try {
            WorkflowModel model = workflowSession.getModel("/var/workflow/models/create-version-");
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", "/content/aemtraining/shivam");
            Workflow workflow = workflowSession.startWorkflow(model, workflowData);
            boolean active = workflow.isActive();
            JsonObject responseJson=new JsonObject();
            responseJson.addProperty("Status",active);
            response.setContentLength(responseJson.toString().getBytes().length);
            response.setContentType("application/json");
            response.getOutputStream().write(responseJson.toString().getBytes());

        } catch (WorkflowException e) {
            logger.error("WorkflowException : {}",e.getMessage(),e);
        }
    }
}