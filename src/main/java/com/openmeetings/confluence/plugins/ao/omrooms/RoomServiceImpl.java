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
package com.openmeetings.confluence.plugins.ao.omrooms;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.openmeetings.confluence.plugins.servlet.RoomsServlet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.ao.Query;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public final class RoomServiceImpl implements RoomService
{
    private final ActiveObjects ao;
    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);
    
    public RoomServiceImpl(ActiveObjects ao)
    {
        this.ao = checkNotNull(ao);
    }

    @Override
    public Room add(boolean isAllowedRecording, boolean isAudioOnly, boolean isModeratedRoom,
    		String name, Long numberOfParticipent, Long roomType, Long roomId, String createdByUserName)
    {
        final Room room = ao.create(Room.class);
      
		room.setIsAllowedRecording(isAllowedRecording);
        room.setIsAudioOnly(isAudioOnly);
        room.setIsModeratedRoom(isModeratedRoom);
        room.setName(name);
        room.setNumberOfParticipent(numberOfParticipent);
        room.setRoomType(roomType);
        room.setRoomId(roomId);
        room.setCreatedByUserName(createdByUserName);
        room.setIsDeleted(false);
        room.save();
        return room;
    }

    @Override
    public List<Room> all()
    {
        return newArrayList(ao.find(Room.class));
    }
    
    @Override
    public List<Room> allNotDeleted()
    {
        //return newArrayList(ao.find(Room.class, Query.select().where("IS_DELETED LIKE ?", false)));
        return newArrayList(ao.find(Room.class, Query.select().where("isdeleted LIKE ?", false)));
    }
    
    @Override
	public List<Room> allNotDeletedByUserName(String userName) {		
	 
	 	return newArrayList(ao.find(Room.class, Query.select().
				 where("IS_DELETED = ? AND CREATED_BY_USER_NAME = ?",false, userName.toString()).limit(1000)));	
		
		 //return newArrayList(ao.find(Room.class,"IS_DELETED LIKE ? AND CREATED_BY_USER_NAME = ?", false, userName));	
		 		
	}

	@Override
	public Room update(Integer id, boolean isAllowedRecording,
			boolean isAudioOnly, boolean isModeratedRoom, String name,
			Long numberOfParticipent, Long roomType) {
		
		final Room room = ao.get(Room.class, id);
	      
		room.setIsAllowedRecording(isAllowedRecording);
        room.setIsAudioOnly(isAudioOnly);
        room.setIsModeratedRoom(isModeratedRoom);
        room.setName(name);
        room.setNumberOfParticipent(numberOfParticipent);
        room.setRoomType(roomType);
        room.save();
        return room;
		
	}

	@Override
	public Room delete(Integer id) {
		
		final Room room = ao.get(Room.class, id);
		
		room.setIsDeleted(true);        
        room.save();
        
        return room;
	}

	@Override
	public Room getRoom(Integer id) {
		
		final Room room = ao.get(Room.class, id);
		return room;
	}	
}
