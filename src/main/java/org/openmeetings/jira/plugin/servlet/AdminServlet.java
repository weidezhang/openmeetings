package org.openmeetings.jira.plugin.servlet;


import java.io.IOException;
import java.io.PrintWriter;
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
import org.openmeetings.jira.plugin.ao.omrooms.Room;
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
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.*;

public class AdminServlet extends HttpServlet
{
	private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);
	
    private final UserManager userManager;
    //private final TemplateRenderer renderer;
    //private final LoginUriProvider loginUriProvider;
    private TemplateRenderer templateRenderer;
    private com.atlassian.jira.user.util.UserManager jiraUserManager;
   

    //private final ActiveObjects ao;
    private final AdminConfigurationService adminOmConfigurationService;
    final PluginSettingsFactory pluginSettingsFactory;
    
    private static final String NEW_BROWSER_TEMPLATE = "/templates/adminnew.vm";
    private static final String EDIT_BROWSER_TEMPLATE = "/templates/adminedit.vm";
    private static final String OM_CONFIG_TEMPLATE = "/templates/omconfig.vm";
//    public TodoServlet(ActiveObjects ao)
//    {
//        this.ao = checkNotNull(ao);
//    }
    
    public AdminServlet(com.atlassian.jira.user.util.UserManager jiraUserManager, 
				    		TemplateRenderer templateRenderer,
				    		AdminConfigurationService adminOmConfigurationService,
				    		UserManager userManager, PluginSettingsFactory pluginSettingsFactory)
    {
        this.userManager = userManager;              
        this.adminOmConfigurationService = checkNotNull(adminOmConfigurationService);
        this.templateRenderer = templateRenderer;
        this.jiraUserManager = jiraUserManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
       
        
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
        	 //Retrieve issue with the specified key
        	//String url = getSomeInfo("url").toString();
        	
//        	String url = request.getParameter("url"); 
//        	String port = request.getParameter("port");        	
//        	String userpass = request.getParameter("userpass"); 
//        	String omusername = request.getParameter("username"); 
//        	String key = request.getParameter("key");
        	
        	String url; 
        	String port;         	
        	String userpass;
        	String omusername;
        	String key; 
        	
        	if( getSomeInfo("url").equals(null)){
        		url = "localhost"; 
	        	port = "5080";         	
	        	userpass = "admin";  
	        	omusername = "admin"; 
	        	key = "Jira";        		
        	}else{
	        	url = (String)getSomeInfo("url"); 
	        	port = (String)getSomeInfo("port");         	
	        	userpass = (String)getSomeInfo("userpass");  
	        	omusername = (String)getSomeInfo("username"); 
	        	key = (String)getSomeInfo("key"); 
        	}
        	
            Map<String, Object> context = Maps.newHashMap();
            
            context.put("url", url);
            context.put("port", port);
            context.put("username", omusername);
            context.put("userpass", userpass);
            context.put("key", key);
            
            response.setContentType("text/html;charset=utf-8");
            // Render the template with the issue inside the context
            templateRenderer.render(OM_CONFIG_TEMPLATE, context, response.getWriter());
        }
        /////////////
       // response.setContentType("text/html;charset=utf-8");
        //renderer.render("admin.vm", response.getWriter());
        
//        final PrintWriter w = response.getWriter();
//        w.write("<h1>Test Admin Key-Values</h1>");
// 
//        
//     // the form to post more TODOs
//        w.write("<form method=\"post\">");
//        w.write("<input type=\"text\" name=\"url\" size=\"25\"/>");
//        w.write("<input type=\"text\" name=\"port\" size=\"25\"/>");
//        w.write("<input type=\"text\" name=\"username\" size=\"25\"/>");
//        w.write("<input type=\"text\" name=\"userpass\" size=\"25\"/>");
//        w.write("<input type=\"text\" name=\"key\" size=\"25\"/>");
//        w.write("&nbsp;&nbsp;");
//        w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
//        w.write("</form>");
//        
// 
//        w.write("<ol>");
// 
//        //getSomeInfo("url");
//            w.printf("<li> %s </li>", getSomeInfo("url"));
//            w.printf("<li><%2$s> %s </%2$s></li>", getSomeInfo("port"), true ? "strong" : "strong");
//            w.printf("<li> %s </li>", getSomeInfo("userpass"));
//            w.printf("<li> %s </li>", getSomeInfo("username"));
//            w.printf("<li> %s </li>", getSomeInfo("key"));
//       
// 
//        w.write("</ol>");
//        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");
// 
//        w.close();

    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
      
        User user = getCurrentUser(request);
      
        if ("y".equals(request.getParameter("edit"))) {
          

      
        } else {
        	String url = request.getParameter("url"); 
        	String port = request.getParameter("port");        	
        	String userpass = request.getParameter("userpass"); 
        	String username = request.getParameter("username"); 
        	String key = request.getParameter("key");
        	
        	storeSomeInfo("url", url);
        	storeSomeInfo("port", port);
        	storeSomeInfo("userpass", userpass);
        	storeSomeInfo("username", username);
        	storeSomeInfo("key", key);
        	
            
            response.sendRedirect(request.getContextPath() + "/plugins/servlet/openmeetingsadmin");
        }
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
    
    public void storeSomeInfo(String key, String value) {
        // createGlobalSettings is nice and fast, so there's no need to cache it (it's memoised when necessary).
        pluginSettingsFactory.createGlobalSettings().put("openmeetings:" + key, value);
        
    }
 
    public Object getSomeInfo(String key) {
        return pluginSettingsFactory.createGlobalSettings().get("openmeetings:" + key);
    }
 
    public void storeSomeInfo(String projectKey, String key, String value) {
        // createSettingsForKey is nice and fast, so there's no need to cache it (it's memoised when necessary).
        pluginSettingsFactory.createSettingsForKey(projectKey).put("openmeetings:" + key, value);
    }
 
    public Object getSomeInfo(String projectKey, String key) {
        return pluginSettingsFactory.createSettingsForKey(projectKey).get("openmeetings:" + key);
    }
}
