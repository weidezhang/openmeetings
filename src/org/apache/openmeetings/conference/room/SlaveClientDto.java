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
package org.apache.openmeetings.conference.room;

import org.apache.openmeetings.utils.math.CalendarPatterns;

/**
 * 
 * Transfer object to send the master in a cluster the user load of a single
 * user session
 * 
 * @author sebawagner
 * 
 */
public class SlaveClientDto {

	private String streamid;
	private String publicSID;
	private String username;
	private String connectedSince;
	private String scope;
	private String firstName;
	private String lastName;
	private Long userId;
	private Long roomId;
	private boolean isAVClient = false;
	
	public SlaveClientDto(RoomClient roomClient) {
		this.streamid = roomClient.getStreamid();
		this.publicSID = roomClient.getPublicSID();
		this.firstName = roomClient.getFirstname();
		this.lastName = roomClient.getLastname();
		this.userId = roomClient.getUser_id();
		this.roomId = roomClient.getRoom_id();
		this.isAVClient = roomClient.getIsAVClient();
		this.username = roomClient.getUsername();
		this.scope = roomClient.getScope();
		if (roomClient.getConnectedSince() != null) {
			this.connectedSince = CalendarPatterns.getDateWithTimeByMiliSeconds(roomClient.getConnectedSince());
		}
	}

	public SlaveClientDto(String streamid, String publicSID, Long roomId2,
			Long userId2, String firstName, String lastName, boolean isAVClient,
			String scope, String username, String connectedSince) {
		this.streamid = streamid;
		this.publicSID = publicSID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userId = userId2;
		this.roomId = roomId2;
		this.isAVClient = isAVClient;
		this.scope = scope;
		this.username = username;
		this.connectedSince = connectedSince;
	}

	public String getStreamid() {
		return streamid;
	}

	public void setStreamid(String streamid) {
		this.streamid = streamid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	public boolean isAVClient() {
		return isAVClient;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConnectedSince() {
		return connectedSince;
	}

	public void setConnectedSince(String connectedSince) {
		this.connectedSince = connectedSince;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setAVClient(boolean isAVClient) {
		this.isAVClient = isAVClient;
	}

	@Override
	public String toString() {
		return " streamid: "+streamid+" publicSID: "+publicSID+" roomId: "+roomId+" userId: "+
				userId+" firstName: "+firstName+" lastName: "+lastName+" isAVClient: "+isAVClient+" scope: "+
				scope+" username: "+username+" connectedSince: "+connectedSince;
	}

}