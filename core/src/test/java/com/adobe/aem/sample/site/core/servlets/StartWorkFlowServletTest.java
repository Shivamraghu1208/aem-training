package com.adobe.aem.sample.site.core.servlets;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class StartWorkFlowServletTest {

    private final AemContext context=new AemContext();

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Session session;

    @Mock
    ServletOutputStream servletOutputStream;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    WorkflowSession workflowSession;

    @Mock
    WorkflowService workflowService;

    @Mock
    WorkflowModel model;

    @Mock
    Workflow workflow;

    @Mock
    WorkflowData workflowData;

    @Captor
    private ArgumentCaptor<byte[]> captor;

    private StartWorkFlowServlet startWorkFlowServlet;

    @Test
    void testWorkFlowIsNotActive() throws WorkflowException, ServletException, IOException {
        context.registerService(WorkflowService.class, workflowService);
        startWorkFlowServlet =context.registerInjectActivateService(StartWorkFlowServlet.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(workflowService.getWorkflowSession(session)).thenReturn(workflowSession);
        when(workflowSession.getModel("/var/workflow/models/create-version-")).thenReturn(model);
        when(workflowSession.newWorkflowData("JCR_PATH", "/content/aemtraining/shivam")).thenReturn(workflowData);
        when(workflowSession.startWorkflow(model,workflowData)).thenReturn(workflow);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        startWorkFlowServlet.doGet(request,response);
        verify(servletOutputStream).write(captor.capture());
        String result=new String(captor.getValue());
        assertEquals("{\"Status\":false}",result);

    }
    @Test
    void testWorkFlowIsActive() throws WorkflowException, ServletException, IOException {
        context.registerService(WorkflowService.class, workflowService);
        startWorkFlowServlet =context.registerInjectActivateService(StartWorkFlowServlet.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(workflowService.getWorkflowSession(session)).thenReturn(workflowSession);
        when(workflowSession.getModel("/var/workflow/models/create-version-")).thenReturn(model);
        when(workflowSession.newWorkflowData("JCR_PATH", "/content/aemtraining/shivam")).thenReturn(workflowData);
        when(workflowSession.startWorkflow(model,workflowData)).thenReturn(workflow);
        when(workflow.isActive()).thenReturn(true);
        when(response.getOutputStream()).thenReturn(servletOutputStream);
        startWorkFlowServlet.doGet(request,response);
        verify(servletOutputStream).write(captor.capture());
        String result=new String(captor.getValue());
        assertEquals("{\"Status\":true}",result);
    }

}