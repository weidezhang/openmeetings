package org.openmeetings.jira.plugin.gateway;

import java.io.IOException;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class OmRestServiceTest {
	private static final Logger log = LoggerFactory.getLogger(OmRestServiceTest.class);
	
    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    
    private OmRestService omRestService;

    @Before
    public void setup() {
//        mockRequest = mock(HttpServletRequest.class);
//        mockResponse = mock(HttpServletResponse.class);
    	omRestService = new OmRestService();
    }

    @After
    public void tearDown() {

    }
    
    private URI getBaseURI() {
		return UriBuilder.fromUri(
				"http://localhost:5080/openmeetings/services/UserService/getSession").build();
	}

    @Test
    public void testCall() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
//        String expected = "test";
//        when(mockRequest.getParameter(Mockito.anyString())).thenReturn(expected);
//        assertEquals(expected,mockRequest.getParameter("some string"));
    	
    	///////////
    	//String rstream = omRestService.call("http://localhost:5080/openmeetings/services/UserService/getSession", null);
    	//log.error("error",rstream);
    	////////////   	
    	
    	
    	
//    	ClientConfig config = new DefaultClientConfig();
//		Client client = Client.create(config);
//		WebResource service = client.resource(getBaseURI());
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
//		
//		String rstream = service.accept(
//				MediaType.TEXT_XML).get(String.class);
//		
		
		
			
		
//		Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rstream);      
//
//        
//        XPathFactory factory = XPathFactory.newInstance();
//        XPath xPath=factory.newXPath();
//        
//        //Get all search Result nodes
//        NodeList nodes = (NodeList)xPath.evaluate("/getSessionResponse/return", response, XPathConstants.NODESET);
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
		
    }
}
