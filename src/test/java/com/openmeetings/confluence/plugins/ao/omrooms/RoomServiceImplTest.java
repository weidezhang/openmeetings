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

import java.util.List;

import junit.framework.TestCase;

import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
public final class RoomServiceImplTest extends TestCase
{
	private EntityManager entityManager;
	 
    private RoomServiceImpl roomServiceImpl;
    
    private ActiveObjects ao;    
    
     
    @Before
    public void setUp() throws Exception
    {
    	assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        roomServiceImpl = new RoomServiceImpl(ao);    	

    }
 
    @Test
    public void testAdd() throws Exception
    {
        final String roomname = "This is a room";
        final Boolean isAllowedRecording = false;
        final Boolean isAudioOnly = false;
        final Boolean isModeratedRoom = false;        
        final Long numberOfParticipent = 10L;
        final Long roomId = 22L;
        final Long roomType = 2L;
        final String name = "This is a room";
        final String createdByUserName = "admin";
 
        ao.migrate(Room.class);    
        
 
        assertEquals(0, ao.find(Room.class).length);
 
        final Room add = roomServiceImpl.add(isAllowedRecording, 
        		isAudioOnly, isModeratedRoom, name, numberOfParticipent, roomType, roomId, createdByUserName);
        assertFalse(add.getID() == 0);
 
        ao.flushAll(); // (3) clear all caches
 
        final Room[] todos = ao.find(Room.class);
        assertEquals(1, todos.length);
        assertEquals(roomname, todos[0].getName());
        assertEquals(false, todos[0].getIsAllowedRecording());
        assertEquals(false, todos[0].getIsAudioOnly());
        assertEquals(false, todos[0].getIsDeleted());
        assertEquals(false, todos[0].getIsModeratedRoom());
        assertEquals(roomId, todos[0].getRoomId());
        assertEquals(roomType, todos[0].getRoomType());
    }
 
    @SuppressWarnings("unchecked")
	@Test
    public void testAll() throws Exception
    {
    	final String roomname = "This is a room";
        final Boolean isAllowedRecording = false;
        final Boolean isAudioOnly = false;
        final Boolean isModeratedRoom = false;        
        final Long numberOfParticipent = 10L;
        final Long roomId = 22L;
        final Long roomType = 2L;
        final String name = "This is a room";
        final String createdByUserName = "admin";
    	
    	ao.migrate(Room.class); // (2)
 
        assertTrue(roomServiceImpl.all().isEmpty());
 
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
        
        final Room room2 = ao.create(Room.class);
        room2.setIsAllowedRecording(isAllowedRecording);
        room2.setIsAudioOnly(isAudioOnly);
        room2.setIsModeratedRoom(isModeratedRoom);
        room2.setName(name);
        room2.setNumberOfParticipent(numberOfParticipent);
        room2.setRoomType(roomType);
        room2.setRoomId(roomId);
        room2.setCreatedByUserName(createdByUserName);
        room2.setIsDeleted(false);
        room2.save(); 
 
        //ao.flushAll(); // (3) clear all caches
 
       // final List<Room> all = roomServiceImpl.all();
        //final List<Room> all = roomServiceImpl.allNotDeletedByUserName("admin");
        //final List<Room> all = roomServiceImpl.allNotDeleted();
        //final List<Room> all = newArrayList(ao.find(Room.class, Query.select().where("isdeleted LIKE ?", false)));
        //final List<Room> all = newArrayList(ao.find(Room.class, Query.select().where("isdeleted = false")));
        
        final List<Room> all = newArrayList(ao.find(Room.class, Query.select().
				 where("isdeleted = ? AND createdbyusername = ?",false, "admin").limit(1000)));
        
        
        System.out.println(all.get(0).getID());
        System.out.println(all.get(0).getIsDeleted());
                
        System.out.println(all.get(1).getID());
        System.out.println(all.get(1).getIsDeleted());
                
        assertEquals(2, all.size());
        assertEquals(room.getID(), all.get(0).getID());
        assertEquals(false, all.get(0).getIsDeleted());
    }

}
