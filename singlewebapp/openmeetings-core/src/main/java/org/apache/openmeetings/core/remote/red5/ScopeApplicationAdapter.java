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
package org.apache.openmeetings.core.remote.red5;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.openmeetings.core.data.conference.RoomManager;
import org.apache.openmeetings.core.data.whiteboard.WhiteboardManager;
import org.apache.openmeetings.core.remote.FLVRecorderService;
import org.apache.openmeetings.core.remote.WhiteBoardService;
import org.apache.openmeetings.core.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.MeetingMemberDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.room.BrowserStatus;
import org.apache.openmeetings.db.dto.room.RoomStatus;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.openmeetings.util.Version;
import org.apache.openmeetings.util.process.UploadCompleteMessage;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IBasicScope;
import org.red5.server.api.scope.IBroadcastScope;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.scope.ScopeType;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.stream.IBroadcastStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ScopeApplicationAdapter extends ApplicationAdapter implements IPendingServiceCallback {
	private static final Logger log = Red5LoggerFactory.getLogger(ScopeApplicationAdapter.class, webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private WhiteBoardService whiteBoardService;
	@Autowired
	private WhiteboardManager whiteboardManagement;
	@Autowired
	private FLVRecorderService flvRecorderService;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private ConferenceLogDao conferenceLogDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private FlvRecordingDao recordingDao;
	@Autowired
	private MeetingMemberDao meetingMemberDao;
	@Autowired
	private ServerDao serverDao;

	private static AtomicLong broadCastCounter = new AtomicLong(0);

	public void resultReceived(IPendingServiceCall arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean appStart(IScope scope) {
		try {
			OmFileHelper.setOmHome(scope.getResource("/").getFile());
			LabelDao.initLanguageMap();

			log.debug("webAppPath : " + OmFileHelper.getOmHome());

			// Only load this Class one time Initially this value might by empty, because the DB is empty yet
			getCryptKey();

			// init your handler here
			for (String scopeName : scope.getScopeNames()) {
				log.debug("scopeName :: " + scopeName);
			}
			
			InitializationContainer.initComplete = true;
			Version.logOMStarted();
			recordingDao.resetProcessingStatus(); //we are starting so all processing recordings are now errors
		} catch (Exception err) {
			log.error("[appStart]", err);
		}
		return true;
	}

	@Override
	public boolean roomConnect(IConnection conn, Object[] params) {
		log.debug("roomConnect : ");

		IServiceCapableConnection service = (IServiceCapableConnection) conn;
		String streamId = conn.getClient().getId();
		
		boolean isAVClient = params.length == 1 ? Boolean.valueOf("" + params[0]) : false;

		log.debug("### Client connected to OpenMeetings, register Client StreamId: " + streamId + " scope "
				+ conn.getScope().getName() + " isAVClient " + isAVClient);

		// Set StreamId in Client
		service.invoke("setId", new Object[] { streamId }, this);

		Map<String, Object> map = conn.getConnectParams();
		String swfURL = map.containsKey("swfUrl") ? (String)map.get("swfUrl") : "";

		//TODO add similar code for other connections
		if (map.containsKey("screenClient")) {
			String parentSid = (String)map.get("parentSid");
			Client parentClient = sessionManager.getClientByPublicSID(parentSid, false, null);
			if (parentClient == null) {
				rejectClient();
			}
		}
		Client rcm = sessionManager.addClientListItem(conn.getClient().getId(),
				conn.getScope().getName(), conn.getRemotePort(),
				conn.getRemoteAddress(), swfURL, isAVClient, null);
		
		SessionVariablesUtil.initClient(conn.getClient(), isAVClient, rcm.getPublicSID());
		//TODO add similar code for other connections, merge with above block
		if (map.containsKey("screenClient")) {
			//TODO add check for room rights
			String parentSid = (String)map.get("parentSid");
			rcm.setRoom_id(Long.parseLong(conn.getScope().getName()));
			rcm.setScreenClient(true);
			SessionVariablesUtil.setIsScreenClient(conn.getClient());
			
			rcm.setUser_id(((Integer)map.get("userId")).longValue());
			SessionVariablesUtil.setUserId(conn.getClient(), rcm.getUser_id());

			rcm.setStreamPublishName(parentSid);
			User u = usersDao.get(rcm.getUser_id() < 0 ? -rcm.getUser_id() : rcm.getUser_id());
			rcm.setUsername(u.getLogin());
			rcm.setFirstname(u.getFirstname());
			rcm.setLastname(u.getLastname());
			log.debug("publishName :: " + rcm.getStreamPublishName());
			sessionManager.updateClientByStreamId(streamId, rcm, false, null);
		}

		// Log the User
		conferenceLogDao.addConferenceLog("ClientConnect",
				rcm.getUser_id(), streamId, null, rcm.getUserip(),
				rcm.getScope(), rcm.getExternalUserId(),
				rcm.getExternalUserType(), rcm.getEmail(),
				rcm.getFirstname(), rcm.getLastname());
		return true;
	}

	public Map<String, String> screenSharerAction(Map<String, Object> map) {
		try {
			log.debug("-----------  screenSharerAction ENTER");
			IConnection current = Red5.getConnectionLocal();

			Client control = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Client client = sessionManager.getClientByPublicSID(control.getStreamPublishName(), false, null);

			Map<String, String> returnMap = new HashMap<String, String>();

			if (client != null) {
				boolean changed = false;
				if (Boolean.valueOf("" + map.get("stopStreaming")) && client.isStartStreaming()) {
					changed = true;
					client.setStartStreaming(false);
					//Send message to all users
					sendMessageToCurrentScope("stopScreenSharingMessage", client, false);
					
					returnMap.put("result", "stopSharingOnly");
				}
				if (Boolean.valueOf("" + map.get("stopRecording")) && client.getIsRecording()) {
					changed = true;
					client.setStartRecording(false);
					client.setIsRecording(false);
					
					returnMap.put("result", "stopRecordingOnly");
					//Send message to all users
					sendMessageToCurrentScope("stopRecordingMessage", client, false);

					flvRecorderService.stopRecordAndSave(current.getScope(), client, null);
				}
				if (Boolean.valueOf("" + map.get("stopPublishing")) && client.isScreenPublishStarted()) {
					changed = true;
					client.setScreenPublishStarted(false);
					returnMap.put("result", "stopPublishingOnly");
					
					//Send message to all users
					sendMessageToCurrentScope("stopPublishingMessage", client, false);
				}
				
				if (changed) {
					sessionManager.updateClientByStreamId(client.getStreamid(), client, false, null);
					
					if (!client.isStartStreaming() && !client.isStartRecording() && !client.isStreamPublishStarted()) {
						returnMap.put("result", "stopAll");
					}
				}
			}
			log.debug("-----------  screenSharerAction, return: " + returnMap);
			return returnMap;
		} catch (Exception err) {
			log.error("[screenSharerAction]", err);
		}
		return null;
	}

	public List<Client> checkScreenSharing() {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("checkScreenSharing -2- " + streamid);

			List<Client> screenSharerList = new LinkedList<Client>();

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			for (Client rcl : sessionManager.getClientListByRoomAll(currentClient.getRoom_id())) {
				if (rcl.isStartStreaming()) {
					screenSharerList.add(rcl);
				}
			}

			return screenSharerList;

		} catch (Exception err) {
			log.error("[checkScreenSharing]", err);
		}
		return null;
	}

	/**
	 * 
	 * @param map
	 * @return returns key,value Map with multiple return values or null in case of exception
	 * 
	 */
	public Map<String, Object> setConnectionAsSharingClient(Map<String, Object> map) {
		try {
			log.debug("-----------  setConnectionAsSharingClient");
			IConnection current = Red5.getConnectionLocal();

			Client control = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Client client = sessionManager.getClientByPublicSID(control.getStreamPublishName(), false, null);

			if (client != null) {
				boolean startRecording = Boolean.valueOf("" + map.get("startRecording"));
				boolean startStreaming = Boolean.valueOf("" + map.get("startStreaming"));
				boolean startPublishing = Boolean.valueOf("" + map.get("startPublishing")) && (0 == sessionManager.getPublishingCount(client.getRoom_id()));

				boolean alreadyStreaming = client.isStartStreaming();
				if (startStreaming) {
					client.setStartStreaming(true);
				}
				boolean alreadyRecording = client.isStartRecording();
				if (startRecording) {
					client.setStartRecording(true);
				}
				if (startPublishing) {
					client.setStreamPublishStarted(true);
				}

				client.setVX(Integer.parseInt(map.get("screenX").toString()));
				client.setVY(Integer.parseInt(map.get("screenY").toString()));
				client.setVWidth(Integer.parseInt(map.get("screenWidth").toString()));
				client.setVHeight(Integer.parseInt(map.get("screenHeight").toString()));
				sessionManager.updateClientByStreamId(current.getClient().getId(), client, false, null);

				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("alreadyPublished", false);

				// if is already started screen sharing, then there is no need
				// to start it again
				if (client.isScreenPublishStarted()) {
					returnMap.put("alreadyPublished", true);
				}

				log.debug(String.format("screen x,y,width,height %s,%s,%s,%s", client.getVX(), client.getVY(), client.getVWidth(), client.getVHeight()));

				if (startStreaming) {
					if (!alreadyStreaming) {
						returnMap.put("modus", "startStreaming");
	
						log.debug("start streamPublishStart Is Screen Sharing ");
						
						//Send message to all users
						sendMessageToCurrentScope("newScreenSharing", client, false);
					} else {
						log.warn("Streaming is already started for the client id=" + client.getId() + ". Second request is ignored.");
					}
				}
				if (startRecording) {
					if (!alreadyRecording) {
						returnMap.put("modus", "startRecording");
	
						String recordingName = "Recording " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());
	
						flvRecorderService.recordMeetingStream(current, client, recordingName, "", false);
					} else {
						log.warn("Recording is already started for the client id=" + client.getId() + ". Second request is ignored.");
					}
				}
				if (startPublishing) {
					sendMessageToCurrentScope("startedPublishing", new Object[]{client, "rtmp://" + map.get("publishingHost") + ":1935/"
							+ map.get("publishingApp") + "/" + map.get("publishingId")}, false, true);
					returnMap.put("modus", "startPublishing");
				}
				return returnMap;

			} else {
				throw new Exception("Could not find Screen Sharing Client " + current.getClient().getId());
			}

		} catch (Exception err) {
			log.error("[setConnectionAsSharingClient]", err);
		}
		return null;
	}

    public synchronized List<Integer> listRoomBroadcast() {
    	HashSet<Integer> broadcastList = new HashSet<Integer>();
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        for (IConnection conn : current.getScope().getClientConnections()) {
            if (conn != null) {
                Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
                if (rcl == null) {
                    // continue;
                } else if (rcl.isScreenClient()) {
                    // continue;
                } else {
                    if (!streamid.equals(rcl.getStreamid())) {
                        // It is not needed to send back
                        // that event to the actuall
                        // Moderator
                        // as it will be already triggered
                        // in the result of this Function
                        // in the Client
                    	int id = (int)rcl.getBroadCastID();
                    	if (!broadcastList.contains(id)) {
                    		broadcastList.add(id);
                    	}
                    }
                }
            }
        }
        return new ArrayList<Integer>(broadcastList);
    }


	/**
	 * this function is invoked directly after initial connecting
	 * 
	 * @return publicSID of current client
	 */
	public String getPublicSID() {
		log.debug("-----------  getPublicSID");
		IConnection current = Red5.getConnectionLocal();
		Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		sessionManager.updateClientByStreamId(current.getClient().getId(), currentClient, false, null);
		return currentClient.getPublicSID();
	}

	/**
	 * this function is invoked after a reconnect
	 * 
	 * @param newPublicSID
	 */
	public Boolean overwritePublicSID(String newPublicSID) {
		try {
			log.debug("-----------  overwritePublicSID");
			IConnection current = Red5.getConnectionLocal();
			IClient c = current.getClient();
			Client currentClient = sessionManager.getClientByStreamId(c.getId(), null);
			if (currentClient == null) {
				return false;
			}
			SessionVariablesUtil.initClient(c, SessionVariablesUtil.isAVClient(c), newPublicSID);
			currentClient.setPublicSID(newPublicSID);
			sessionManager.updateClientByStreamId(c.getId(), currentClient, false, null);
			return true;
		} catch (Exception err) {
			log.error("[overwritePublicSID]", err);
		}
		return null;
	}

	/**
	 * Logic must be before roomDisconnect cause otherwise you cannot throw a
	 * message to each one
	 * 
	 */
	@Override
	public void roomLeave(IClient client, IScope room) {
		try {
			log.debug(String.format("roomLeave %s %s %s %s", client.getId(), room.getClients().size(), room.getContextPath(), room.getName()));

			Client currentClient = sessionManager.getClientByStreamId(client.getId(), null);

			// The Room Client can be null if the Client left the room by using
			// logicalRoomLeave
			if (currentClient != null) {
				log.debug("currentClient IS NOT NULL");
				roomLeaveByScope(currentClient, room, true);
			}
		} catch (Exception err) {
			log.error("[roomLeave]", err);
		}
	}

	/**
	 * this means a user has left a room but only logically, he didn't leave the
	 * app he just left the room
	 * 
	 * FIXME: Is this really needed anymore if you re-connect to another scope?
	 * 
	 * Exit Room by Application
	 * 
	 */
	public void logicalRoomLeave() {
		log.debug("logicalRoomLeave ");
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug(streamid + " is leaving");

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			roomLeaveByScope(currentClient, current.getScope(), true);
		} catch (Exception err) {
			log.error("[logicalRoomLeave]", err);
		}
	}

	/**
	 * Removes the Client from the List, stops recording, adds the Room-Leave
	 * event to running recordings, clear Polls and removes Client from any list
	 * 
	 * This function is kind of private/protected as the client won't be able 
	 * to call it with proper values.
	 * 
	 * @param currentClient
	 * @param currentScope
	 */
	public void roomLeaveByScope(Client currentClient, IScope currentScope, boolean removeUserFromSessionList) {
		try {
			log.debug("currentClient " + currentClient);
			Long room_id = currentClient.getRoom_id();

			// Log the User
			conferenceLogDao.addConferenceLog("roomLeave",
					currentClient.getUser_id(), currentClient.getStreamid(),
					room_id, currentClient.getUserip(), "",
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getEmail(), currentClient.getFirstname(),
					currentClient.getLastname());

			// Remove User from Sync List's
			if (room_id != null) {
				whiteBoardService.removeUserFromAllLists(currentScope, currentClient);
			}

			log.debug("removing USername " + currentClient.getUsername() + " "
					+ currentClient.getConnectedSince() + " streamid: "
					+ currentClient.getStreamid());

			// stop and save any recordings
			if (currentClient.getIsRecording()) {
				log.debug("*** roomLeave Current Client is Recording - stop that");
				if (currentClient.getInterviewPodId() != null) {
					//interview, TODO need better check
					_stopInterviewRecording(currentClient, currentScope);
				} else {
					flvRecorderService.stopRecordAndSave(currentScope, currentClient, null);

					// set to true and overwrite the default one cause otherwise no
					// notification is send
					currentClient.setIsRecording(true);
				}
			}

			// Notify all clients of the same currentScope (room) with domain
			// and room except the current disconnected cause it could throw an exception
			log.debug("currentScope " + currentScope);

			if (currentScope != null && currentScope.getClientConnections() != null) {
				// Notify Users of the current Scope
				for (IConnection cons : currentScope.getClientConnections()) {
					if (cons != null && cons instanceof IServiceCapableConnection) {
						log.debug("sending roomDisconnect to {}  client id {}", cons, cons.getClient().getId());

						Client rcl = sessionManager.getClientByStreamId(cons.getClient().getId(), null);

						// Check if the Client does still exist on the list
						if (rcl == null) {
							log.debug("For this StreamId: " + cons.getClient().getId() + " There is no Client in the List anymore");
							continue;
						}
						
						//Do not send back to sender, but actually all other clients should receive this message swagner 01.10.2009
						if (!currentClient.getStreamid().equals(rcl.getStreamid())) {
							// add Notification if another user isrecording
							log.debug("###########[roomLeave]");
							if (rcl.getIsRecording()) {
								log.debug("*** roomLeave Any Client is Recording - stop that");
								flvRecorderService.stopRecordingShowForClient(cons, currentClient);
							}
							
							//If the user was a avclient, we do not broadcast a message about that to everybody
							if (currentClient.isAvClient()) {
								continue;
							}
							
							boolean isScreen = rcl.isScreenClient();
							if (isScreen && currentClient.getPublicSID().equals(rcl.getStreamPublishName())) {
								//going to terminate screen sharing started by this client
								((IServiceCapableConnection) cons).invoke("stopStream", new Object[] { },this);
								continue;
							} else if (isScreen) {
								// screen sharing clients do not receive events
								continue;
							} else if (rcl.isAvClient()) {
								// AVClients or potential AVClients do not receive events
								continue;
							}
							
							// Send to all connected users
							((IServiceCapableConnection) cons).invoke("roomDisconnect", new Object[] { currentClient },this);
							log.debug("sending roomDisconnect to " + cons);
						}
					}
				}
			}

			if (removeUserFromSessionList) {
				sessionManager.removeClient(currentClient.getStreamid(), null);
			}
		} catch (Exception err) {
			log.error("[roomLeaveByScope]", err);
		}
	}

	/**
	 * This method handles the Event after a stream has been added all connected
	 * Clients in the same room will get a notification
	 * 
	 */
	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamPublishStart(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamPublishStart(IBroadcastStream stream) {
		try {
			log.debug("-----------  streamPublishStart");
			IConnection current = Red5.getConnectionLocal();
			final String streamid = current.getClient().getId();
			final Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			//We make a second object the has the reference to the object 
			//that we will use to send to all participents
			Client clientObjectSendToSync = currentClient;
			
			// Notify all the clients that the stream had been started
			log.debug("start streamPublishStart broadcast start: " + stream.getPublishedName() + " CONN " + current);

			// In case its a screen sharing we start a new Video for that
			if (currentClient.isScreenClient()) {
				currentClient.setScreenPublishStarted(true);
				sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
			}
			//If its an audio/video client then send the session object with the full data to everybody
			else if (currentClient.isAvClient()) {
				clientObjectSendToSync = sessionManager.getClientByPublicSID(currentClient.getPublicSID(), false, null);
			}
			
			log.debug("newStream SEND: " + currentClient);

			// Notify all users of the same Scope
			// We need to iterate through the streams to catch if anybody is recording
			new MessageSender(current, "newStream", clientObjectSendToSync) {
				@Override
				public boolean filter(IConnection conn) {
					Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
					
					if (rcl == null) {
						log.debug("RCL IS NULL newStream SEND");
						return true;
					}
					
					log.debug("check send to "+rcl);
					
					if (rcl.getPublicSID() == "") {
						log.debug("publicSID IS NULL newStream SEND");
						return true;
					}
					if (rcl.getIsRecording()) {
						log.debug("RCL getIsRecording newStream SEND");
						flvRecorderService.addRecordingByStreamId(current, streamid, currentClient, rcl.getFlvRecordingId());
					}
					if (rcl.isAvClient()) {
						log.debug("RCL getIsAVClient newStream SEND");
						return true;
					}
					if (rcl.isScreenClient()) {
						log.debug("RCL getIsScreenClient newStream SEND");
						return true;
					}
					
					if (rcl.getPublicSID().equals(currentClient.getPublicSID())) {
						log.debug("RCL publicSID is equal newStream SEND");
						return true;
					}
					log.debug("RCL SEND is equal newStream SEND "+rcl.getPublicSID()+" || "+rcl.getUserport());
					return false;
				}
			}.start();
		} catch (Exception err) {
			log.error("[streamPublishStart]", err);
		}
	}

	public IBroadcastScope getBroadcastScope(IScope scope, String name) {
		IBasicScope basicScope = scope.getBasicScope(ScopeType.BROADCAST, name);
		if (!(basicScope instanceof IBroadcastScope)) {
			return null;
		} else {
			return (IBroadcastScope) basicScope;
		}
	}

	/**
	 * This method handles the Event after a stream has been removed all
	 * connected Clients in the same room will get a notification
	 * 
	 */
	/* (non-Javadoc)
	 * @see org.red5.server.adapter.MultiThreadedApplicationAdapter#streamBroadcastClose(org.red5.server.api.stream.IBroadcastStream)
	 */
	@Override
	public void streamBroadcastClose(IBroadcastStream stream) {

		// Notify all the clients that the stream had been closed
		log.debug("start streamBroadcastClose broadcast close: " + stream.getPublishedName());
		try {
			IConnection current = Red5.getConnectionLocal();
			Client rcl = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			sendClientBroadcastNotifications(stream, "closeStream", rcl);
		} catch (Exception e) {
			log.error("[streamBroadcastClose]", e);
		}
	}

	/**
	 * This method handles the notification room-based
	 * 
	 * @return void
	 * 
	 */
	private synchronized void sendClientBroadcastNotifications(IBroadcastStream stream, String clientFunction, Client rc) {
		try {
			// Store the local so that we do not send notification to ourself back
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			if (currentClient == null) {

				// In case the client has already left(kicked) this message
				// might be thrown later then the RoomLeave
				// event and the currentClient is already gone
				// The second Use-Case where the currentClient is maybe null is
				// if we remove the client because its a Zombie/Ghost

				return;

			}
			// Notify all the clients that the stream had been started
			log.debug("sendClientBroadcastNotifications: " + stream.getPublishedName());
			log.debug("sendClientBroadcastNotifications : " + currentClient + " " + currentClient.getStreamid());

			// Notify all clients of the same scope (room)
			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						if (conn.equals(current)) {
							// there is a Bug in the current implementation
							// of the appDisconnect
							if (clientFunction.equals("closeStream")) {
								Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
								if (clientFunction.equals("closeStream") && rcl.getIsRecording()) {
									log.debug("*** stopRecordingShowForClient Any Client is Recording - stop that");
									// StreamService.stopRecordingShowForClient(conn,
									// currentClient,
									// rcl.getRoomRecordingName(), false);
									flvRecorderService.stopRecordingShowForClient(conn, currentClient);
								}
								// Don't notify current client
								current.ping();
							}
							continue;
						} else {
							Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
							if (rcl != null) {
								if (rcl.isScreenClient()) {
									// continue;
								} else {
									log.debug("is this users still alive? :" + rcl);
									IServiceCapableConnection iStream = (IServiceCapableConnection) conn;
									iStream.invoke(clientFunction, new Object[] { rc }, this);
								}

								log.debug("sending notification to " + conn + " ID: ");

								// if this close stream event then stop the
								// recording of this stream
								if (clientFunction.equals("closeStream") && rcl.getIsRecording()) {
									log.debug("***  +++++++ ######## sendClientBroadcastNotifications Any Client is Recording - stop that");
									flvRecorderService.stopRecordingShowForClient(conn, currentClient);
								}
							}
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendClientBroadcastNotifications]", err);
		}
	}


	/**
	 * Adds a Moderator by its publicSID
	 * 
	 * @param publicSID
	 * @return -1
	 */
	public synchronized Long addModerator(String publicSID) {
		try {
			log.debug("-----------  addModerator: " + publicSID);

			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();

			currentClient.setIsMod(true);
			// Put the mod-flag to true for this client
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);

			List<Client> currentMods = sessionManager.getCurrentModeratorByRoom(room_id);
			
			//Send message to all users
			sendMessageToCurrentScope("setNewModeratorByList", currentMods, true);

		} catch (Exception err) {
			log.error("[addModerator]", err);
		}
		return -1L;
	}

	@SuppressWarnings("unchecked")
	public void setNewCursorPosition(Object item) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			@SuppressWarnings("rawtypes")
			Map cursor = (Map) item;
			cursor.put("streamPublishName", currentClient.getStreamPublishName());

			sendMessageToCurrentScope("newRed5ScreenCursor", cursor, true, false);
		} catch (Exception err) {
			log.error("[setNewCursorPosition]", err);
		}
	}

	public Long removeModerator(String publicSID) {
		try {
			log.debug("-----------  removeModerator: " + publicSID);

			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				return -1L;
			}
			Long room_id = currentClient.getRoom_id();

			currentClient.setIsMod(false);
			// Put the mod-flag to true for this client
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);

			List<Client> currentMods = sessionManager.getCurrentModeratorByRoom(room_id);

			sendMessageToCurrentScope("setNewModeratorByList", currentMods, true);
		} catch (Exception err) {
			log.error("[addModerator]", err);
		}
		return -1L;
	}

	public Long setBroadCastingFlag(String publicSID, boolean value, Integer interviewPodId) {
		try {
			log.debug("-----------  setBroadCastingFlag: " + publicSID);

			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				return -1L;
			}

			currentClient.setIsBroadcasting(value);
			currentClient.setInterviewPodId(interviewPodId);

			// Put the mod-flag to true for this client
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);
		    
			// Notify all clients of the same scope (room)
			sendMessageToCurrentScope("setNewBroadCastingFlag", currentClient, true);
		} catch (Exception err) {
			log.error("[setBroadCastingFlag]", err);
		}
		return -1L;
	}

	public Long giveExclusiveAudio(String publicSID) {
		try {
			log.debug("-----------  giveExclusiveAudio: " + publicSID);

			IConnection current = Red5.getConnectionLocal();
			// String streamid = current.getClient().getId();

			final Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				return -1L;
			}

			// Put the mod-flag to true for this client
			currentClient.setMicMuted(false);
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);

			// Notify all clients of the same scope (room)
			new MessageSender(current, "receiveExclusiveAudioFlag", currentClient) {
				@Override
				public boolean filter(IConnection conn) {
					Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
					if (rcl == null) {
					} else if (rcl.isScreenClient()) {
					} else {
						if (rcl != currentClient) {
							rcl.setMicMuted(true);
							sessionManager.updateClientByStreamId(rcl.getStreamid(), rcl, false, null);
						}
						return false;
					}
					return true;
				}
			}.start();
		} catch (Exception err) {
			log.error("[giveExclusiveAudio]", err);
		}
		return -1L;
	}

	public Long switchMicMuted(String publicSID, boolean mute) {
		try {
			log.debug("-----------  switchMicMuted: " + publicSID);

			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);
			if (currentClient == null) {
				return -1L;
			}

			currentClient.setMicMuted(mute);
			sessionManager.updateClientByStreamId(currentClient.getStreamid(), currentClient, false, null);

			HashMap<Integer, Object> newMessage = new HashMap<Integer, Object>();
			newMessage.put(0, "updateMuteStatus");
			newMessage.put(1, currentClient);
			sendMessageWithClient(newMessage);
		} catch (Exception err) {
			log.error("[switchMicMuted]", err);
		}
		return 0L;
	}

	public Boolean getMicMutedByPublicSID(String publicSID) {
		try {
			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);
			if (currentClient == null) {
				return true;
			}

			//Put the mod-flag to true for this client
			Boolean muted = currentClient.getMicMuted();
			if (null == muted) {
				muted = true;
			}

			return muted;
		} catch (Exception err) {
			log.error("[getMicMutedByPublicSID]",err);
		}
		return true;
	}

	/**
	 * Invoked by a User whenever he want to become moderator this is needed,
	 * cause if the room has no moderator yet there is no-one he can ask to get
	 * the moderation, in case its a Non-Moderated Room he should then get the
	 * Moderation without any confirmation needed
	 * 
	 * @return Long 1 => means get Moderation, 2 => ask Moderator for
	 *         Moderation, 3 => wait for Moderator
	 */
	public synchronized Long applyForModeration(String publicSID) {
		try {

			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			List<Client> currentModList = sessionManager.getCurrentModeratorByRoom(currentClient.getRoom_id());

			if (currentModList.size() > 0) {
				return 2L;
			} else {
				// No moderator in this room at the moment
				Room room = roomDao.get(currentClient.getRoom_id());

				return room.isModerated() ? 3L : 1L;
			}

		} catch (Exception err) {
			log.error("[applyForModeration]", err);
		}
		return -1L;
	}

	/**
	 * there will be set an attribute called "broadCastCounter" this is the name
	 * this user will publish his stream
	 * 
	 * @return long broadCastId
	 */
	public long getBroadCastId() {
		try {
			log.debug("-----------  getBroadCastId");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client client = sessionManager.getClientByStreamId(streamid, null);
			client.setBroadCastID(broadCastCounter.getAndIncrement());
			sessionManager.updateClientByStreamId(streamid, client, false, null);
			return client.getBroadCastID();
		} catch (Exception err) {
			log.error("[getBroadCastId]", err);
		}
		return -1;
	}

	/**
	 * this must be set _after_ the Video/Audio-Settings have been chosen (see
	 * editrecordstream.lzx) but _before_ anything else happens, it cannot be
	 * applied _after_ the stream has started! avsettings can be: av - video and
	 * audio a - audio only v - video only n - no a/v only static image
	 * furthermore
	 * 
	 * @param avsettings
	 * @param newMessage
	 * @param vWidth
	 * @param vHeight
	 * @param room_id
	 * @param publicSID
	 * @param interviewPodId
	 * @return RoomClient being updated in case of no errors, null otherwise
	 */
	public Client setUserAVSettings(String avsettings,
			Object newMessage, Integer vWidth, Integer vHeight, 
			long room_id, String publicSID, Integer interviewPodId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			IClient c = current.getClient();
			String streamid = c.getId();
			log.debug("-----------  setUserAVSettings {} {} {}", new Object[] {streamid, publicSID, avsettings, newMessage});
			Client parentClient = sessionManager.getClientByPublicSID(publicSID, false, null);
			Client currentClient = sessionManager.getClientByStreamId(streamid, null);
			currentClient.setAvsettings(avsettings);
			currentClient.setRoom_id(room_id);
			currentClient.setPublicSID(publicSID);
			currentClient.setVWidth(vWidth);
			currentClient.setVHeight(vHeight);
			currentClient.setInterviewPodId(interviewPodId);
			currentClient.setUser_id(parentClient.getUser_id());
			currentClient.setLastname(parentClient.getLastname());
			currentClient.setFirstname(parentClient.getFirstname());
			currentClient.setPicture_uri(parentClient.getPicture_uri());
			sessionManager.updateAVClientByStreamId(streamid, currentClient, null);
			SessionVariablesUtil.initClient(c, false, publicSID);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, true);
			return currentClient;
		} catch (Exception err) {
			log.error("[setUserAVSettings]", err);
		}
		return null;
	}

	/*
	 * checks if the user is allowed to apply for Moderation
	 */
	public Boolean checkRoomValues(Long room_id) {
		try {

			// appointed meeting or moderated Room?
			Room room = roomDao.get(room_id);

			// not really - default logic
			if (!room.isAppointment() && room.isModerated()) {
				// if this is a Moderated Room then the Room can be only
				// locked off by the Moderator Bit
				List<Client> clientModeratorListRoom = sessionManager.getCurrentModeratorByRoom(room_id);

				// If there is no Moderator yet and we are asking for it
				// then deny it
				// cause at this moment, the user should wait untill a
				// Moderator enters the Room
				return clientModeratorListRoom.size() != 0;
			} else {
				// FIXME: TODO: For Rooms that are created as Appointment we
				// have to check that too
				// but I don't know yet the Logic behind it - swagner 19.06.2009
				return true;

			}
		} catch (Exception err) {
			log.error("[checkRoomValues]", err);
		}
		return false;
	}

	/**
	 * This function is called once a User enters a Room
	 * 
	 * It contains several different mechanism depending on what roomtype and
	 * what options are available for the room to find out if the current user
	 * will be a moderator of that room or not<br/>
	 * <br/>
	 * Some rules:<br/>
	 * <ul>
	 * <li>If it is a room that was created through the calendar, the user that
	 * organized the room will be moderator, the param Boolean becomeModerator
	 * will be ignored then</li>
	 * <li>In regular rooms you can use the param Boolean becomeModerator to set
	 * any user to become a moderator of the room</li>
	 * </ul>
	 * <br/>
	 * If a new moderator is detected a Push Call to all current users of the
	 * room is invoked "setNewModeratorByList" to notify them of the new
	 * moderator<br/>
	 * <br/>
	 * At the end of the mechanism a push call with the new client-object
	 * and all the informations about the new user is send to every user of the
	 * current conference room<br/>
	 * <br/>
	 *
	 * @param room_id - id of the room
	 * @param becomeModerator - is user will become moderator
	 * @param isSuperModerator - is user super moderator
	 * @param organization_id - organization id of the user
	 * @param colorObj - some color
	 * @return RoomStatus object
	 */
	public synchronized RoomStatus setRoomValues(Long room_id, Boolean becomeModerator, Boolean isSuperModerator,
			Long organization_id, String colorObj) {
		try {
			log.debug("-----------  setRoomValues");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamid, null);
			currentClient.setRoom_id(room_id);
			currentClient.setRoomEnter(new Date());

			currentClient.setUsercolor(colorObj);

			// Inject externalUserId if nothing is set yet
			if (currentClient.getExternalUserId() == null) {
				if (currentClient.getUser_id() != null) {
					User us = usersDao.get(currentClient.getUser_id());
					if (us != null) {
						currentClient.setExternalUserId(us.getExternalUserId());
						currentClient.setExternalUserType(us.getExternalUserType());
					}
				}
			}

			// This can be set without checking for Moderation Flag
			currentClient.setIsSuperModerator(isSuperModerator);

			sessionManager.updateClientByStreamId(streamid, currentClient, true, null);

            Room room = roomDao.get(room_id);
            if (room.getShowMicrophoneStatus()) {
            	currentClient.setCanGiveAudio(true);
            }

			// Log the User
			conferenceLogDao.addConferenceLog("roomEnter",
					currentClient.getUser_id(), streamid, room_id,
					currentClient.getUserip(), "",
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getEmail(), currentClient.getFirstname(),
					currentClient.getLastname());
			
			// Check for Moderation LogicalRoom ENTER
			List<Client> clientListRoom = sessionManager.getClientListByRoom(room_id);

			// Return Object
			RoomStatus roomStatus = new RoomStatus();
			// appointed meeting or moderated Room? => Check Max Users first
			if (room.getNumberOfPartizipants() != null && clientListRoom.size() > room.getNumberOfPartizipants()) {
				roomStatus.setRoomFull(true);
				return roomStatus;
			}

			// default logic for non regular rooms
			if (!room.isAppointment()) {
				if (room.isModerated()) {
					// if this is a Moderated Room then the Room can be only
					// locked off by the Moderator Bit
					// List<RoomClient> clientModeratorListRoom =
					// this.sessionManager.getCurrentModeratorByRoom(room_id);

					// If there is no Moderator yet we have to check if the
					// current User has the Bit set to true to
					// become one, otherwise he won't get Moderation and has to
					// wait
					if (becomeModerator) {
						currentClient.setIsMod(true);

						// There is a need to send an extra Event here, cause at
						// this moment there could be
						// already somebody in the Room waiting

						// Update the Client List
						sessionManager.updateClientByStreamId(streamid, currentClient, false, null);

						List<Client> modRoomList = sessionManager.getCurrentModeratorByRoom(currentClient.getRoom_id());
						
						//Sync message to everybody
						sendMessageToCurrentScope("setNewModeratorByList", modRoomList, false);
					} else {
						// The current User is not a Teacher/Admin or whatever
						// Role that should get the
						// Moderation
						currentClient.setIsMod(false);
					}
				} else {
					// If this is a normal Room Moderator rules : first come, first draw ;-)
					log.debug("setRoomValues : Room"
							+ room_id
							+ " not appointed! Moderator rules : first come, first draw ;-)");
					if (clientListRoom.size() == 1) {
						log.debug("Room is empty so set this user to be moderation role");
						currentClient.setIsMod(true);
					} else {
						log.debug("Room is already somebody so set this user not to be moderation role");

						if (becomeModerator) {
							currentClient.setIsMod(true);

							// Update the Client List
							sessionManager.updateClientByStreamId(streamid, currentClient, false, null);

							List<Client> modRoomList = sessionManager.getCurrentModeratorByRoom(currentClient.getRoom_id());

							// There is a need to send an extra Event here,
							// cause at this moment there could be
							// already somebody in the Room waiting -swagner check this comment, 20.01.2012
							
							//Sync message to everybody
							sendMessageToCurrentScope("setNewModeratorByList", modRoomList, false);

						} else {
							// The current User is not a Teacher/Admin or
							// whatever Role that should get the Moderation
							currentClient.setIsMod(false);
						}
					}
				}

				// Update the Client List
				sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
			} else {
				// If this is an Appointment then the Moderator will be set to the Invitor

				Appointment ment = appointmentDao.getAppointmentByRoom(room_id);

				Long userIdInRoomClient = currentClient.getUser_id();

				boolean found = false;
				boolean moderator_set = false;
				// First check owner who is not in the members list
				if (ment.getOwner().getId().equals(userIdInRoomClient)) {
					found = true;
					log.debug("User "
							+ userIdInRoomClient
							+ " is moderator due to flag in MeetingMember record");
					currentClient.setIsMod(true);
					moderator_set = true;

					// Update the Client List
					sessionManager.updateClientByStreamId(streamid, currentClient, false, null);

					List<Client> modRoomList = sessionManager.getCurrentModeratorByRoom(currentClient.getRoom_id());

					// There is a need to send an extra Event here, cause at this moment 
					// there could be already somebody in the Room waiting

					//Sync message to everybody
					sendMessageToCurrentScope("setNewModeratorByList", modRoomList, false);
				}
				if (!found) {
					// Check if current user is set to moderator
					for (MeetingMember member : ment.getMeetingMembers()) {
						// only persistent users can schedule a meeting
						// user-id is only set for registered users
						if (member.getUser() != null) {
							log.debug("checking user " + member.getUser().getFirstname()
									+ " for moderator role - ID : "
									+ member.getUser().getId());
	
							if (member.getUser().getId().equals(userIdInRoomClient)) {
								found = true;
								log.debug("User " + userIdInRoomClient+ " is NOT moderator due to flag in MeetingMember record");
								currentClient.setIsMod(false);
								sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
								break;
							}
						}
					}
				}

				if (!found) {
					log.debug("User "
							+ userIdInRoomClient
							+ " could not be found as MeetingMember -> definitely no moderator");
					currentClient.setIsMod(false);
					sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
				} else {
					// if current user is part of the member list, but moderator
					// couldn't be retrieved : first come, first draw!
					if (clientListRoom.size() == 1 && !moderator_set) {
						log.debug("");
						currentClient.setIsMod(true);

						// Update the Client List
						sessionManager.updateClientByStreamId(streamid, currentClient, false, null);

						List<Client> modRoomList = sessionManager.getCurrentModeratorByRoom(currentClient.getRoom_id());

						// There is a need to send an extra Event here, cause at
						// this moment there could be
						// already somebody in the Room waiting

						//Sync message to everybody
						sendMessageToCurrentScope("setNewModeratorByList", modRoomList, false);
						
						sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
					}
				}

			}
			
			//Sync message to everybody
			sendMessageToCurrentScope("addNewUser", currentClient, false);

			//Status object for Shared Browsing
			BrowserStatus browserStatus = (BrowserStatus)current.getScope().getAttribute("browserStatus");

			if (browserStatus == null) {
				browserStatus = new BrowserStatus();
			}

			// RoomStatus roomStatus = new RoomStatus();

			// FIXME: Rework Client Object to DTOs
			roomStatus.setClientList(clientListRoom);
			roomStatus.setBrowserStatus(browserStatus);

			return roomStatus;
		} catch (Exception err) {
			log.error("[setRoomValues]", err);
		}
		return null;
	}

	/**
	 * This method is invoked when the user has disconnected and reconnects to
	 * the Gateway with the new scope
	 * 
	 * @param SID
	 * @param userId
	 * @param username
	 * @param firstname
	 * @param lastname
	 * @param picture_uri
	 * @return client being updated in case of success, null otherwise
	 */
	public Client setUsernameReconnect(String SID, Long userId, String username, String firstname, String lastname, String picture_uri) {
		try {
			log.debug("-----------  setUsernameReconnect");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			SessionVariablesUtil.setUserId(current.getClient(), userId);
			currentClient.setPicture_uri(picture_uri);
			currentClient.setUserObject(userId, username, firstname, lastname);

			// Update Session Data
			sessiondataDao.updateUserWithoutSession(SID, userId);

			// only fill this value from User-Record
			// cause invited users have no associated User, so
			// you cannot set the firstname,lastname from the UserRecord
			if (userId != null) {
				User us = usersDao.get(userId);
				
				if (us != null) {
					currentClient.setExternalUserId(us.getExternalUserId());
					currentClient.setExternalUserType(us.getExternalUserType());
					if (us.getPictureuri() != null) {
						// set Picture-URI
						currentClient.setPicture_uri(us.getPictureuri());
					}
				}
			}
			sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
			return currentClient;
		} catch (Exception err) {
			log.error("[setUsername]", err);
		}
		return null;
	}

	/**
	 * this is set initial directly after login/loading language
	 * 
	 * @param SID - id of the session
	 * @param userId - id of the user being set
	 * @param username - username of the user
	 * @param firstname - firstname of the user
	 * @param lastname - lastname of the user
	 * @return RoomClient in case of everything is OK, null otherwise
	 */
	public Client setUsernameAndSession(String SID, Long userId, String username, String firstname, String lastname) {
		try {
			log.debug("-----------  setUsernameAndSession");
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamid, null);

			currentClient.setUsername(username);
			currentClient.setUser_id(userId);
			SessionVariablesUtil.setUserId(current.getClient(), userId);
			currentClient.setUserObject(userId, username, firstname, lastname);

			// Update Session Data
			log.debug("UDPATE SESSION " + SID + ", " + userId);
			sessiondataDao.updateUserWithoutSession(SID, userId);

			User user = usersDao.get(userId);

			if (user != null) {
				currentClient.setExternalUserId(user.getExternalUserId());
				currentClient.setExternalUserType(user.getExternalUserType());
			}

			// only fill this value from User-Record
			// cause invited users have non
			// you cannot set the firstname,lastname from the UserRecord
			User us = usersDao.get(userId);
			if (us != null && us.getPictureuri() != null) {
				// set Picture-URI
				currentClient.setPicture_uri(us.getPictureuri());
			}
			sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
			return currentClient;
		} catch (Exception err) {
			log.error("[setUsername]", err);
		}
		return null;
	}

	/**
	 * used by the Screen-Sharing Servlet to trigger events
	 * 
	 * @param room_id
	 * @param message
	 * @return the list of room clients
	 */
	public HashMap<String, Client> sendMessageByRoomAndDomain(Long room_id, Object message) {
		HashMap<String, Client> roomClientList = new HashMap<String, Client>();
		try {

			log.debug("sendMessageByRoomAndDomain " + room_id);

			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(OpenmeetingsVariables.webAppRootKey);

			log.debug("webAppKeyScope " + webAppKeyScope);

			IScope scopeHibernate = webAppKeyScope.getScope(room_id.toString());

			new MessageSender(scopeHibernate, "newMessageByRoomAndDomain", message) {
				@Override
				public boolean filter(IConnection conn) {
					IClient client = conn.getClient();
					return SessionVariablesUtil.isScreenClient(client) || SessionVariablesUtil.isAVClient(client);
				}
			}.start();
		} catch (Exception err) {
			log.error("[getClientListBYRoomAndDomain]", err);
		}
		return roomClientList;
	}

	public synchronized List<Client> getCurrentModeratorList() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			Long room_id = currentClient.getRoom_id();
			return sessionManager.getCurrentModeratorByRoom(room_id);
		} catch (Exception err) {
			log.error("[getCurrentModerator]", err);
		}
		return null;
	}

	/**
	 * This Function is triggered from the Whiteboard
	 * 
	 * @param whiteboardObjParam - array of parameters being sended to whiteboard
	 * @param whiteboardId - id of whiteboard parameters will be send to
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendVarsByWhiteboardId(ArrayList whiteboardObjParam, Long whiteboardId) {
		//
		try {

			Map whiteboardObj = new HashMap();
			int i = 0;
			for (Iterator iter = whiteboardObjParam.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				// log.debug("obj"+obj);
				whiteboardObj.put(i, obj);
				i++;
			}

			// Check if this User is the Mod:
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager
					.getClientByStreamId(current.getClient().getId(), null);

			if (currentClient == null) {
				return;
			}

			Long room_id = currentClient.getRoom_id();

			// log.debug("***** sendVars: " + whiteboardObj);

			// Store event in list
			String action = whiteboardObj.get(2).toString();

			if (action.equals("deleteMindMapNodes")) {

				// Simulate Single Delete Events for z-Index
				List actionObject = (List) whiteboardObj.get(3);

				List<List> itemObjects = (List) actionObject.get(3);

				Map whiteboardTempObj = new HashMap();
				whiteboardTempObj.put(2, "delete");

				for (List itemObject : itemObjects) {

					List<Object> tempActionObject = new LinkedList<Object>();
					tempActionObject.add("mindmapnode");
					tempActionObject.add(itemObject.get(0)); // z-Index -8
					tempActionObject.add(null); // simulate -7
					tempActionObject.add(null); // simulate -6
					tempActionObject.add(null); // simulate -5
					tempActionObject.add(null); // simulate -4
					tempActionObject.add(null); // simulate -3
					tempActionObject.add(null); // simulate -2
					tempActionObject.add(itemObject.get(1)); // Object-Name -1

					whiteboardTempObj.put(3, tempActionObject);

					whiteboardManagement.addWhiteBoardObjectById(
							room_id, whiteboardTempObj, whiteboardId);

				}

			} else {

				whiteboardManagement.addWhiteBoardObjectById(
						room_id, whiteboardObj, whiteboardId);

			}

			Map<String, Object> sendObject = new HashMap<String, Object>();
			sendObject.put("id", whiteboardId);
			sendObject.put("param", whiteboardObjParam);

			boolean showDrawStatus = getWhiteboardDrawStatus();

			// Notify all Clients of that Scope (Room)
			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						IClient client = conn.getClient();
						if (SessionVariablesUtil.isScreenClient(client)) {
							// screen sharing clients do not receive events
							continue;
						} else if (SessionVariablesUtil.isAVClient(client)) {
							// AVClients or potential AVClients do not receive events
							continue;
						} if (client.getId().equals(
								current.getClient().getId())) {
							// don't send back to same user
							continue;
						}
						((IServiceCapableConnection) conn).invoke(
							"sendVarsToWhiteboardById",
								new Object[] { showDrawStatus ? currentClient : null, sendObject }, 
									this);
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendVarsByWhiteboardId]", err);
		}
	}

	public int sendVarsModeratorGeneral(Object vars) {
		log.debug("*..*sendVars: " + vars);
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
			// Long room_id = currentClient.getRoom_id();

			log.debug("***** id: " + currentClient.getStreamid());

			boolean ismod = currentClient.getIsMod();

			if (ismod) {
				log.debug("CurrentScope :" + current.getScope().getName());
				// Send to all Clients of the same Scope
				sendMessageToCurrentScope("sendVarsToModeratorGeneral", vars, false);
				return 1;
			} else {
				// log.debug("*..*you are not allowed to send: "+ismod);
				return -1;
			}
		} catch (Exception err) {
			log.error("[sendVarsModeratorGeneral]", err);
		}
		return -1;
	}

	public int sendMessage(Object newMessage) {
		try {
			
			sendMessageToCurrentScope("sendVarsToMessage", newMessage, false);
			
		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
	}
	
	public int sendMessageAll(Object newMessage) {
		try {
			
			sendMessageToCurrentScope("sendVarsToMessage", newMessage, true);
			
		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
	}

	/**
	 * send status for shared browsing to all members except self
	 * @param newMessage
	 * @return 1
	 */
	@SuppressWarnings({ "rawtypes" })
	public synchronized int sendBrowserMessageToMembers(Object newMessage) {
		try {
			IConnection current = Red5.getConnectionLocal();

			List newMessageList = (List) newMessage;

			String action = newMessageList.get(0).toString();

			BrowserStatus browserStatus = (BrowserStatus) current.getScope().getAttribute("browserStatus");

			if (browserStatus == null) {
				browserStatus = new BrowserStatus();
			}

			if (action.equals("initBrowser") || action.equals("newBrowserURL")) {
				browserStatus.setBrowserInited(true);
				browserStatus.setCurrentURL(newMessageList.get(1).toString());
			} else if (action.equals("closeBrowserURL")) {
				browserStatus.setBrowserInited(false);
			}

			current.getScope().setAttribute("browserStatus", browserStatus);
			
			sendMessageToCurrentScope("sendVarsToMessage", newMessage, false);

		} catch (Exception err) {
			log.error("[sendMessage]", err);
		}
		return 1;
	}

	/**
	 * wrapper method
	 * @param newMessage
	 */
	public void sendMessageToMembers(Object newMessage) {
		//Sync to all users of current scope
		sendMessageToCurrentScope("sendVarsToMessage", newMessage, false);
	}
	
	/**
	 * General sync mechanism for all messages that are send from within the 
	 * scope of the current client, but:
	 * <ul>
	 * <li>optionally do not send to self (see param: sendSelf)</li>
	 * <li>do not send to clients that are screen sharing clients</li>
	 * <li>do not send to clients that are audio/video clients (or potentially ones)</li>
	 * <li>do not send to connections where no RoomClient is registered</li>
	 * </ul>
	 *  
	 * @param remoteMethodName The method to be called
	 * @param newMessage parameters
	 * @param sendSelf send to the current client as well
	 */
	public void sendMessageToCurrentScope(String remoteMethodName, Object newMessage, boolean sendSelf) {
		sendMessageToCurrentScope(remoteMethodName, newMessage, sendSelf, false);
	}
	
	/**
	 * Only temporary for load test, with return argument for the client to have a result
	 * 
	 * @param remoteMethodName
	 * @param newMessage
	 * @param sendSelf
	 * @return true
	 */
	@Deprecated
	public boolean loadTestSyncMessage(String remoteMethodName, Object newMessage, boolean sendSelf) {
		sendMessageToCurrentScope(remoteMethodName, newMessage, sendSelf, false);
		return true;
	}
	
	
	/**
	 * General sync mechanism for all messages that are send from within the 
	 * scope of the current client, but:
	 * <ul>
	 * <li>optionally do not send to self (see param: sendSelf)</li>
	 * <li>send to clients that are screen sharing clients based on parameter</li>
	 * <li>do not send to clients that are audio/video clients (or potentially ones)</li>
	 * <li>do not send to connections where no RoomClient is registered</li>
	 * </ul>
	 *  
	 * @param remoteMethodName The method to be called
	 * @param newMessage parameters
	 * @param sendSelf send to the current client as well
	 * @param sendScreen send to the current client as well
	 */
	public void sendMessageToCurrentScope(final String remoteMethodName, final Object newMessage, final boolean sendSelf, final boolean sendScreen) {
		new MessageSender(remoteMethodName, newMessage) {
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return (!sendScreen && SessionVariablesUtil.isScreenClient(client))
						|| SessionVariablesUtil.isAVClient(client)
						|| (!sendSelf && client.getId().equals(current.getClient().getId()));
			}
		}.start();

	}

	public abstract class MessageSender extends Thread {
		final IScope scope;
		final IConnection current;
		final String remoteMethodName;
		final Object newMessage;
		
		public MessageSender(final String remoteMethodName, final Object newMessage) {
			this((IScope)null, remoteMethodName, newMessage);
		}
		
		public MessageSender(IScope _scope, String remoteMethodName, Object newMessage) {
			this(Red5.getConnectionLocal(), _scope, remoteMethodName, newMessage);
		}
		
		public MessageSender(IConnection current, String remoteMethodName, Object newMessage) {
			this(current, null, remoteMethodName, newMessage);
		}
		
		public MessageSender(IConnection current, IScope _scope, String remoteMethodName, Object newMessage) {
			this.current = current;
			scope = _scope == null ? current.getScope() : _scope;
			this.remoteMethodName = remoteMethodName;
			this.newMessage = newMessage;
		}
		
		public abstract boolean filter(IConnection conn);
		
		@Override
		public void run() {
			try {
				if (scope == null) {
					log.debug(String.format("[MessageSender] -> 'Unable to send message to NULL scope' %s, %s", remoteMethodName, newMessage));
				} else {
					log.trace(String.format("[MessageSender] -> 'sending message' %s, %s", remoteMethodName, newMessage));
					// Send to all Clients of that Scope(Room)
					for (IConnection conn : scope.getClientConnections()) {
						if (conn != null && conn instanceof IServiceCapableConnection) {
							if (filter(conn)) {
								continue;
							}
							((IServiceCapableConnection) conn).invoke(remoteMethodName, new Object[] { newMessage }, ScopeApplicationAdapter.this);
						}
					}
					log.trace(String.format("[MessageSender] -> 'sending message DONE' %s", remoteMethodName));
				}
			} catch (Exception err) {
				log.error(String.format("[MessageSender -> %s, %s]", remoteMethodName, newMessage), err);
			}
		}
	}

	/**
	 * wrapper method
	 * @param newMessage
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageWithClient(Object newMessage) {
		try {
			sendMessageWithClientWithSyncObject(newMessage, true);

		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}
	
	/**
	 * wrapper method
	 * @param newMessage
	 * @param sync
	 * @return 1 in case of success, -1 otherwise
	 */
	public int sendMessageWithClientWithSyncObject(Object newMessage, boolean sync) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);
			
			//Sync to all users of current scope
			sendMessageToCurrentScope("sendVarsToMessageWithClient", hsm, sync);

		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * Function is used to send the kick Trigger at the moment, 
	 * it sends a general message to a specific clientId
	 * 
	 * @param newMessage
	 * @param clientId
	 * @return 1 in case of success, -1 otherwise
	 */
	public synchronized int sendMessageById(Object newMessage, String clientId, IScope scope) {
		try {
			log.debug("### sendMessageById ###" + clientId);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			for (IConnection conn : scope.getClientConnections()) {
				if (conn != null) {
					if (conn instanceof IServiceCapableConnection) {
						if (conn.getClient().getId().equals(clientId)) {
							((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient", new Object[] { hsm }, this);
						}
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}

	/**
	 * Sends a message to a user in the same room by its clientId
	 * 
	 * @param newMessage
	 * @param clientId
	 * @return 1 in case of no exceptions, -1 otherwise
	 */
	public synchronized int sendMessageWithClientById(Object newMessage, String clientId) {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			HashMap<String, Object> hsm = new HashMap<String, Object>();
			hsm.put("client", currentClient);
			hsm.put("message", newMessage);

			// broadcast Message to specific user with id inside the same Scope
			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn.getClient().getId().equals(clientId)) {
					((IServiceCapableConnection) conn).invoke("sendVarsToMessageWithClient", new Object[] { hsm }, this);
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
			return -1;
		}
		return 1;
	}
	
	public synchronized void sendUploadCompletMessageByPublicSID(UploadCompleteMessage message, String publicSID) {
		try {
			//if the upload is locally, just proceed to the normal function
			//Search for RoomClient on current server (serverId == null means it will look on the master for the RoomClient)
			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);
			
			if (currentClient != null) {
				sendMessageWithClientByPublicSID(message, publicSID);
			} else {
				throw new Exception("Could not Find RoomClient on List publicSID: "+ publicSID);
			}
			
		} catch (Exception err) {
			log.error("[sendUploadCompletMessageByPublicSID] ", err);
		}
	}
	

	public synchronized void sendMessageWithClientByPublicSID(Object message, String publicSID) {
		try {
			// ApplicationContext appCtx = getContext().getApplicationContext();
			IScope globalScope = getContext().getGlobalScope();

			IScope webAppKeyScope = globalScope.getScope(OpenmeetingsVariables.webAppRootKey);

			// log.debug("webAppKeyScope "+webAppKeyScope);

			// Get Room Id to send it to the correct Scope
			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				throw new Exception("Could not Find RoomClient on List publicSID: " + publicSID);
			}
			// default Scope Name
			String scopeName = "hibernate";
			if (currentClient.getRoom_id() != null) {
				scopeName = currentClient.getRoom_id().toString();
			}

			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

			// log.debug("scopeHibernate "+scopeHibernate);

			if (scopeHibernate != null) {
				// Notify the clients of the same scope (room) with user_id

				for (IConnection conn : webAppKeyScope.getScope(scopeName).getClientConnections()) {
					IClient client = conn.getClient();
					if (SessionVariablesUtil.isScreenClient(client)) {
						// screen sharing clients do not receive events
						continue;
					} else if (SessionVariablesUtil.isAVClient(client)) {
						// AVClients or potential AVClients do not receive events
						continue;
					}
					
					if (SessionVariablesUtil.getPublicSID(client).equals(publicSID)) {
						((IServiceCapableConnection) conn).invoke("newMessageByRoomAndDomain", new Object[] { message }, this);
					}
				}
			} else {
				// Scope not yet started
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
		}
	}

	public synchronized void sendMessageWithClientByPublicSIDOrUser(Object message, String publicSID, Long user_id) {
		try {
			// Get Room Id to send it to the correct Scope
			Client currentClient = sessionManager.getClientByPublicSID(publicSID, false, null);

			if (currentClient == null) {
				currentClient = sessionManager.getClientByUserId(user_id);
			}

			Set<IConnection> conset = null;

			if (currentClient == null) {
				// Must be from a previous session, search for user in current scope
				IConnection current = Red5.getConnectionLocal();
				// Notify all Clients of that Scope (Room)
				conset = current.getScope().getClientConnections();
			} else {
				// default Scope Name
				String scopeName = "hibernate";
				if (currentClient.getRoom_id() != null) {
					scopeName = currentClient.getRoom_id().toString();
				}

				IScope globalScope = getContext().getGlobalScope();
				IScope webAppKeyScope = globalScope.getScope(OpenmeetingsVariables.webAppRootKey);
				IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

				if (scopeHibernate != null) {
					conset = scopeHibernate.getClientConnections();
				}
			}

			// Notify the clients of the same scope (room) with user_id
			for (IConnection conn : conset) {
				if (conn != null) {
					
					IClient client = conn.getClient();
					if (SessionVariablesUtil.isScreenClient(client)) {
						// screen sharing clients do not receive events
						continue;
					} else if (SessionVariablesUtil.isAVClient(client)) {
						// AVClients or potential AVClients do not receive events
						continue;
					}
					
					if (SessionVariablesUtil.getPublicSID(client).equals(publicSID)) {
						// log.debug("IS EQUAL ");
						((IServiceCapableConnection) conn).invoke("newMessageByRoomAndDomain", new Object[] { message }, this);
						log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain" + message);
					} else if (user_id != 0 && SessionVariablesUtil.getUserId(client).equals(user_id)) {
						((IServiceCapableConnection) conn).invoke("newMessageByRoomAndDomain", new Object[] { message }, this);
						log.debug("sendMessageWithClientByPublicSID RPC:newMessageByRoomAndDomain" + message);
					}
				}
			}
		} catch (Exception err) {
			log.error("[sendMessageWithClient] ", err);
		}
	}

	/**
	 * @deprecated this method should be reworked to use a single SQL query in
	 *             the cache to get any client in the current room that is
	 *             recording instead of iterating through connections!
	 * @return true in case there is recording session, false otherwise, null if any exception happend
	 */
	public Boolean getInterviewRecordingStatus() {
		try {
			IConnection current = Red5.getConnectionLocal();

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

					if (rcl.getIsRecording() != null && rcl.getIsRecording()) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception err) {
			log.error("[getInterviewRecordingStatus]", err);
		}

		return null;
	}

	/**
	 * @deprecated @see {@link ScopeApplicationAdapter#getInterviewRecordingStatus()}
	 * @return - false if there were existing recording, true if recording was started successfully, null if any exception happens
	 */
	public Boolean startInterviewRecording() {
		try {
			log.debug("-----------  startInterviewRecording");
			IConnection current = Red5.getConnectionLocal();

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);

					if (rcl != null && rcl.getIsRecording() != null && rcl.getIsRecording()) {
						return false;
					}
				}
			}
			Client current_rcl = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			// Also set the Recording Flag to Record all Participants that enter
			// later
			current_rcl.setIsRecording(true);
			sessionManager.updateClientByStreamId(current.getClient().getId(), current_rcl, false, null);

			Map<String, String> interviewStatus = new HashMap<String, String>();
			interviewStatus.put("action", "start");

			for (IConnection conn : current.getScope().getClientConnections()) {
				if (conn != null) {
					IClient client = conn.getClient();
					if (SessionVariablesUtil.isScreenClient(client)) {
						// screen sharing clients do not receive events
						continue;
					} else if (SessionVariablesUtil.isAVClient(client)) {
						// AVClients or potential AVClients do not receive events
						continue;
					}

					((IServiceCapableConnection) conn).invoke("interviewStatus", new Object[] { interviewStatus }, this);
					log.debug("-- startInterviewRecording " + interviewStatus);
				}
			}
			String recordingName = "Interview " + CalendarPatterns.getDateWithTimeByMiliSeconds(new Date());

			flvRecorderService.recordMeetingStream(current, current_rcl, recordingName, "", true);

			return true;
		} catch (Exception err) {
			log.debug("[startInterviewRecording]", err);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public Boolean sendRemoteCursorEvent(final String streamid, Map messageObj) {
		new MessageSender("sendRemoteCursorEvent", messageObj) {
			
			@Override
			public boolean filter(IConnection conn) {
				IClient client = conn.getClient();
				return SessionVariablesUtil.isScreenClient(client) && conn.getClient().getId().equals(streamid);
			}
		}.start();
		return null;
	}

	private Long checkRecordingClient(IConnection conn) {
		Long flvRecordingId = null;
		if (conn != null) {
			Client rcl = sessionManager.getClientByStreamId(conn.getClient().getId(), null);
			if (rcl != null && rcl.getIsRecording() != null && rcl.getIsRecording()) {
				rcl.setIsRecording(false);
				flvRecordingId = rcl.getFlvRecordingId();
				rcl.setFlvRecordingId(null);

				// Reset the Recording Flag to Record all
				// Participants that enter later
				sessionManager.updateClientByStreamId(conn.getClient().getId(), rcl, false, null);
			}
		}
		return flvRecordingId;
	}
	
	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 * 
	 * @return true if interview was found
	 */
	public Boolean stopInterviewRecording() {
		IConnection current = Red5.getConnectionLocal();
		Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);
		return _stopInterviewRecording(currentClient, current.getScope());
	}
	
	/**
	 * Stop the recording of the streams and send event to connected users of scope
	 * 
	 * @return true if interview was found
	 */
	private Boolean _stopInterviewRecording(Client currentClient, IScope currentScope) {
		try {
			log.debug("-----------  stopInterviewRecording");
			Long flvRecordingId = currentClient.getFlvRecordingId();

			for (IConnection conn : currentScope.getClientConnections()) {
				Long recordingId = checkRecordingClient(conn);
				if (recordingId != null) {
					flvRecordingId = recordingId;
				}
			}
			if (flvRecordingId == null) {
				log.debug("stopInterviewRecording:: unable to find recording client");
				return false;
			}

			flvRecorderService.stopRecordAndSave(scope, currentClient, flvRecordingId);

			Map<String, String> interviewStatus = new HashMap<String, String>();
			interviewStatus.put("action", "stop");

			sendMessageToCurrentScope("interviewStatus", interviewStatus, true);
			sendMessageToCurrentScope("stopRecordingMessage", currentClient, true);
			return true;

		} catch (Exception err) {
			log.debug("[stopInterviewRecording]", err);
		}
		return null;
	}

	/**
	 * Get all ClientList Objects of that room and domain Used in
	 * lz.applyForModeration.lzx
	 * 
	 * @return all ClientList Objects of that room
	 */
	public synchronized List<Client> getClientListScope() {
		try {
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = sessionManager.getClientByStreamId(current.getClient().getId(), null);

			return sessionManager.getClientListByRoom(currentClient.getRoom_id());
		} catch (Exception err) {
			log.debug("[getClientListScope]", err);
		}
		return new ArrayList<Client>();
	}

	private boolean getWhiteboardDrawStatus() {
		return configurationDao.getWhiteboardDrawStatus();
	}
	
	public String getCryptKey() {
		return configurationDao.getCryptKey();
	}

	public IScope getRoomScope(String room) {
		try {

			IScope globalScope = getContext().getGlobalScope();
			IScope webAppKeyScope = globalScope.getScope(OpenmeetingsVariables.webAppRootKey);

			String scopeName = "hibernate";
			// If set then its a NON default Scope
			if (room.length() != 0) {
				scopeName = room;
			}

			IScope scopeHibernate = webAppKeyScope.getScope(scopeName);

			return scopeHibernate;
		} catch (Exception err) {
			log.error("[getRoomScope]", err);
		}
		return null;
	}

    /*
	 * SIP transport methods
	 */

	private List<Long> getVerifiedActiveRoomIds(Server s) {
		List<Long> result = new ArrayList<Long>(sessionManager.getActiveRoomIdsByServer(s));
		//verify
		for (Iterator<Long> i = result.iterator(); i.hasNext();) {
			Long id = i.next();
			List<Client> rcs = sessionManager.getClientListByRoom(id);
			if (rcs.size() == 0 || (rcs.size() == 1 && rcs.get(0).isSipTransport())) {
				i.remove();
			}
		}
		return result.isEmpty() ? result : roomDao.getSipRooms(result);
	}
	
	public List<Long> getActiveRoomIds() {
		List<Long> result = getVerifiedActiveRoomIds(null);
		for (Server s : serverDao.getActiveServers()) {
			result.addAll(getVerifiedActiveRoomIds(s));
		}
		return result.isEmpty() ? result : roomDao.getSipRooms(result);
	}
	
	private String getSipTransportLastname(Long roomId) {
		return getSipTransportLastname(roomId, roomManager.getSipConferenceMembersNumber(roomId));
	}
	
	private String getSipTransportLastname(Long roomId, Integer c) {
		return (c != null && c > 0) ? "(" + (c - 1) + ")" : "";
	}
	
    public synchronized int updateSipTransport() {
		log.debug("-----------  updateSipTransport");
        IConnection current = Red5.getConnectionLocal();
        String streamid = current.getClient().getId();
        Client client = sessionManager.getClientByStreamId(streamid, null);
        Long roomId = client.getRoom_id();
        Integer count = roomManager.getSipConferenceMembersNumber(roomId); 
        String newNumber = getSipTransportLastname(roomId, count);
        log.debug("getSipConferenceMembersNumber: " + newNumber);
        if (!newNumber.equals(client.getLastname())) {
            client.setLastname(newNumber);
            sessionManager.updateClientByStreamId(streamid, client, false, null);
            log.debug("updateSipTransport: {}, {}, {}, {}, {}", new Object[]{client.getPublicSID(),
                    client.getRoom_id(), client.getFirstname(), client.getLastname(), client.getAvsettings()});
            sendMessageWithClient(new String[]{"personal",client.getFirstname(),client.getLastname()});
        }
        return count != null && count > 0 ? count - 1 : 0; 
    }

    /**
     * Perform call to specified phone number and join to conference
     * @param number to call
     */
	public synchronized void joinToConfCall(String number) {
		IConnection current = Red5.getConnectionLocal();
		String streamid = current.getClient().getId();
		Client currentClient = sessionManager.getClientByStreamId(streamid, null);
		try {
			String sipNumber = getSipNumber(currentClient.getRoom_id());
			log.debug("asterisk -rx \"originate Local/" + number + "@rooms-out extension " + sipNumber + "@rooms-originate\"");
			Runtime.getRuntime().exec(new String[] { "asterisk", "-rx", "originate Local/" + number + "@rooms-out extension " + sipNumber + "@rooms-originate" });
		} catch (IOException e) {
			log.error("Executing asterisk originate error: ", e);
		}
	}

    public synchronized String getSipNumber(Long room_id) {
        Room r = roomDao.get(room_id);
        if(r != null && r.getConfno() != null) {
            log.debug("getSipNumber: room_id: {}, sipNumber: {}", new Object[]{room_id, r.getConfno()});
            return r.getConfno();
        }
        return null;
    }

	public void setSipTransport(Long room_id, String publicSID, String broadCastId) {
		log.debug("-----------  setSipTransport");
		IConnection current = Red5.getConnectionLocal();
		IClient c = current.getClient();
		String streamid = c.getId();
		// Notify all clients of the same scope (room)
		Client currentClient = sessionManager.getClientByStreamId(streamid, null);
		currentClient.setSipTransport(true);
		currentClient.setRoom_id(room_id);
		currentClient.setRoomEnter(new Date());
		currentClient.setFirstname("SIP Transport");
		currentClient.setLastname(getSipTransportLastname(room_id));
		currentClient.setBroadCastID(Long.parseLong(broadCastId));
		currentClient.setIsBroadcasting(true);
		currentClient.setPublicSID(publicSID);
		currentClient.setVWidth(120);
		currentClient.setVHeight(90);
		currentClient.setPicture_uri("phone.png");
		sessionManager.updateClientByStreamId(streamid, currentClient, false, null);
		SessionVariablesUtil.initClient(c, false, publicSID);

		sendMessageToCurrentScope("addNewUser", currentClient, false);
	}
}
