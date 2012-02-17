package org.openmeetings.jira.plugin.servlet;


import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmeetings.jira.plugin.ao.adminconfiguration.AdminConfiguration;
import org.openmeetings.jira.plugin.ao.adminconfiguration.AdminConfigurationService;
import org.openmeetings.jira.plugin.ao.adminconfiguration.AdminConfigurationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.*;

public class Backup2AdminServlet extends HttpServlet
{
	private static final Logger log = LoggerFactory.getLogger(Backup2AdminServlet.class);
	
    private final UserManager userManager;
    //private final TemplateRenderer renderer;
    //private final LoginUriProvider loginUriProvider;
    private TemplateRenderer templateRenderer;
    private com.atlassian.jira.user.util.UserManager jiraUserManager;
   

    //private final ActiveObjects ao;
    private final AdminConfigurationService adminOmConfigurationService;
    
    private static final String NEW_BROWSER_TEMPLATE = "/templates/adminnew.vm";
    private static final String EDIT_BROWSER_TEMPLATE = "/templates/adminedit.vm";
    
//    public TodoServlet(ActiveObjects ao)
//    {
//        this.ao = checkNotNull(ao);
//    }
    
    public Backup2AdminServlet(com.atlassian.jira.user.util.UserManager jiraUserManager, 
				    		TemplateRenderer templateRenderer,
				    		AdminConfigurationService adminOmConfigurationService,
				    		UserManager userManager)
    {
        this.userManager = userManager;              
        this.adminOmConfigurationService = checkNotNull(adminOmConfigurationService);
        this.templateRenderer = templateRenderer;
        this.jiraUserManager = jiraUserManager;
       
        
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
      
        User user = getCurrentUser(request);
      
        if ("y".equals(request.getParameter("edit"))) {
            // Perform update if the "edit" param is passed in
            // First get the issue from the key that's passed in
            //IssueService.IssueResult issueResult = issueService.getIssue(user, request.getParameter("key"));
            AdminConfiguration omConfig =  adminOmConfigurationService.get(request.getParameter("key"));
            //MutableIssue issue = issueResult.getIssue();
            // Next we need to validate the updated issue
            //AdminConfiguration adminConfiguration = adminConfiguration;
            
//            adminConfiguration.setOmUrl(request.getParameter("url"));
//            adminConfiguration.setOmPort(request.getParameter("port"));
//            adminConfiguration.setOmUserName(request.getParameter("port"));
//            adminConfiguration.setOmUserPass(request.getParameter("port"));
//            adminConfiguration.setOmKey(request.getParameter("port"));
            
            AdminConfiguration result = adminOmConfigurationService.saveConfiguration(request.getParameter("url"), 
            		request.getParameter("port"), 
            		request.getParameter("key"), 
            		request.getParameter("userpass"), 
            		request.getParameter("username"), 
            		request.getParameter("key"));
            
            
//            IssueService.UpdateValidationResult result = issueService.validateUpdate(user, issue.getId(),
//                    issueInputParameters);
      
            if (result == null) {
                // If the validation fails, we re-render the edit page with the errors in the context
                Map<String, Object> context = Maps.newHashMap();
                context.put("omConfig", result);
                context.put("errors", result.getIdentifier());
                response.setContentType("text/html;charset=utf-8");
                templateRenderer.render(EDIT_BROWSER_TEMPLATE, context, response.getWriter());
            } else {
                // If the validation passes, we perform the update then redirect the user back to the
                // page with the list of issues
                
            	  Map<String, Object> context = Maps.newHashMap();
                  context.put("issue", result);
                  context.put("errors", result.getIdentifier());
                  response.setContentType("text/html;charset=utf-8");
                  templateRenderer.render(NEW_BROWSER_TEMPLATE, context, response.getWriter());
            	
            	//issueService.update(user, result);
                //response.sendRedirect("openmeetingsadmin");
            }
      
        } else {
            // Perform creation if the "new" param is passed in
//            // First we need to validate the new issue being created
//            IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
//            // We're only going to set the summary and description. The rest are hard-coded to
//            // simplify this tutorial.
//            issueInputParameters.setSummary(request.getParameter("summary"));
//            issueInputParameters.setDescription(request.getParameter("description"));
//            // We need to set the assignee, reporter, project, and issueType...
//            // For assignee and reporter, we'll just use the currentUser
//            issueInputParameters.setAssigneeId(user.getName());
//            issueInputParameters.setReporterId(user.getName());
//            // We hard-code the project name to be the project with the TUTORIAL key
//            Project project = projectService.getProjectByKey(user, "TUTORIAL").getProject();
//            issueInputParameters.setProjectId(project.getId());
//            // We also hard-code the issueType to be a "bug" == 1
//            issueInputParameters.setIssueTypeId("1");
//            // Perform the validation
//            IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
      
//            if () {
//                // If the validation fails, render the list of issues with the error in a flash message
//                List<Issue> issues = getIssues(request);
                Map<String, Object> context = Maps.newHashMap();
                context.put("omConfig", "issues");
                context.put("errors", "result");
        		response.setContentType("text/html;charset=utf-8");
                templateRenderer.render(NEW_BROWSER_TEMPLATE, context, response.getWriter());
//            } else {
//                // If the validation passes, redirect the user to the main issue list page
//                //issueService.create(user, result);
//                response.sendRedirect("issuecrud");
//            }
        }
    }
    

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String username = userManager.getRemoteUsername(request);
        if (username != null && !userManager.isSystemAdmin(username))
        {
            redirectToLogin(request, response);
            return;
        }
        
        for (AdminConfiguration omConfig : adminOmConfigurationService.all()) // (2)
        {
           // w.printf("<li><%2$s> %s </%2$s></li>", omConfig.getDescription(), omConfig.isComplete() ? "strike" : "strong");
        }
        /////////
        if ("y".equals(request.getParameter("new"))) {
            // Renders new.vm template if the "new" parameter is passed
     
            // Create an empty context map to pass into the render method
            Map<String, Object> context = Maps.newHashMap();
            // Make sure to set the contentType otherwise bad things happen
            response.setContentType("text/html;charset=utf-8");
            // Render the velocity template (new.vm). Since the new.vm template 
            // doesn't need to render any in dynamic content, we just pass it an empty context
           templateRenderer.render(NEW_BROWSER_TEMPLATE, context, response.getWriter());
        } else if ("y".equals(request.getParameter("edit"))) {
            // Renders edit.vm template if the "edit" parameter is passed
     
            // Retrieve issue with the specified key
        	AdminConfiguration omConfig =  adminOmConfigurationService.get(request.getParameter("key"));// req.getParameter("key"));
            Map<String, Object> context = Maps.newHashMap();
            context.put("omConfig", omConfig);
            response.setContentType("text/html;charset=utf-8");
            // Render the template with the issue inside the context
            templateRenderer.render(EDIT_BROWSER_TEMPLATE, context, response.getWriter());
        } else {
//            // Render the list of issues (list.vm) if no params are passed in
//            List<Issue> issues = getIssues(request);
//            Map<String, Object> context = Maps.newHashMap();
//            context.put("issues", issues);
//            response.setContentType("text/html;charset=utf-8");
//            // Pass in the list of issues as the context
//            templateRenderer.render(LIST_BROWSER_TEMPLATE, context, response.getWriter());
        	// Retrieve issue with the specified key
        	AdminConfiguration omConfig =  adminOmConfigurationService.get(request.getParameter("key"));// req.getParameter("key"));
            Map<String, Object> context = Maps.newHashMap();
            context.put("omConfig", omConfig);
            response.setContentType("text/html;charset=utf-8");
            // Render the template with the issue inside the context
            templateRenderer.render(EDIT_BROWSER_TEMPLATE, context, response.getWriter());
        }
        /////////////
       // response.setContentType("text/html;charset=utf-8");
        //renderer.render("admin.vm", response.getWriter());
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        //response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request)
    {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null)
        {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
    
    private User getCurrentUser(HttpServletRequest req) {
	    // To get the current user, we first get the username from the session.
	    // Then we pass that over to the jiraUserManager in order to get an
	    // actual User object.
	    return jiraUserManager.getUser(userManager.getRemoteUsername(req));
	}
}
