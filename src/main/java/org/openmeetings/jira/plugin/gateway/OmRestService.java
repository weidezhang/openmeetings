package org.openmeetings.jira.plugin.gateway;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.openmeetings.jira.plugin.ao.adminconfiguration.OmPluginSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OmRestService {
	
	private static final Logger log = LoggerFactory.getLogger(OmRestService.class);
	
	
	public String call(String request, Object param)throws IOException, ServletException, SAXException, ParserConfigurationException, XPathExpressionException
	{
		//String request = "http://api.search.yahoo.com/WebSearchService/V1/webSearch";
	    HttpClient client = new HttpClient();
	
	    PostMethod method = new PostMethod(request);
	
	    // Add POST parameters
	    
	    method.addParameter("roomId","1");
//	
//	    method.addParameter("query","umbrella");
//	
//	    method.addParameter("results","10");	    
	    
	    // Send POST request

	
	    InputStream rstream = null;
	
	    
	    // Get the response body
	
	    try {
			rstream = method.getResponseBodyAsStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    int statusCode = 0;
		try {
			statusCode = client.executeMethod(method);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    

	    switch (statusCode) {
	    
		    case 200: {
		    	
		    System.out.println("Success connection");
	
		    break;
	
		    }
		    case 400: {
	
		    System.out.println("Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect."); 
	
		    System.out.println("Change the parameter appcd to appid and this error message will go away.");
	
		    break;

	    }

		    case 403: {
	
		    System.out.println("Forbidden. You do not have permission to access this resource, or are over your rate limit.");
	
		    break;

	    }

		    case 503: {
	
		    System.out.println("Service unavailable. An internal problem prevented us from returning data to you.");
	
		    break;

	    }

		    default: System.out.println("Your call to Yahoo! Web Services returned an unexpected  HTTP status of: " + statusCode);

	    }
	    
	    
	 // Process response
//        Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);      
//
//        
//        XPathFactory factory = XPathFactory.newInstance();
//        XPath xPath=factory.newXPath();
//        
//        //Get all search Result nodes
//        NodeList nodes = (NodeList)xPath.evaluate("/ResultSet/Result", response, XPathConstants.NODESET);
//        int nodeCount = nodes.getLength();
//        
//        //iterate over search Result nodes
//        for (int i = 0; i < nodeCount; i++) {
//            //Get each xpath expression as a string
//        	String title = (String)xPath.evaluate("Title", nodes.item(i), XPathConstants.STRING);
//            String summary = (String)xPath.evaluate("Summary", nodes.item(i), XPathConstants.STRING);
//            String url = (String)xPath.evaluate("Url", nodes.item(i), XPathConstants.STRING);
//            //print out the Title, Summary, and URL for each search result
//            System.out.println("Title: " + title);
//            System.out.println("Summary: " + summary);
//            System.out.println("URL: " + url);
//            System.out.println("--");
//            
//        }
//        System.out.println(rstream);
		return rstream.toString();
	    
	}
}
