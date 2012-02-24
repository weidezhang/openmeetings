package org.openmeetings.jira.plugin.servlet;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmeetings.jira.plugin.ao.adminconfiguration.OmPluginSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

public class AdminServlet extends HttpServlet
{
	private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);
	
    private final UserManager userManager;    
    private TemplateRenderer templateRenderer;
    private com.atlassian.jira.user.util.UserManager jiraUserManager;
   
    private OmPluginSettings omPluginSettings;    
    
    
    private static final String OM_CONFIG_TEMPLATE = "/templates/config/omconfig.vm";
    
    public AdminServlet(com.atlassian.jira.user.util.UserManager jiraUserManager, 
				    		TemplateRenderer templateRenderer,				    		
				    		UserManager userManager,
				    		OmPluginSettings omPluginSettings)
    {
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.jiraUserManager = jiraUserManager;
        //this.pluginSettingsFactory = pluginSettingsFactory;
        this.omPluginSettings = omPluginSettings;
       
        
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
           //templateRenderer.render(NEW_BROWSER_TEMPLATE, context, response.getWriter());
        } else if ("y".equals(request.getParameter("edit"))) {

        } else {
        	        	
        	String url; 
        	String port;         	
        	String userpass;
        	String omusername;
        	String key; 
        	
        	if( omPluginSettings.getSomeInfo("url") == null){
        		url = "localhost"; 
	        	port = "5080";         	
	        	userpass = "admin";  
	        	omusername = "admin"; 
	        	key = "Jira";        		
        	}else{
	        	url = (String)omPluginSettings.getSomeInfo("url"); 
	        	port = (String)omPluginSettings.getSomeInfo("port");         	
	        	userpass = (String)omPluginSettings.getSomeInfo("userpass");  
	        	omusername = (String)omPluginSettings.getSomeInfo("username"); 
	        	key = (String)omPluginSettings.getSomeInfo("key"); 
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

    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map params = request.getParameterMap();
      
        User user = getCurrentUser(request);
      
        if ("y".equals(request.getParameter("edit"))) {
          
        	
        	response.sendRedirect(request.getContextPath() + "secure/AdminSummary.jspa");
      
        } else {
        	String url = request.getParameter("url"); 
        	String port = request.getParameter("port");        	
        	String userpass = request.getParameter("userpass"); 
        	String username = request.getParameter("username"); 
        	String key = request.getParameter("key");
        	
        	omPluginSettings.storeSomeInfo("url", url);
        	omPluginSettings.storeSomeInfo("port", port);
        	omPluginSettings.storeSomeInfo("userpass", userpass);
        	omPluginSettings.storeSomeInfo("username", username);
        	omPluginSettings.storeSomeInfo("key", key);
        	
            
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
    
//    public void storeSomeInfo(String key, String value) {
//        // createGlobalSettings is nice and fast, so there's no need to cache it (it's memoised when necessary).
//        pluginSettingsFactory.createGlobalSettings().put("openmeetings:" + key, value);
//        
//    }
// 
//    public Object getSomeInfo(String key) {
//        return pluginSettingsFactory.createGlobalSettings().get("openmeetings:" + key);
//    }
// 
//    public void storeSomeInfo(String projectKey, String key, String value) {
//        // createSettingsForKey is nice and fast, so there's no need to cache it (it's memoised when necessary).
//        pluginSettingsFactory.createSettingsForKey(projectKey).put("openmeetings:" + key, value);
//    }
// 
//    public Object getSomeInfo(String projectKey, String key) {
//        return pluginSettingsFactory.createSettingsForKey(projectKey).get("openmeetings:" + key);
//    }
}
