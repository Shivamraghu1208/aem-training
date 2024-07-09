package com.adobe.aem.sample.site.core.servlets;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import java.io.IOException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The StartWorkFlowServlet - this servlet is used to start a workflow in AEM.
 */
@Component(service = {Servlet.class}, property = {"sling.servlet.methods=GET", "sling.servlet.paths=/bin/checkWorkflow", "sling.servlet.extensions=json"})
public class StartWorkFlowServlet extends SlingSafeMethodsServlet {


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
            workflowSession.startWorkflow(model, workflowData);
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
    }
}