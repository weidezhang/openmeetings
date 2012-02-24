package org.openmeetings.jira.plugin.ao.omrooms;

import com.atlassian.activeobjects.external.ActiveObjects;

import java.util.List;

import net.java.ao.Query;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public final class RoomServiceImpl implements RoomService
{
    private final ActiveObjects ao;

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
        return newArrayList(ao.find(Room.class, Query.select().where("IS_DELETED LIKE ?", false).limit(10)));
    }
    
    @Override
	public List<Room> allNotDeletedByUserName(String userName) {		
	 
	 	return newArrayList(ao.find(Room.class, Query.select().
				 where("IS_DELETED LIKE ? AND CREATED_BY_USER_NAME LIKE ?",false, userName.toString()).limit(1000)));	
		
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
