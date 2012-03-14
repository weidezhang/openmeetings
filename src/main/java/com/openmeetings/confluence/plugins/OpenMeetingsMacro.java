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
package com.openmeetings.confluence.plugins;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.List;
import java.util.Iterator;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.openmeetings.confluence.plugins.ao.omrooms.Room;
import com.openmeetings.confluence.plugins.ao.omrooms.RoomService;
import com.opensymphony.util.TextUtils;
import com.atlassian.confluence.renderer.PageContext;

/**
 * This very simple macro shows you the very basic use-case of displaying *something* on the Confluence page where it is used.
 * Use this example macro to toy around, and then quickly move on to the next example - this macro doesn't
 * really show you all the fun stuff you can do with Confluence.
 */
public class OpenMeetingsMacro extends BaseMacro
{

    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private final SettingsManager settingsManager;
    private final RoomService roomService;

    public OpenMeetingsMacro(PageManager pageManager, SpaceManager spaceManager, SettingsManager settingsManager, RoomService roomService)
    {
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
        this.roomService = checkNotNull(roomService);
    }

    public boolean isInline()
    {
        return false;
    }

    public boolean hasBody()
    {
        return false;
    }

    public RenderMode getBodyRenderMode()
    {
        return RenderMode.NO_RENDER;
    }

    /**
     * This method returns XHTML to be displayed on the page that uses this macro
     * 
     */
    public String execute(Map params, String body, RenderContext renderContext)
            throws MacroException
    {

    
        StringBuffer result = new StringBuffer();

        
    	if (params.containsKey("room_id"))
        {
			 Page page = pageManager.getPage(((PageContext) renderContext).getEntity().getId());
			 String pageCreatorName = "";
			 List<Room> rooms = null;
			 
			 if(page != null){
				 pageCreatorName =	page.getCreatorName();
				 rooms =  roomService.allNotDeletedByUserName(pageCreatorName);
				  
			 }else{
				 
				 User user = AuthenticatedUserThreadLocal.getUser();
				 pageCreatorName = user.getName();
				 rooms =  roomService.allNotDeletedByUserName(pageCreatorName);
			 }
	       	 
    		//List<Room> rooms =  roomService.allNotDeletedByUserName(pageCreatorName);
    		
    		Integer roomid = Integer.valueOf(params.get("room_id").toString());
    		
    		//Room roomInParam = roomService.getRoom(roomid);
    		
    		String roomLink = "";
    		
    		for (Room room : rooms) {
    			if(room.getRoomId() == (long)roomid){
    				roomLink =  "<a href=" + settingsManager.getGlobalSettings().getBaseUrl()+"/plugins/servlet/openmeetingsrooms?enter=y&roomId=" + roomid + ">Link to Web-Conference</a>";
    				result.append(roomLink);
    			}
    		}
    		
    		if(roomLink == ""){
    			roomLink =  "<span class='error'>Room not avaliable!</span>";             
                result.append(roomLink);
    		}
    		
//    		if(rooms.contains(roomInParam)){
//    			String roomLink =  "<a href=" + settingsManager.getGlobalSettings().getBaseUrl()+"/plugins/servlet/openmeetingsrooms?enter=y&roomId=" + roomid + ">Link to Web-Conference</a>";
//                
//                result.append(roomLink);
//    		
//    		}else{
//
//            	String roomLink =  "Room not avaliable!";
//                
//                result.append(roomLink);
//    		}
//    		
        }
            

        return result.toString();
    }    
    

}