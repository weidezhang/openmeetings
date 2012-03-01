/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

public class AdminServlet extends HttpServlet
{
	private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);
	
    private final UserManager userManager;    
    private TemplateRenderer templateRenderer;
    private com.atlassian.jira.user.util.UserManager jiraUserManager;
    private final LoginUriProvider loginUriProvider;
   
    private OmPluginSettings omPluginSettings; 
    
    private static final String OM_CONFIG_TEMPLATE = "/templates/config/omconfig.vm";
    
    public AdminServlet(com.atlassian.jira.user.util.UserManager jiraUserManager, 
				    		TemplateRenderer templateRenderer,				    		
				    		UserManager userManager,
				    		OmPluginSettings omPluginSettings,
				    		LoginUriProvider loginUriProvider)
    {
        this.userManager = userManager;
        this.templateRenderer = templateRenderer;
        this.jiraUserManager = jiraUserManager;
        //this.pluginSettingsFactory = pluginSettingsFactory;
        this.omPluginSettings = omPluginSettings;
        this.loginUriProvider = loginUriProvider;
       
        
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String username = userManager.getRemoteUsername(request);
        if (username != null && !userManager.isSystemAdmin(username))
        {
            redirectToLogin(request, response);
            return;
        }else if(username == null){
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
            
            //response.sendRedirect(request.getContextPath() + "/plugins/servlet/openmeetingsadmin");
            response.sendRedirect(request.getContextPath() + "/secure/AdminSummary.jspa");
       
    }
    

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
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
    
}
