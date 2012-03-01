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

import java.io.IOException;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.ws.rs.core.UriBuilder;

public class OmGatawayTest {
	private static final Logger log = LoggerFactory.getLogger(OmRestServiceTest.class);
	
    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    
    private OmRestService omRestService;
    private String sessionId;

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

    //@Test
    public void testLogin() throws Exception {
//        String expected = "test";
//        when(mockRequest.getParameter(Mockito.anyString())).thenReturn(expected);
//        assertEquals(expected,mockRequest.getParameter("some string"));
    	
    	
			String sessionURL = "http://localhost:5080/openmeetings/services/UserService/getSession";
			LinkedHashMap<String,Element> elementMap = omRestService.call(sessionURL, null);
			
	    	Element item = elementMap.get("return");
	    	
	    	log.error(elementMap.toString());
	    	
	    	this.setSessionId(item.elementText("session_id"));
	    	
			//log.error(item.elementText("session_id"));
	    	
	    	log.error(item.elementText("session_id"));
			
			LinkedHashMap<String, Element> result = omRestService.call("http://localhost:5080/openmeetings/services/UserService/loginUser?SID="+this.getSessionId()+"&username=schwert&userpass=schwert", null);
			
			//log.error(result.get("return").asXML());
			
			log.error(result.get("return").getStringValue());		
			
			
			if (Integer.valueOf(result.get("return").getStringValue())>0){
		    	//return true; 
			} else {
				//return false;
			}
		
    }
    
    @Test
    public void testAddRoomWithModerationExternalTypeAndTopBarOption() throws XPathExpressionException, IOException, ServletException, SAXException, ParserConfigurationException, DocumentException{
    	
//    	this.testLogin();
//    	
//    	String roomname = "test test";
//    	
//    	String testURL = "http://localhost:5080/openmeetings/services/RoomService/addRoomWithModerationExternalTypeAndTopBarOption?" +
//    			"SID="+this.getSessionId()+
//    					"&name="+roomname+
//						"&roomtypes_id=1"+
//						"&comment="+
//						"&numberOfPartizipants=2"+
//						"&ispublic=true"+
//						"&appointment=false"+
//						"&isDemoRoom=false"+
//						"&demoTime="+
//						"&isModeratedRoom=true"+
//						"&externalRoomType=2"+
//						"&allowUserQuestions="+
//						"&isAudioOnly=false"+
//						"&waitForRecording=false"+
//						"&allowRecording=false"+
//						"&hideTopBar=false";
//    	
//    	log.error("INFO", java.net.URLEncoder.encode(testURL, "UTF-8").toString());
//    	System.out.println("INFO "+ java.net.URLEncoder.encode(testURL, "UTF-8"));
//		///LinkedHashMap<String, Element> result = omRestService.call(java.net.URLDecoder.decode(testURL, "UTF-8").replaceAll("+","%20"), null);
//		
//		LinkedHashMap<String, Element> result = omRestService.call(new URL(testURL).toString().replaceAll(" ","%20"), null);
//
//		
//		log.error(result.get("return").asXML());
//		
//		log.error(result.get("return").getStringValue());
//		
//    	//return null;
    }
    
    public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
    
	private static URI getBaseURI(String url) {
		return UriBuilder.fromUri(
				url).build();
	}
    
}
