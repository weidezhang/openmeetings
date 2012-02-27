package org.openmeetings.jira.plugin.gateway;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.apache.commons.httpclient.HttpException;
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
	
	public LinkedHashMap<String, Element> call(String request, Object param) throws Exception
	{				
		HttpClient client = new HttpClient();
        GetMethod method = null;
        
		try {
			method = new GetMethod(getEncodetURI(request).toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int statusCode = 0;
		try {
			statusCode = client.executeMethod(method);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new Exception("Connection to OpenMeetings refused. Please check your OpenMeetings configuration.");
		}
		
		
		switch (statusCode) {
	    
		    case 200: {		    	
			    //System.out.println("Success connection");			    
			    break;	
		    }
		    case 400: {		    	
				throw new Exception("Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect.");
	    	
			    //System.out.println("Bad request. The parameters passed to the service did not match as expected. The Message should tell you what was missing or incorrect."); 
		     
				//break;
	
		    }	
		    case 403: {
				throw new Exception("Forbidden. You do not have permission to access this resource, or are over your rate limit.");
	
				//System.out.println("Forbidden. You do not have permission to access this resource, or are over your rate limit.");
	
				//break;
		
		    }		
		    case 503: {
				throw new Exception("Service unavailable. An internal problem prevented us from returning data to you.");

				//System.out.println("Service unavailable. An internal problem prevented us from returning data to you.");
		
			    //break;
		
		    }			    
		    default:{ 
					throw new Exception("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: " + statusCode);
	
			    	//System.out.println("Your call to OpenMeetings! Web Services returned an unexpected  HTTP status of: " + statusCode);
		    }
		    
	    }
        
		
        InputStream rstream = null;

        try {
			rstream = method.getResponseBodyAsStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("No Response Body");

		}

        BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
        
        SAXReader reader = new SAXReader();
	    String line;
	    Document document = null;
	    try {
			while ((line = br.readLine()) != null) {
				document = reader.read(new ByteArrayInputStream(line.getBytes("UTF-8")));
				//System.out.println("line"+line);
			
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("UnsupportedEncodingException by SAXReader");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("IOException by SAXReader in REST Service");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("DocumentException by SAXReader in REST Service");
		}finally{
			br.close();
		}
	    
        Element root = document.getRootElement(); 
        
        LinkedHashMap<String,Element> elementMap = new LinkedHashMap<String,Element>();       	                    
        
        for ( @SuppressWarnings("unchecked")Iterator<Element> i = root.elementIterator(); i.hasNext(); ) {
        	
        	
	            Element item = i.next();
	            
	            if(item.getNamespacePrefix()=="soapenv"){
	            	throw new Exception(item.getData().toString());
	            }else{
	            	String nodeVal = item.getName();
		            elementMap.put(nodeVal, item);
	            }
        	}
		
		return elementMap;
	    
	}	
	
}
