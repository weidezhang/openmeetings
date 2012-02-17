package org.openmeetings.jira.plugin.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.openmeetings.jira.plugin.ao.adminconfiguration.OmPluginSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class OmGateway {
	
	private static final Logger log = LoggerFactory.getLogger(OmGateway.class);
	
	private OmRestService omRestService;
	private OmPluginSettings omPluginSettings;

	public OmGateway(OmRestService omRestService,
			OmPluginSettings omPluginSettings) {		
		this.omRestService = omRestService;
		this.omPluginSettings = omPluginSettings;
	}	
	
	public void loginUser() throws XPathExpressionException, IOException, ServletException, SAXException, ParserConfigurationException{
		
		String url = (String)omPluginSettings.getSomeInfo("url"); 
    	String port = (String)omPluginSettings.getSomeInfo("port");         	
    	String userpass = (String)omPluginSettings.getSomeInfo("userpass");  
    	String omusername = (String)omPluginSettings.getSomeInfo("username"); 
    	String key = (String)omPluginSettings.getSomeInfo("key"); 
		
    	String sessionURL = "http://"+url+":"+port+"/openmeetings/services/UserService/getSession";
    	
		omRestService.call(sessionURL, null);
	}
	
}
