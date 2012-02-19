package org.openmeetings.jira.plugin.gateway;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URI;
import java.util.Iterator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import java.io.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

public class OmRestServiceTest {
	private static final Logger log = LoggerFactory.getLogger(OmRestServiceTest.class);
	
    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    
    @Before
    public void setup() {
new OmRestService();
    }

    @After
    public void tearDown() {

    }
    
    private URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://localhost:5080/openmeetings/services/UserService/getSession").build();
	}

    //@Test
    public void testCallJersey() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, DocumentException {

    	ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());
//		// Get XML
//		System.out.println(service.path("rest").path("todo").accept(
//				MediaType.TEXT_XML).get(String.class));
//		// Get XML for application
//		System.out.println(service.path("rest").path("todo").accept(
//				MediaType.APPLICATION_JSON).get(String.class));
//		// Get JSON for application
//		System.out.println(service.path("rest").path("todo").accept(
//				MediaType.APPLICATION_XML).get(String.class));
//    	
//		System.out.println(service.accept(
//				MediaType.APPLICATION_JSON).get(String.class));
		
		String rstream2 = service.accept(
				MediaType.APPLICATION_JSON).get(String.class);
		
		
		
    	SAXReader reader = new SAXReader();
        Document document = null;
		try {
			reader.isValidating();
			document = reader.read(new ByteArrayInputStream(rstream2.getBytes("UTF-8")));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Element root = document.getRootElement();
       
        log.error(root.asXML());
        
        
        for ( @SuppressWarnings("unchecked")
            
        	Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
                
            Element item = i.next();
            
            log.debug(item.getName());
            //log.error(item.getNamespacePrefix());
            //log.error(item.getPath());
            //log.error(item.getXPathResult(10).getName());//.getPath("/ns:getSessionResponse/ns:return/ax24:session_id"));
            log.error(item.elementText("session_id"));
            
            String nodeVal = item.getName();
    	
    		}
    	
    }
    
    
    @Test
    public void testCallApacheCommons() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, DocumentException {
        
    	    	
    	String request= "http://localhost:5080/openmeetings/services/UserService/getSession";
		HttpClient client = new HttpClient();

        GetMethod method = new GetMethod(request);


		// Send GET request

        int statusCode = client.executeMethod(method);
        
        InputStream rstream = null;

        rstream = method.getResponseBodyAsStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
        
        SAXReader reader = new SAXReader();
	    String line;
	    Document document = null;
	    while ((line = br.readLine()) != null) {
	    	document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
	    	System.out.println("line"+line);
	    
	    }
	
	    //System.out.println("line2"+document.asXML());
	                  
        Element root = document.getRootElement();
       
        log.error(root.asXML());
        
        
        for ( @SuppressWarnings("unchecked")
            
        	Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
                
            Element item = i.next();
            
            log.debug(item.getName());
            //log.error(item.getNamespacePrefix());
            //log.error(item.getPath());
            //log.error(item.getXPathResult(10).getName());//.getPath("/ns:getSessionResponse/ns:return/ax24:session_id"));
            log.error(item.elementText("session_id"));
            
            String nodeVal = item.getName();
        }

        br.close();
			
    }
    
}
