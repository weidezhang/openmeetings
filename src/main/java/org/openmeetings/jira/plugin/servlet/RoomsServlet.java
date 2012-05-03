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


import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.dom4j.DocumentException;
import org.openmeetings.jira.plugin.ao.adminconfiguration.OmPluginSettings;
import org.openmeetings.jira.plugin.ao.omrooms.Room;
import org.openmeetings.jira.plugin.ao.omrooms.RoomService;
import org.openmeetings.jira.plugin.gateway.OmGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;

 
public final class RoomsServlet extends HttpServlet
{
	private static final Logger log = LoggerFactory.getLogger(RoomsServlet.class);
	
    private final RoomService roomService;
    private TemplateRenderer templateRenderer;
    private OmGateway omGateway;
    private UserManager userManager;
    private com.atlassian.jira.user.util.UserManager jiraUserManager;	
	protected final VelocityRequestContextFactory requestContextFactory;
	private OmPluginSettings omPluginSettings;	
	private final AvatarManager avatarManager;
	
	private ArrayList<Exception> errors = new ArrayList<Exception>();;
	
	
    
    private static final String LIST_BROWSER_TEMPLATE = "/templates/omrooms/list.vm";
    private static final String NEW_BROWSER_TEMPLATE = "/templates/omrooms/new.vm";
    private static final String EDIT_BROWSER_TEMPLATE = "/templates/omrooms/edit.vm";
    private static final String ENTER_BROWSER_TEMPLATE = "/templates/omrooms/enter.vm";
 
    public RoomsServlet(OmPluginSettings omPluginSettings, VelocityRequestContextFactory requestContextFactory, AvatarManager avatarManager, RoomService roomService, TemplateRenderer templateRenderer, OmGateway omGateway, com.atlassian.jira.user.util.UserManager jiraUserManager, UserManager userManager)
    {
        this.roomService = checkNotNull(roomService);
        this.templateRenderer = templateRenderer;
        this.omGateway = omGateway;
        this.jiraUserManager = jiraUserManager;
        this.userManager = userManager;
        this.avatarManager = avatarManager;
        this.requestContextFactory = requestContextFactory;
        this.omPluginSettings = omPluginSettings;     
        
    }
 
    public String getCanonicalBaseUrl() {
    	   return this.requestContextFactory.getJiraVelocityRequestContext().getCanonicalBaseUrl();
    }

	private User getCurrentUser(HttpServletRequest req) {
	    // To get the current user, we first get the username from the session.
	    // Then we pass that over to the jiraUserManager in order to get an
	    // actual User object.	 
	    return jiraUserManager.getUserObject(userManager.getRemoteUsername(req));
	}
	
		
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	User currentUser = getCurrentUser(req);    	
    	//ArrayList<Exception> errors = new ArrayList<Exception>();
    	String roomURL = "";    	
    	
		//User currentUser2 = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
    	    	        
        if ("y".equals(req.getParameter("new"))) {
        	// Renders new.vm template if the "new" parameter is passed            
            // Create an empty context map to pass into the render method
            Map<String, Object> context = Maps.newHashMap();
            // Make sure to set the contentType otherwise bad things happen
            res.setContentType("text/html;charset=utf-8");
            // Render the velocity template (new.vm). Since the new.vm template 
            // doesn't need to render any in dynamic content, we just pass it an empty context
           templateRenderer.render(NEW_BROWSER_TEMPLATE, context, res.getWriter());
        	
        }else if("y".equals(req.getParameter("edit"))){
        	// Renders edit.vm template if the "edit" parameter is passed                       
        	Integer id = Integer.valueOf(req.getParameter("key"));
        	Room room = roomService.getRoom(id);
        	
            Map<String, Object> context = Maps.newHashMap();
            context.put("room", room);
            context.put("errors", this.errors);
            res.setContentType("text/html;charset=utf-8");
            // Render the template with the issue inside the context
            templateRenderer.render(EDIT_BROWSER_TEMPLATE, context, res.getWriter());        	
        }else if("y".equals(req.getParameter("delete"))){
        	Integer id = Integer.valueOf(req.getParameter("key"));
    		roomService.delete(id);
    		res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
        }else if("y".equals(req.getParameter("enter"))){        	
        	try {
				if(omGateway.loginUser()){
					
					String username = "";
					String firsname = "";
					String userId = "";
					String email = "";
					int becomeModeratorAsInt = 1;
					int showAudioVideoTestAsInt = 1;
					
					String url = (String)omPluginSettings.getSomeInfo("url"); 
		        	String port = (String)omPluginSettings.getSomeInfo("port");
		        	String externalUserType = (String)omPluginSettings.getSomeInfo("key");  
					
		        	if(currentUser == null){
		        		firsname = "anonymous";
						email = "";
						userId = new Long(new Date().getTime()).toString();
						username = "anonymous";
						becomeModeratorAsInt = 0;
						showAudioVideoTestAsInt = 1;  
		        	}else{
		        		firsname = currentUser.getDisplayName();
						email = currentUser.getEmailAddress();
						userId = currentUser.getName();//new Date().getTime();
						username = currentUser.getName();
						becomeModeratorAsInt = 1;
						showAudioVideoTestAsInt = 1;
		        	}					
					 String avatarId = this.avatarManager.getDefaultAvatarId(Avatar.Type.USER).toString();					 
					 //URI avatarUrl = avatarService.getAvatarURL(currentUser, avatarId, Avatar.Size.SMALL);
					 //String profilePictureUrl = avatarUrl.toString();
					 String profilePictureUrl = this.getCanonicalBaseUrl() + "/secure/projectavatar?avatarId=" + avatarId + "&size=small";
					 
					Long roomId = Long.valueOf(req.getParameter("roomId"));
										
					String roomHash = omGateway.setUserObjectAndGenerateRoomHash(username, firsname, "", profilePictureUrl, 
																				email, userId, externalUserType, 	roomId, 
																				becomeModeratorAsInt,
																				showAudioVideoTestAsInt);
					
					
					if(!roomHash.isEmpty()){
				
						roomURL = "http://"+url+":"+port+
								"/openmeetings/?"+
								"scopeRoomId=" + roomId +
								"&secureHash=" +roomHash+								
								"&language=1"+
								"&lzproxied=solo";					
													
					}
					
				}else{
					
					this.errors.add(new Exception("Could not login User to OpenMeetings, check your OpenMeetings plugin configuration"));
				}
				
				
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			}
     
	        Map<String, Object> context = Maps.newHashMap();	        
	       
			context.put("roomURL", roomURL);
			context.put("errors", this.errors);
	        res.setContentType("text/html;charset=utf-8");	        
	        // Pass in the list of rooms as the context
	        templateRenderer.render(ENTER_BROWSER_TEMPLATE, context, res.getWriter());
	        this.errors.clear();
        }else {
            // Render the list of issues (list.vm) if no params are passed in
            //List<Room> rooms =  roomService.allNotDeleted();
            List<Room> rooms =  roomService.allNotDeletedByUserName(currentUser.getName());
            Map<String, Object> context = Maps.newHashMap();
            context.put("errors", this.errors);
            context.put("rooms", rooms);
            res.setContentType("text/html;charset=utf-8");
            // Pass in the list of rooms as the context
            templateRenderer.render(LIST_BROWSER_TEMPLATE, context, res.getWriter());
            this.errors.clear();
        } 	
    	
    }
 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	User currentUser = getCurrentUser(req);  
    	//Second variant to get current user object. 
    	//User currentUser2 = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();    	
    	//User user2 = (User) ComponentManager.getInstance().getJiraAuthenticationContext().getUser();
    	
    	if ("y".equals(req.getParameter("edit"))) {
    		
    		Boolean isAllowedRecording = Boolean.valueOf(req.getParameter("isAllowedRecording"));
        	Boolean isAudioOnly = Boolean.valueOf(req.getParameter("isAudioOnly"));
        	Boolean isModeratedRoom = Boolean.valueOf(req.getParameter("isModeratedRoom"));
        	String roomName = req.getParameter("roomname");
        	Long numberOfParticipent = Long.valueOf(req.getParameter("numberOfParticipent"));  
        	Long roomType = Long.valueOf(req.getParameter("roomType")); 
        	Integer id = Integer.valueOf(req.getParameter("key"));
        	//Long roomId = Long.valueOf(req.getParameter("roomId"));
        	
        	//Get RoomId for updating room from DB
        	Room room = roomService.getRoom(id);
        	Long roomId = room.getRoomId();
        	
        	try {
				if(omGateway.loginUser()){
					
					roomId = omGateway.updateRoomWithModerationAndQuestions(
							isAllowedRecording, 
							isAudioOnly, 
							isModeratedRoom, 
							roomName, 
							numberOfParticipent, 
							roomType, 
							roomId);
					
		        	roomService.update(id, isAllowedRecording, isAudioOnly, isModeratedRoom, roomName, numberOfParticipent, roomType);

				}else{
					this.errors.add(new Exception("Could not login User to OpenMeetings, check your OpenMeetings plugin configuration"));
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			}
        	
            
        	res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
    		
    	}else if("y".equals(req.getParameter("delete"))){    		
    		Integer id = Integer.valueOf(req.getParameter("key"));
    		roomService.delete(id);
    		res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
    	}else{
    		
        	Boolean isAllowedRecording = Boolean.valueOf(req.getParameter("isAllowedRecording"));
        	Boolean isAudioOnly = Boolean.valueOf(req.getParameter("isAudioOnly"));
        	Boolean isModeratedRoom = Boolean.valueOf(req.getParameter("isModeratedRoom"));
        	String roomName = req.getParameter("roomname");
        	Long numberOfParticipent = Long.valueOf(req.getParameter("numberOfParticipent"));  
        	Long roomType = Long.valueOf(req.getParameter("roomType")); 
        	String externalRoomType = (String)omPluginSettings.getSomeInfo("key");
        	Long roomId = 0L;
        	
        	try {
				if(omGateway.loginUser()){
					
					roomId = omGateway.addRoomWithModerationExternalTypeAndTopBarOption(
							isAllowedRecording,
							isAudioOnly,
							isModeratedRoom,
							roomName,
							numberOfParticipent,
							roomType,
							externalRoomType
							);
					
		        	roomService.add(isAllowedRecording, isAudioOnly, isModeratedRoom, roomName, numberOfParticipent, roomType, roomId, currentUser.getName());

				}else{
					this.errors.add(new Exception("Could not login User to OpenMeetings, check your OpenMeetings plugin configuration"));
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				this.errors.add(e);
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.errors.add(e);
			}
        	
            //roomService.add(description, true, true, true, "name", 4L, 1L);
        	
            res.sendRedirect(req.getContextPath() + "/plugins/servlet/openmeetingsrooms");
    		
    	}   	

    }
}