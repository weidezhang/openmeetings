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
package org.apache.openmeetings.remote;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.openmeetings.data.conference.InvitationManager;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.log.ConferenceLogDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.SOAPLoginDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.MeetingMember;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.server.RemoteSessionObject;
import org.apache.openmeetings.db.entity.server.SOAPLogin;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.db.entity.user.Userdata;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.ldap.LdapLoginManagement;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.remote.util.SessionVariablesUtil;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author swagner
 * 
 */
public class MainService implements IPendingServiceCallback {

	private static final Logger log = Red5LoggerFactory.getLogger(MainService.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;
	@Autowired
	private SessiondataDao sessiondataDao;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private StateDao statemanagement;
	@Autowired
	private RoomManager roomManager;
	@Autowired
	private ConferenceLogDao conferenceLogDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RoomDao roomDao;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private LdapConfigDao ldapConfigDao;
	@Autowired
	private SOAPLoginDao soapLoginDao;
	@Autowired
	private InvitationManager invitationManager;
	@Autowired
	private LdapLoginManagement ldapLoginManagement;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private TimezoneUtil timezoneUtil;

	// External User Types
	public static final String EXTERNAL_USER_TYPE_LDAP = "LDAP";


	/**
	 * gets a user by its SID
	 * 
	 * @param SID
	 * @param USER_ID
	 * @return - user with SID given
	 */
	public User getUser(String SID, int USER_ID) {
		User users = new User();
		Long users_id = sessiondataDao.checkSession(SID);
		Set<Right> rights = userDao.getRights(users_id);
		if (AuthLevelUtil.hasAdminLevel(rights) || AuthLevelUtil.hasWebServiceLevel(rights)) {
			users = userDao.get(USER_ID);
		} else {
			users.setFirstname("No rights to do this");
		}
		return users;
	}

	public Client getCurrentRoomClient(String SID) {
		try {
			IConnection current = Red5.getConnectionLocal();
			String streamid = current.getClient().getId();

			log.debug("getCurrentRoomClient -1- " + SID);
			log.debug("getCurrentRoomClient -2- " + streamid);

			Client currentClient = sessionManager.getClientByStreamId(streamid, null);
			return currentClient;
		} catch (Exception err) {
			log.error("[getCurrentRoomClient]", err);
		}
		return null;
	}

	/**
	 * load this session id before doing anything else
	 * 
	 * @return a unique session identifier
	 */
	public Sessiondata getsessiondata() {
		return sessiondataDao.startsession();
	}

	public Long setCurrentUserOrganization(String SID, Long organization_id) {
		try {
			sessiondataDao.updateUserOrg(SID, organization_id);
			return 1L;
		} catch (Exception err) {
			log.error("[setCurrentUserOrganization]", err);
		}
		return -1L;
	}

	public User loginWicket(String SID, String wicketSID, Long wicketroomid) {
		log.debug("[loginWicket]");
		Long userId = sessiondataDao.checkSession(wicketSID);
		User u = userId == null ? null : userDao.get(userId);
		if (u != null && wicketroomid != null) {
			log.debug("[loginWicket] user and roomid are not empty: " + userId + ", " + wicketroomid);
			boolean allowed = false;
			Room r = roomDao.get(wicketroomid);
			if (r.getAppointment() != null && r.getAppointment()) {
				Appointment a = appointmentDao.getAppointmentByRoom(wicketroomid);
				if (a != null && !a.isDeleted()) {
					allowed = a.getOwner().getUser_id().equals(userId);
					log.debug("[loginWicket] appointed room, isOwner ? " + allowed);
					if (!allowed) {
						for (MeetingMember mm : a.getMeetingMembers()) {
							if (mm.getUser().getUser_id().equals(userId)) {
								allowed = true;
								break;
							}
						}
					}
					/*
					TODO need to be reviewed
					Calendar c = WebSession.getCalendar();
					if (c.getTime().after(a.getStart()) && c.getTime().before(a.getEnd())) {
						allowed = true;
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm"); //FIXME format
						deniedMessage = Application.getString(1271) + String.format(" %s - %s", sdf.format(a.getStart()), sdf.format(a.getEnd()));
					}
					*/
				}
			} else {
				allowed = r.getIspublic() || (r.getOwnerId() != null && r.getOwnerId().equals(userId));
				log.debug("[loginWicket] public ? " + r.getIspublic() + ", ownedId ? " + r.getOwnerId() + " " + allowed);
				if (!allowed) {
					for (RoomOrganisation ro : r.getRoomOrganisations()) {
						for (Organisation_Users ou : u.getOrganisation_users()) {
							if (ro.getOrganisation().getOrganisation_id().equals(ou.getOrganisation().getOrganisation_id())) {
								allowed = true;
								break;
							}
						}
						if (allowed) {
							break;
						}
					}
				}
			}
			if (allowed) {
				IConnection current = Red5.getConnectionLocal();
				String streamId = current.getClient().getId();
				Client currentClient = sessionManager.getClientByStreamId(streamId, null);
				
				if (!u.getOrganisation_users().isEmpty()) {
					u.setSessionData(sessiondataDao.getSessionByHash(wicketSID));
					currentClient.setUser_id(u.getUser_id());
					currentClient.setRoom_id(wicketroomid);
					SessionVariablesUtil.setUserId(current.getClient(), u.getUser_id());
				
					currentClient.setUsername(u.getLogin());
					currentClient.setFirstname(u.getFirstname());
					currentClient.setLastname(u.getLastname());
					currentClient.setPicture_uri(u.getPictureuri());
					sessionManager.updateClientByStreamId(streamId, currentClient, false, null);
					
					scopeApplicationAdapter.sendMessageToCurrentScope("roomConnect", currentClient, false);
					
					return u;
				}
			}
		}
		return null;
	}
	
	public Object secureLoginByRemote(String SID, String secureHash) {
		try {

			log.debug("############### secureLoginByRemote " + secureHash);

			String clientURL = Red5.getConnectionLocal().getRemoteAddress();

			log.debug("swfURL " + clientURL);

			SOAPLogin soapLogin = soapLoginDao.get(secureHash);

			if (soapLogin.getUsed()) {

				if (soapLogin.getAllowSameURLMultipleTimes()) {

					if (!soapLogin.getClientURL().equals(clientURL)) {
						log.debug("does not equal " + clientURL);
						return -42L;
					}

				} else {
					log.debug("Already used " + secureHash);
					return -42L;
				}
			}

			Long loginReturn = loginUserByRemote(soapLogin.getSessionHash());

			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = sessionManager.getClientByStreamId(streamId, null);

			if (currentClient.getUser_id() != null) {
				sessiondataDao.updateUser(SID, currentClient.getUser_id());
			}

			currentClient.setAllowRecording(soapLogin.isAllowRecording());
			sessionManager.updateClientByStreamId(streamId, currentClient, false, null);

			if (loginReturn == null) {
				log.debug("loginReturn IS NULL for SID: " + soapLogin.getSessionHash());

				return -1L;
			} else if (loginReturn < 0) {
				log.debug("loginReturn IS < 0 for SID: " + soapLogin.getSessionHash());

				return loginReturn;
			} else {

				soapLogin.setUsed(true);
				soapLogin.setUseDate(new Date());

				soapLogin.setClientURL(clientURL);

				soapLoginDao.update(soapLogin);

				// Create Return Object and hide the validated
				// sessionHash that is stored server side
				// this hash should be never thrown back to the user

				SOAPLogin returnSoapLogin = new SOAPLogin();

				returnSoapLogin.setRoom_id(soapLogin.getRoom_id());
				returnSoapLogin.setBecomemoderator(soapLogin.getBecomemoderator());
				returnSoapLogin.setShowAudioVideoTest(soapLogin.getShowAudioVideoTest());
				returnSoapLogin.setRoomRecordingId(soapLogin.getRoomRecordingId());
				returnSoapLogin.setShowNickNameDialog(soapLogin.getShowNickNameDialog());
				returnSoapLogin.setLandingZone(soapLogin.getLandingZone());

				return returnSoapLogin;
			}

		} catch (Exception err) {
			log.error("[secureLoginByRemote]", err);
		}
		return null;
	}

	/**
	 * Function is called if the user loggs in via a secureHash and sets the
	 * param showNickNameDialog in the Object SOAPLogin to true the user gets
	 * displayed an additional dialog to enter his nickname
	 * 
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long setUserNickName(String firstname, String lastname, String email) {
		try {

			IConnection current = Red5.getConnectionLocal();
			String streamId = current.getClient().getId();
			Client currentClient = this.sessionManager
					.getClientByStreamId(streamId, null);

			currentClient.setFirstname(firstname);
			currentClient.setLastname(lastname);
			currentClient.setEmail(email);

			// Log the User
			conferenceLogDao.addConferenceLog(
					"nicknameEnter", currentClient.getUser_id(), streamId,
					null, currentClient.getUserip(), currentClient.getScope(),
					currentClient.getExternalUserId(),
					currentClient.getExternalUserType(),
					currentClient.getEmail(), currentClient.getFirstname(),
					currentClient.getLastname());

			this.sessionManager.updateClientByStreamId(streamId,
					currentClient, false, null);

			return 1L;

		} catch (Exception err) {
			log.error("[setUserNickName] ", err);
		}
		return new Long(-1);
	}

	/**
	 * Attention! This SID is NOT the default session id! its the Session id
	 * retrieved in the call from the SOAP Gateway!
	 * 
	 * @param SID
	 * @return - 1 in case of success, -1 otherwise
	 */
	public Long loginUserByRemote(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasWebServiceLevel(userDao.getRights(users_id))) {
				Sessiondata sd = sessiondataDao.getSessionByHash(SID);
				if (sd == null || sd.getSessionXml() == null) {
					return new Long(-37);
				} else {
					RemoteSessionObject userObject = RemoteSessionObject.fromXml(sd.getSessionXml());

					log.debug(userObject.toString());

					IConnection current = Red5.getConnectionLocal();
					String streamId = current.getClient().getId();
					Client currentClient = sessionManager.getClientByStreamId(streamId, null);

					// Check if this User is simulated in the OpenMeetings
					// Database

					if (!Strings.isEmpty(userObject.getExternalUserId())) {
						// If so we need to check that we create this user in
						// OpenMeetings and update its record

						User user = userDao.getExternalUser(userObject.getExternalUserId(), userObject.getExternalUserType());

						if (user == null) {
							String iCalTz = configurationDao.getConfValue("default.timezone", String.class, "");

							Address a = userDao.getAddress(null, null, null, 1L, null, null, null, userObject.getEmail());

							Set<Right> rights = UserDao.getDefaultRights();
							rights.remove(Right.Login);
							rights.remove(Right.Dashboard);
							User u = userDao.addUser(rights, userObject.getFirstname(), userObject.getUsername(),
											userObject.getLastname(), 1L, "" // password is empty by default
											, a, false, null, null, timezoneUtil.getTimeZone(iCalTz), false
											, null, null, false, false, userObject.getExternalUserId()
											, userObject.getExternalUserType(), null, userObject.getPictureUrl());

							long userId = u.getUser_id();
							currentClient.setUser_id(userId);
							SessionVariablesUtil.setUserId(current.getClient(), userId);
						} else {
							user.setPictureuri(userObject.getPictureUrl());

							userDao.update(user, users_id);

							currentClient.setUser_id(user.getUser_id());
							SessionVariablesUtil.setUserId(current.getClient(), user.getUser_id());
						}
					}

					log.debug("userObject.getExternalUserId() -2- " + currentClient.getUser_id());

					currentClient.setUserObject(userObject.getUsername(), userObject.getFirstname(), userObject.getLastname());
					currentClient.setPicture_uri(userObject.getPictureUrl());
					currentClient.setEmail(userObject.getEmail());

					log.debug("UPDATE USER BY STREAMID " + streamId);

					if (currentClient.getUser_id() != null) {
						sessiondataDao.updateUser(SID, currentClient.getUser_id());
					}

					sessionManager.updateClientByStreamId(streamId, currentClient, false, null);

					return new Long(1);
				}
			}
		} catch (Exception err) {
			log.error("[loginUserByRemote] ", err);
		}
		return new Long(-1);
	}

	/**
	 * this function returns a user object with organization objects set only
	 * the organization is not available for users that are using a HASH mechanism
	 * cause the SOAP/REST API does not guarantee that the user connected to the HASH
	 * has a valid user object set
	 * 
	 * @param SID
	 */
	public User markSessionAsLogedIn(String SID) {
		try {
			sessiondataDao.updateUserWithoutSession(SID, -1L);
			
			Long defaultRpcUserid = configurationDao.getConfValue(
					"default.rpc.userid", Long.class, "-1");
			User defaultRpcUser = userDao.get(defaultRpcUserid);
			
			User user = new User();
			user.setOrganisation_users(defaultRpcUser.getOrganisation_users());
			
			return user;
			
		} catch (Exception err) {
			log.error("[markSessionAsLogedIn]", err);
		}
		return null;
	}

	/**
	 * clear this session id
	 * 
	 * @param SID
	 * @return string value if completed
	 */
	public Long logoutUser(String SID) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			IConnection current = Red5.getConnectionLocal();
			Client currentClient = this.sessionManager
					.getClientByStreamId(current.getClient().getId(), null);
			
			scopeApplicationAdapter.roomLeaveByScope(currentClient,current.getScope(), false);
			
			currentClient.setUserObject(null, null, null, null);
			
			return userManager.logout(SID, users_id);
		} catch (Exception err) {
			log.error("[logoutUser]",err);
		}
		return -1L;
	}

	public String[] getTimeZones(int start, int count) {
		String all[] = TimeZone.getAvailableIDs();
		return Arrays.copyOfRange(all, start, Math.min(start + count, all.length));
	}

	public List<Configuration> getGeneralOptions(String SID) {
		try {
			return configurationDao.get("exclusive.audio.keycode", "red5sip.enable", CONFIG_MAX_UPLOAD_SIZE_KEY, "mute.keycode", CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY);
		} catch (Exception err) {
			log.error("[getGeneralOptions]",err);
		}
		return null;
	}

	public List<Userdata> getUserdata(String SID) {
		Long users_id = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
			return userManager.getUserdataDashBoard(users_id);
		}
		return null;
	}

	/**
	 * TODO: Is this function in usage?
	 * 
	 * @deprecated
	 * @param SID
	 * @param domain
	 * @return - empty map
	 */
	@Deprecated
	public LinkedHashMap<Integer, Client> getUsersByDomain(String SID,
			String domain) {
		Long users_id = sessiondataDao.checkSession(SID);
		if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {
			LinkedHashMap<Integer, Client> lMap = new LinkedHashMap<Integer, Client>();
			// Integer counter = 0;
			// for (Iterator<String> it =
			// Application.getClientList().keySet().iterator();it.hasNext();) {
			// RoomClient rc = Application.getClientList().get(it.next());
			// //if (rc.getDomain().equals(domain)) {
			// lMap.put(counter, rc);
			// counter++;
			// //}
			// }
			return lMap;
		} else {
			return null;
		}
	}

	public int closeRoom(String SID, Long room_id, Boolean status) {
		try {
			Long users_id = sessiondataDao.checkSession(SID);
			if (AuthLevelUtil.hasUserLevel(userDao.getRights(users_id))) {

				roomManager.closeRoom(room_id, status);

				if (status) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("message", "roomClosed");
					scopeApplicationAdapter.sendMessageByRoomAndDomain(room_id, message);
				}

				return 1;

			}

			return 1;
		} catch (Exception err) {
			log.error("[closeRoom]", err);
		}
		return -1;
	}

	public void resultReceived(IPendingServiceCall arg0) {
		log.debug("[resultReceived]" + arg0);
	}
}
