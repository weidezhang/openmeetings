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
package org.openmeetings.jira.plugin.gateway;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

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
    
    
    //@Test
    public void testCallApacheCommons() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, DocumentException {
        
    	try{   	
    	String request= "http://localhost:5080/openmeetings/services/UserService/getSession";
		HttpClient client = new HttpClient();

        GetMethod method = new GetMethod(request);


		// Send GET request

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
	    	System.out.println("line"+line);
	    
	    }
	
	    //System.out.println("line2"+document.asXML());
	                  
        Element root = document.getRootElement();
       
        log.error(root.asXML());
        
        
        for ( @SuppressWarnings("unchecked")
            
        	Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
            
        	
        	
            Element item = i.next();
            
            if(item.getNamespacePrefix()=="soapenv"){
            	
            	System.out.println("Data:"+item.getData().toString());
            	
//	            log.debug(item.getName());
//	            //log.error(item.getNamespacePrefix());
//	            //log.error(item.getPath());
//	            //log.error(item.getXPathResult(10).getName());//.getPath("/ns:getSessionResponse/ns:return/ax24:session_id"));
//	            log.error(item.elementText("session_id"));
//	            log.error("Error: ",item.elementText("Text"));
//	            log.error(item.attributeValue("soapenv:Reason"));
//	            log.error(item.asXML());
//	            log.error(item.getPath());
//	            log.error(item.getData().toString());
//	            log.error(item.getName());
//	            log.error(item.getNamespacePrefix());
	            
	            
	            String nodeVal = item.getName();
        	}else{
        		log.debug(item.getName());
	            //log.error(item.getNamespacePrefix());
	            //log.error(item.getPath());
	            //log.error(item.getXPathResult(10).getName());//.getPath("/ns:getSessionResponse/ns:return/ax24:session_id"));
	            log.error(item.elementText("session_id"));
	            log.error("Error: ",item.elementText("Text"));
	            log.error(item.attributeValue("soapenv:Reason"));
        		
        	}
        }

        br.close();
		
    	}catch(Exception e){
    		
    		e.getStackTrace();
    		System.out.println("Exeption: "+ e);
    	}
    }
    
    private  String getEncodetURI(String url) throws MalformedURLException {
		return new URL(url).toString().replaceAll(" ","%20");
	}
    
    @Test
	public void callTestExceptions() throws Exception
	{	
//    	String request= "http://localhost:5080/openmeetings/services/UserService/getSession";
//
//		HttpClient client = new HttpClient();
//        GetMethod method = null;
//        
//		try {
//			method = new GetMethod(getEncodetURI(request).toString());
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		int statusCode = 0;
//		try {
//			statusCode = client.executeMethod(method);
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration. HttpException");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			System.out.println( new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration. IOException").getMessage());
//		}
//		
//		
//		switch (statusCode) {
//	    
//		    case 200: {		    	
//			    System.out.println("Success connection");			    
//			    break;	
//		    }
//		    case 400: {		    	
//				throw new Exception("Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect.");
//	    	
//			    //System.out.println("Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect."); 
//		     
//				//break;
//	
//		    }	
//		    case 403: {
//				throw new Exception("Forbidden. You do not have permission to access this resource, or are over your rate limit.");
//	
//				//System.out.println("Forbidden. You do not have permission to access this resource, or are over your rate limit.");
//	
//				//break;
//		
//		    }		
//		    case 503: {
//				throw new Exception("Service unavailable. An internal problem prevented us from returning data to you.");
//
//				//System.out.println("Service unavailable. An internal problem prevented us from returning data to you.");
//		
//			    //break;
//		
//		    }			    
//		    default:{ 
//					throw new Exception("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: " + statusCode);
//	
//			    	//System.out.println("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: " + statusCode);
//		    }
//		    
//	    }
//        
//		
//        InputStream rstream = null;
//
//        try {
//			rstream = method.getResponseBodyAsStream();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Exception("No Response Body");
//
//		}
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
//        
//        SAXReader reader = new SAXReader();
//	    String line;
//	    Document document = null;
//	    try {
//			while ((line = br.readLine()) != null) {
//				document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
//				//System.out.println("line"+line);
//			
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Exception("UnsupportedEncodingException by SAXReader");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Exception("IOException by SAXReader in REST Service");
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new Exception("DocumentException by SAXReader in REST Service");
//		}finally{
//			br.close();
//		}
//	    
//        Element root = document.getRootElement(); 
//        
//        LinkedHashMap<String,Element> elementMap = new LinkedHashMap<String,Element>();       	                    
//        
//        for ( @SuppressWarnings("unchecked")Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
//        	
//        	
//	            Element item = i.next();
//	            
//	            if(item.getNamespacePrefix()=="soapenv"){
//	            	throw new Exception(item.getData().toString());
//	            }else{
//	            	String nodeVal = item.getName();
//		            elementMap.put(nodeVal, item);
//		            log.error(item.asXML());
//	            }
//        	}
//		
//		//return elementMap;        
	    
	}	
	
    
    
    
}
