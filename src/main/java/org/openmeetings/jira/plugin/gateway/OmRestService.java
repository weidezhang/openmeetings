package org.openmeetings.jira.plugin.gateway;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


public class OmRestService {
	
	private static final Logger log = LoggerFactory.getLogger(OmRestService.class);
	
	private URI getURI(String url) {
		return UriBuilder.fromUri(
				url).build();
	}
	
	private  String getEncodetURI(String url) throws MalformedURLException {
		return new URL(url).toString().replaceAll(" ","%20");
	}	
	
	public LinkedHashMap<String, Element> call(String request, Object param)throws IOException, ServletException, SAXException, ParserConfigurationException, XPathExpressionException, DocumentException
	{				
		HttpClient client = new HttpClient();

        GetMethod method = new GetMethod(getEncodetURI(request).toString());
		int statusCode = client.executeMethod(method);
		
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
        
        InputStream rstream = null;

        rstream = method.getResponseBodyAsStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
        
        SAXReader reader = new SAXReader();
	    String line;
	    Document document = null;
	    while ((line = br.readLine()) != null) {
	    	document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
	    	//System.out.println("line"+line);
	    
	    }
	    
        Element root = document.getRootElement(); 
        
        LinkedHashMap<String,Element> elementMap = new LinkedHashMap<String,Element>();       	                    
        
        for ( @SuppressWarnings("unchecked")
            
        	Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {	                
	            Element item = i.next();            
	            String nodeVal = item.getName();
	            elementMap.put(nodeVal, item);
        	}
		
		return elementMap;
	    
	}	
	
}
