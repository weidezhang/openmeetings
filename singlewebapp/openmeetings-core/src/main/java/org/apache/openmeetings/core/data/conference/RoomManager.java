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
package org.apache.openmeetings.core.data.conference;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.openmeetings.db.dao.room.IRoomManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomModeratorsDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.room.SipDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.SearchResult;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author swagner
 * 
 */
@Transactional
public class RoomManager implements IRoomManager {
	private static final Logger log = Red5LoggerFactory.getLogger(RoomManager.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private OrganisationDao orgDao;
	@Autowired
	private RoomModeratorsDao roomModeratorsDao;
	@Autowired
	private UserDao usersDao;
	@Autowired
	private ISessionManager sessionManager;
    @Autowired
	private RoomDao roomDao;
    @Autowired
	private SipDao sipDao;
    @Autowired
	private RoomTypeDao roomTypeDao;

	/**
	 * Get a Rooms-Object or NULL
	 * 
	 * @param externalRoomId
	 * @return Rooms-Object or NULL
	 */
	public Room getRoomByExternalId(Long externalRoomId, String externalRoomType, long roomtypesId) {
		log.debug("getRoombyExternalId : " + externalRoomId + " - " + externalRoomType + " - " + roomtypesId);
		try {
			TypedQuery<Room> query = em.createNamedQuery("getRoomByExternalId", Room.class);
			query.setParameter("externalRoomId", externalRoomId);
			query.setParameter("externalRoomType", externalRoomType);
			query.setParameter("roomtypesId", roomtypesId);
			List<?> ll = query.getResultList();
			if (ll.size() > 0) {
				return (Room) ll.get(0);
			} else {
				log.error("Could not find room " + externalRoomId);
			}
		} catch (Exception ex2) {
			log.error("[getRoomByExternalId] ", ex2);
		}
		return null;
	}

	public SearchResult<Room> getRooms(int start, int max, String orderby, boolean asc, String search) {
		try {
			SearchResult<Room> sResult = new SearchResult<Room>();
			sResult.setRecords(this.selectMaxFromRooms(search));
			sResult.setObjectName(Room.class.getName());
			sResult.setResult(this.getRoomsInternatlByHQL(start, max,
					orderby, asc, search));
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}
	
	public SearchResult<Room> getRoomsWithCurrentUsers(int start, int max, String orderby, boolean asc) {
		try {
			SearchResult<Room> sResult = new SearchResult<Room>();
			sResult.setRecords(this.selectMaxFromRooms(""));
			sResult.setObjectName(Room.class.getName());

			List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
					asc);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			sResult.setResult(rooms);
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByList(int start, int max, String orderby, boolean asc) {
		try {
			List<Room> rooms = this.getRoomsInternatl(start, max, orderby,
					asc);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			return rooms;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsWithCurrentUsersByListAndType(int start, int max, String orderby, boolean asc, String externalRoomType) {
		try {
			List<Room> rooms = this.getRoomsInternatlbyType(start, max,
					orderby, asc, externalRoomType);

			for (Room room : rooms) {
				room.setCurrentusers(sessionManager.getClientListByRoom(room.getId()));
			}

			return rooms;
		} catch (Exception ex2) {
			log.error("[getRooms] ", ex2);
		}
		return null;
	}

	public Long selectMaxFromRooms(String search) {
		try {
			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			// get all users
			TypedQuery<Long> query = em.createNamedQuery("selectMaxFromRooms", Long.class);
			query.setParameter("search", search);
			List<Long> ll = query.getResultList();
			log.debug("Number of records" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatl(int start, int max, String orderby, boolean asc) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	/**
	 * gets a list of all availible rooms
	 * 
	 * @param user_level
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	public List<Room> getRoomsInternatlByHQL(int start, int max,
			String orderby, boolean asc, String search) {
		try {

			String hql = "select c from Room c where c.deleted = false AND c.name LIKE :search ";

			if (search.length() == 0) {
				search = "%";
			} else {
				search = "%" + search + "%";
			}
			if (orderby != null) {
				hql += " ORDER BY " + (orderby.startsWith("c.") ? "" : "c.") + orderby;
	
				if (asc) {
					hql += " ASC";
				} else {
					hql += " DESC";
				}
			}
			TypedQuery<Room> query = em.createQuery(hql, Room.class);
			query.setParameter("search", search);
			query.setFirstResult(start);
			query.setMaxResults(max);

			return query.getResultList();

		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<Room> getAllRooms() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			cq.where(condition);
			TypedQuery<Room> q = em.createQuery(cq);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getAllRooms]", ex2);
		}
		return null;
	}

	public List<Room> getRoomsInternatlbyType(int start, int max,
			String orderby, boolean asc, String externalRoomType) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Room> cq = cb.createQuery(Room.class);
			Root<Room> c = cq.from(Room.class);
			Predicate condition = cb.equal(c.get("deleted"), false);
			Predicate subCondition = cb.equal(c.get("externalRoomType"),
					externalRoomType);
			cq.where(condition, subCondition);
			cq.distinct(asc);
			if (asc) {
				cq.orderBy(cb.asc(c.get(orderby)));
			} else {
				cq.orderBy(cb.desc(c.get(orderby)));
			}
			TypedQuery<Room> q = em.createQuery(cq);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<Room> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getRooms ] ", ex2);
		}
		return null;
	}

	public List<RoomOrganisation> getOrganisationsByRoom(long roomId) {
		try {
			String hql = "select c from RoomOrganisation as c "
					+ "where c.room.id = :id "
					+ "AND c.deleted = false";
			TypedQuery<RoomOrganisation> q = em.createQuery(hql, RoomOrganisation.class);

			q.setParameter("id", roomId);
			List<RoomOrganisation> ll = q.getResultList();
			return ll;
		} catch (Exception ex2) {
			log.error("[getOrganisationsByRoom] ", ex2);
		}
		return null;
	}

	public List<Room> getRoomsByIds(List<Integer> roomIds) {
		try {
			if (roomIds == null || roomIds.size() == 0) {
				return new LinkedList<Room>();
			}

			String queryString = "SELECT r from Room r " + "WHERE ";

			queryString += "(";

			int i = 0;
			for (Integer roomId : roomIds) {
				if (i != 0) {
					queryString += " OR ";
				}
				queryString += " r.id = " + roomId;
				i++;
			}

			queryString += ")";

			TypedQuery<Room> q = em.createQuery(queryString, Room.class);

			List<Room> ll = q.getResultList();

			return ll;

		} catch (Exception ex2) {
			log.error("[getRoomsByIds] ", ex2);
		}
		return null;
	}

	// ---------------------------------------------------------------------------------------------

    /**
     * Returns number of SIP conference participants
     * @param roomId id of room
     * @return number of participants
     */
    public Integer getSipConferenceMembersNumber(Long roomId) {
    	Room r = roomDao.get(roomId);
    	return r == null || r.getConfno() == null ? null : sipDao.countUsers(r.getConfno());
    }

    private List<RoomModerator> getModerators(List<Map<String, Object>> list, Long roomId) {
		List<RoomModerator> result = new ArrayList<RoomModerator>();

		for (Map<String, Object> roomModeratorObj : list) {
			Long roomModeratorsId = roomModeratorObj.containsKey("roomModeratorsId") ? Long.parseLong(roomModeratorObj.get("roomModeratorsId").toString()) : null;
			Long userId = Long.parseLong(roomModeratorObj.get("userId").toString());
			Boolean isSuperModerator = Boolean.parseBoolean(roomModeratorObj.get("isSuperModerator").toString());

			RoomModerator rm = new RoomModerator();
			rm.setId(roomModeratorsId);
			rm.setRoomId(roomId);
			rm.setUser(usersDao.get(userId));
			rm.setSuperModerator(isSuperModerator);
			
			result.add(rm);
		}
		return result;
    }

	/**
	 * adds a new Record to the table rooms
	 * @param name
	 * @param roomtypesId
	 * @param ispublic
	 * @param hideActivitiesAndActions TODO
	 * @param hideFilesExplorer TODO
	 * @param hideActionsMenu TODO
	 * @param hideScreenSharing TODO
	 * @param hideWhiteboard TODO
	 * @return id of the newly created room or NULL
	 */
	public Long addRoom(String name, long roomtypesId,
			String comment, Long numberOfPartizipants, boolean ispublic,
			List<Integer> organisations, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			List<Map<String, Object>> roomModerators,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, String conferencePin,
			Long ownerId, Boolean waitForRecording, boolean allowRecording,
			Boolean hideTopBar, Boolean hideChat, Boolean hideActivitiesAndActions, Boolean hideFilesExplorer, 
			Boolean hideActionsMenu, Boolean hideScreenSharing, Boolean hideWhiteboard,
			Boolean showMicrophoneStatus, Boolean chatModerated, boolean chatOpened
			, boolean filesOpened, boolean autoVideoSelect, boolean sipEnabled) {

		try {
			Room r = new Room();
			r.setName(name);
			r.setComment(comment);
			r.setInserted(new Date());
			r.setNumberOfPartizipants(numberOfPartizipants);
			r.setRoomtype(roomTypeDao.get(roomtypesId));
			r.setIspublic(ispublic);
			r.setAllowUserQuestions(allowUserQuestions);
			r.setAudioOnly(isAudioOnly);
			r.setAllowFontStyles(allowFontStyles);

			r.setAppointment(appointment);

			r.setIsDemoRoom(isDemoRoom);
			r.setDemoTime(demoTime);

			r.setModerated(isModeratedRoom);
			r.setHideTopBar(hideTopBar);

			r.setDeleted(false);

			r.setIsClosed(isClosed);
			r.setRedirectURL(redirectURL);

			r.setOwnerId(ownerId);

			r.setWaitForRecording(waitForRecording);
			r.setAllowRecording(allowRecording);
			
			r.setChatHidden(hideChat);
			r.setActivitiesHidden(hideActivitiesAndActions);
			r.setHideActionsMenu(hideActionsMenu);
			r.setHideFilesExplorer(hideFilesExplorer);
			r.setHideScreenSharing(hideScreenSharing);	
			r.setHideWhiteboard(hideWhiteboard);
			r.setShowMicrophoneStatus(showMicrophoneStatus);
			r.setChatModerated(chatModerated);
			r.setChatOpened(chatOpened);
			r.setFilesOpened(filesOpened);
			r.setAutoVideoSelect(autoVideoSelect);
			r.setSipEnabled(sipEnabled);
			r.setPin(conferencePin);
			
			r = roomDao.update(r, ownerId);

			if (organisations != null) {
				Long t = this.updateRoomOrganisations(organisations, r);
				if (t == null) {
					return null;
				}
			}

			if (roomModerators != null) {
				r.setModerators(getModerators(roomModerators, r.getId()));
				r = roomDao.update(r, ownerId);
			}

			return r.getId();
		} catch (Exception ex2) {
			log.error("[addRoom] ", ex2);
		}
		return null;
	}

	/**
	 * adds/check a new Record to the table rooms with external fields
	 * 
	 * @param name
	 * @param roomtypesId
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param organisations
	 * @param appointment
	 * @param isDemoRoom
	 * @param demoTime
	 * @param isModeratedRoom
	 * @param roomModerators
	 * @param externalRoomId
	 * @param externalRoomType
	 * @param allowUserQuestions
	 * @param isAudioOnly
	 * @param allowFontStyles
	 * @param isClosed
	 * @param redirectURL
	 * @param waitForRecording
	 * @param allowRecording
	 * @param hideTopBar
	 * @return id of (the newly created) room or NULL
	 */
	public Long addExternalRoom(String name, long roomtypesId, String comment,
			Long numberOfPartizipants, boolean ispublic, List<Integer> organisations,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, List<Map<String, Object>> roomModerators,
			Long externalRoomId, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, Boolean waitForRecording,
			boolean allowRecording, Boolean hideTopBar) {

		log.debug("addExternalRoom");

		try {
			Room r = new Room();
			r.setName(name);
			r.setComment(comment);
			r.setInserted(new Date());
			r.setNumberOfPartizipants(numberOfPartizipants);
			r.setRoomtype(roomTypeDao.get(roomtypesId));
			r.setIspublic(ispublic);

			r.setAllowUserQuestions(allowUserQuestions);
			r.setAudioOnly(isAudioOnly);
			r.setAllowFontStyles(allowFontStyles);

			r.setAppointment(appointment);

			r.setIsDemoRoom(isDemoRoom);
			r.setDemoTime(demoTime);

			r.setModerated(isModeratedRoom);

			r.setDeleted(false);

			r.setExternalRoomId(externalRoomId);
			r.setExternalRoomType(externalRoomType);

			r.setIsClosed(isClosed);
			r.setRedirectURL(redirectURL);

			r.setWaitForRecording(waitForRecording);
			r.setAllowRecording(allowRecording);

			r.setHideTopBar(hideTopBar);

			r = em.merge(r);

			long returnId = r.getId();

			if (organisations != null) {
				Long t = this.updateRoomOrganisations(organisations, r);
				if (t == null) {
					return null;
				}
			}

			if (roomModerators != null) {
				r.setModerators(getModerators(roomModerators, r.getId()));
				r = roomDao.update(r, null);
			}

			return returnId;
		} catch (Exception ex2) {
			log.error("[addExternalRoom] ", ex2);
		}
		return null;
	}

	/**
	 * adds a new record to the table rooms_organisation
	 * 
	 * @param roomId
	 * @param organisationId
	 * @return the id of the newly created Rooms_Organisation or NULL
	 */
	public Long addRoomToOrganisation(long roomId, long organisationId) {
		try {
			RoomOrganisation rOrganisation = new RoomOrganisation();
			rOrganisation.setRoom(roomDao.get(roomId));
			log.debug("addRoomToOrganisation rooms '"
					+ rOrganisation.getRoom().getName() + "'");
			rOrganisation.setInserted(new Date());
			rOrganisation.setOrganisation(orgDao.get(organisationId));
			rOrganisation.setDeleted(false);

			rOrganisation = em.merge(rOrganisation);
			long returnId = rOrganisation.getId();
			return returnId;
		} catch (Exception ex2) {
			log.error("[addRoomToOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * get List of Rooms_Organisation by organisation and roomtype
	 * 
	 * @param organisationId
	 * @param roomtypesId
	 * @return
	 */
	public List<RoomOrganisation> getRoomsOrganisationByOrganisationIdAndRoomType(long organisationId, long roomtypesId) {
		try {
			TypedQuery<RoomOrganisation> q = em.createNamedQuery("getRoomsOrganisationByOrganisationIdAndRoomType", RoomOrganisation.class);
			q.setParameter("roomtypesId", roomtypesId);
			q.setParameter("organisationId", organisationId);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * Gets all rooms by an organisation
	 * 
	 * @param organisationId
	 * @return list of Rooms_Organisation with Rooms as Sub-Objects or null
	 */
	public List<RoomOrganisation> getRoomsOrganisationByOrganisationId(long organisationId) {
		try {
			TypedQuery<RoomOrganisation> query = em.
					createNamedQuery("getRoomsOrganisationByOrganisationId", RoomOrganisation.class);

			query.setParameter("organisationId", organisationId);

			List<RoomOrganisation> ll = query.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getPublicRoomsWithoutType] ", ex2);
		}
		return null;
	}

	public SearchResult<RoomOrganisation> getRoomsOrganisationsByOrganisationId(long organisationId, int start, int max, String orderby,
			boolean asc) {
		try {
			SearchResult<RoomOrganisation> sResult = new SearchResult<RoomOrganisation>();
			sResult.setObjectName(RoomOrganisation.class.getName());
			sResult.setRecords(this.selectMaxFromRoomsByOrganisation(organisationId).longValue());
			sResult.setResult(getRoomsOrganisationByOrganisationId(organisationId, start, max, orderby, asc));
			return sResult;
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public Integer selectMaxFromRoomsByOrganisation(long organisationId) {
		try {
			// get all users
			TypedQuery<RoomOrganisation> q = em.createNamedQuery("selectMaxFromRoomsByOrganisation", RoomOrganisation.class);

			q.setParameter("organisationId", organisationId);
			List<RoomOrganisation> ll = q.getResultList();

			return ll.size();
		} catch (Exception ex2) {
			log.error("[selectMaxFromRooms] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param organisationId
	 * @param start
	 * @param max
	 * @param orderby
	 * @param asc
	 * @return
	 */
	private List<RoomOrganisation> getRoomsOrganisationByOrganisationId(long organisationId, int start, int max, String orderby,
			boolean asc) {
		try {
			String hql = "select c from RoomOrganisation as c where c.organisation.id = :organisationId AND c.deleted = false";
			if (orderby.startsWith("c.")) {
				hql += "ORDER BY " + orderby;
			} else {
				hql += "ORDER BY " + "c." + orderby;
			}
			if (asc) {
				hql += " ASC";
			} else {
				hql += " DESC";
			}

			TypedQuery<RoomOrganisation> q = em.createQuery(hql, RoomOrganisation.class);

			q.setParameter("organisationId", organisationId);
			q.setFirstResult(start);
			q.setMaxResults(max);
			List<RoomOrganisation> ll = q.getResultList();

			return ll;
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public RoomOrganisation getRoomsOrganisationByOrganisationIdAndRoomId(long organisationId, long roomId) {
		try {
			TypedQuery<RoomOrganisation> q = em.
					createNamedQuery("getRoomsOrganisationByOrganisationIdAndRoomId", RoomOrganisation.class);

			q.setParameter("roomId", roomId);
			q.setParameter("organisationId", organisationId);
			List<RoomOrganisation> ll = q.getResultList();

			if (ll.size() > 0) {
				return ll.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getRoomsOrganisationByOrganisationIdAndRoomId] ", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @param roomId
	 * @return
	 */
	public List<RoomOrganisation> getRoomsOrganisationByRoomsId(long roomId) {
		try {
			TypedQuery<RoomOrganisation> q = em.createNamedQuery("getRoomsOrganisationByRoomsId", RoomOrganisation.class);
			q.setParameter("roomId", roomId);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getRoomsByOrganisation] ", ex2);
		}
		return null;
	}

	public Long updateRoomInternal(long roomId, long roomtypesId,
			String name, boolean ispublic, String comment,
			Long numberOfPartizipants, List<Integer> organisations,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, List<Map<String, Object>> roomModerators,
			Boolean allowUserQuestions, Boolean isAudioOnly, Boolean allowFontStyles, Boolean isClosed,
			String redirectURL, String conferencePin,
			Long ownerId, Boolean waitForRecording, boolean allowRecording,
			Boolean hideTopBar, Boolean hideChat, Boolean hideActivitiesAndActions, Boolean hideFilesExplorer, 
			Boolean hideActionsMenu, Boolean hideScreenSharing, Boolean hideWhiteboard, 
			Boolean showMicrophoneStatus, Boolean chatModerated, boolean chatOpened, boolean filesOpened
			, boolean autoVideoSelect, boolean sipEnabled) {
		try {
			log.debug("*** updateRoom numberOfPartizipants: "
					+ numberOfPartizipants);
			Room r = roomDao.get(roomId);
			r.setComment(comment);

			r.setIspublic(ispublic);
			r.setNumberOfPartizipants(numberOfPartizipants);
			r.setName(name);
			r.setRoomtype(roomTypeDao.get(roomtypesId));
			r.setUpdated(new Date());
			r.setAllowUserQuestions(allowUserQuestions);
			r.setAudioOnly(isAudioOnly);
			r.setAllowFontStyles(allowFontStyles);

			r.setIsDemoRoom(isDemoRoom);
			r.setDemoTime(demoTime);

			r.setAppointment(appointment);

			r.setModerated(isModeratedRoom);
			r.setHideTopBar(hideTopBar);

			r.setIsClosed(isClosed);
			r.setRedirectURL(redirectURL);
			r.setOwnerId(ownerId);
			r.setWaitForRecording(waitForRecording);
			r.setAllowRecording(allowRecording);
			
			r.setChatHidden(hideChat);
			r.setActivitiesHidden(hideActivitiesAndActions);
			r.setHideActionsMenu(hideActionsMenu);
			r.setHideFilesExplorer(hideFilesExplorer);
			r.setHideScreenSharing(hideScreenSharing);
			r.setHideWhiteboard(hideWhiteboard);
			r.setShowMicrophoneStatus(showMicrophoneStatus);
			r.setChatModerated(chatModerated);
			r.setChatOpened(chatOpened);
			r.setFilesOpened(filesOpened);
			r.setAutoVideoSelect(autoVideoSelect);
			r.setPin(conferencePin);
			r.setSipEnabled(sipEnabled);
			r = roomDao.update(r, ownerId);

			if (organisations != null) {
				Long t = this.updateRoomOrganisations(organisations, r);
				if (t == null) {
					return null;
				}
			}
			if (roomModerators != null) {
				r.setModerators(getModerators(roomModerators, r.getId()));
				r = roomDao.update(r, null);
			}

			return r.getId();
		} catch (Exception ex2) {
			log.error("[updateRoom] ", ex2);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private boolean checkRoomAlreadyInOrg(Long orgid, List organisations)
			throws Exception {
		for (Iterator it = organisations.iterator(); it.hasNext();) {
			RoomOrganisation rOrganisation = (RoomOrganisation) it.next();
			if (rOrganisation.getOrganisation().getId()
					.equals(orgid))
				return true;
		}
		return false;
	}

	private boolean checkRoomShouldByDeleted(long orgId, List<Integer> organisations) throws Exception {
		for (Iterator<Integer> it = organisations.iterator(); it.hasNext();) {
			Integer key = it.next();
			Long storedOrgId = key.longValue();
			if (storedOrgId.equals(orgId))
				return true;
		}
		return false;
	}

	private Long updateRoomOrganisations(List<Integer> organisations, Room room) throws Exception {
		List<RoomOrganisation> roomOrganisations = getOrganisationsByRoom(room.getId());

		List<Long> roomsToAdd = new LinkedList<Long>();
		List<Long> roomsToDel = new LinkedList<Long>();

		for (Iterator<Integer> it = organisations.iterator(); it.hasNext();) {
			Integer key = it.next();
			Long orgIdToAdd = key.longValue();
			if (!this.checkRoomAlreadyInOrg(orgIdToAdd, roomOrganisations))
				roomsToAdd.add(orgIdToAdd);
		}

		for (Iterator<RoomOrganisation> it = roomOrganisations.iterator(); it.hasNext();) {
			RoomOrganisation rOrganisation = it.next();
			Long orgIdToDel = rOrganisation.getOrganisation()
					.getId();
			if (!this.checkRoomShouldByDeleted(orgIdToDel, organisations))
				roomsToDel.add(orgIdToDel);
		}

		// log.error("updateRoomOrganisations roomsToAdd: "+roomsToAdd.size());
		// log.error("updateRoomOrganisations roomsToDel: "+roomsToDel.size());

		for (Iterator<Long> it = roomsToAdd.iterator(); it.hasNext();) {
			Long orgIdToAdd = it.next();
			addRoomToOrganisation(room.getId(), orgIdToAdd);
		}
		for (Iterator<Long> it = roomsToDel.iterator(); it.hasNext();) {
			Long orgToDel = it.next();
			deleteRoomFromOrganisationByRoomAndOrganisation(room.getId(), orgToDel);
		}

		return new Long(1);
	}

	/**
	 * delete all Rooms_Organisations and Room by a given roomId
	 * 
	 * @param roomId
	 */
	public Long deleteRoomById(long roomId) {
		try {
			deleteAllRoomsOrganisationOfRoom(roomId);
			roomDao.delete(roomDao.get(roomId), -1L);
			return roomId;
		} catch (Exception ex2) {
			log.error("[deleteRoomById] ", ex2);
		}
		return null;
	}

	/**
	 * delete all Rooms_Organisation by a roomId
	 * 
	 * @param roomId
	 */
	@SuppressWarnings("rawtypes")
	public void deleteAllRoomsOrganisationOfRoom(long roomId) {
		try {
			List ll = this.getRoomsOrganisationByRoomsId(roomId);
			for (Iterator it = ll.iterator(); it.hasNext();) {
				RoomOrganisation rOrg = (RoomOrganisation) it.next();
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOrganisationOfRoom] ", ex2);
		}
	}

	/**
	 * Delete all room of a given Organisation
	 * 
	 * @param organisationId
	 */
	public void deleteAllRoomsOrganisationOfOrganisation(long organisationId) {
		try {
			for (RoomOrganisation rOrg : getRoomsOrganisationByOrganisationId(organisationId)) {
				this.deleteRoomsOrganisation(rOrg);
			}
		} catch (Exception ex2) {
			log.error("[deleteAllRoomsOfOrganisation] ", ex2);
		}
	}

	private Long deleteRoomFromOrganisationByRoomAndOrganisation(long roomId, long organisationId) {
		try {
			RoomOrganisation rOrganisation = getRoomsOrganisationByOrganisationIdAndRoomId(organisationId, roomId);
			return this.deleteRoomsOrganisation(rOrganisation);
		} catch (Exception ex2) {
			log.error("[deleteRoomFromOrganisationByRoomAndOrganisation] ", ex2);
		}
		return null;
	}

	/**
	 * delete a Rooms_Organisation-Object
	 * 
	 * @param rOrg
	 */
	public Long deleteRoomsOrganisation(RoomOrganisation rOrg) {
		try {
			rOrg.setDeleted(true);
			rOrg.setUpdated(new Date());
			if (rOrg.getId() == null) {
				em.persist(rOrg);
			} else {
				if (!em.contains(rOrg)) {
					em.merge(rOrg);
				}
			}
			return rOrg.getId();
		} catch (Exception ex2) {
			log.error("[deleteRoomsOrganisation] ", ex2);
		}
		return null;
	}

	// --------------------------------------------------------------------------------------------

	public void closeRoom(Long roomId, Boolean status) {
		try {

			Room room = roomDao.get(roomId);

			room.setIsClosed(status);

			roomDao.update(room, -1L);

		} catch (Exception e) {
			log.error("Error updateRoomObject : ", e);
		}
	}

	/**
	 * Get a Rooms-Object or NULL
	 * 
	 * @param ownerId
	 * @param roomtypesId
	 * @param roomName
	 * @return Rooms-Object or NULL
	 */
	public Room getRoomByOwnerAndTypeId(Long ownerId, Long roomtypesId, String roomName) {
		try {

			if (roomtypesId == null || roomtypesId == 0) {
				return null;
			}
			log.debug("getRoomByOwnerAndTypeId : " + ownerId + " || " + roomtypesId);
			Room room = null;
			TypedQuery<Room> query = em.createNamedQuery("getRoomByOwnerAndTypeId", Room.class);
			query.setParameter("ownerId", ownerId);
			query.setParameter("roomtypesId", roomtypesId);
			List<Room> ll = query.getResultList();
			if (ll.size() > 0) {
				room = ll.get(0);
			}

			if (room != null) {
				return room;
			} else {
				log.debug("Could not find room " + ownerId + " || " + roomtypesId);
				
				Long roomId = addRoom(roomName, roomtypesId,
						"My Rooms of ownerId " + ownerId,
						(roomtypesId == 1) ? 25L : 150L, // numberOfPartizipants
						false, // ispublic
						null, // organisations
						false, // appointment
						false, // isDemoRoom
						null, // demoTime
						false, // isModeratedRoom
						null, // roomModerators
						true, // allowUserQuestions
						false, // isAudioOnly
						true, // allowFontStyle
						false, // isClosed
						"", // redirectURL
						"", // conferencePin
						ownerId, null,
						true,  // allowRecording 
						false, // hideTopBar
						false, // hideChat
						false, // hideActivitiesAndActions
						false, // hideFilesExplorer
						false, // hideActionsMenu
						false, // hideScreenSharing 
						false, // hideWhiteboard
						false, //showMicrophoneStatus
						false, // chatModerated
						false, //chatOpened
						false, //filesOpened
						false, //autoVideoSelect
						false //sipEnabled
						);

				if (roomId != null) {
					return roomDao.get(roomId);
				}
			}
		} catch (Exception ex2) {
			log.error("[getRoomByOwnerAndTypeId] ", ex2);
		}
		return null;
	}

}
