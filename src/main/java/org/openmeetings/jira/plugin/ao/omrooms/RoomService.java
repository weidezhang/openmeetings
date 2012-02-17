package org.openmeetings.jira.plugin.ao.omrooms;

import com.atlassian.activeobjects.tx.Transactional;

import java.util.List;

@Transactional
public interface RoomService
{
    Room add(boolean isAllowedRecording, boolean isAudioOnly, boolean isModeratedRoom,
    		String name, Long numberOfParticipent, Long roomType);
    
    Room update(Integer id, boolean isAllowedRecording, boolean isAudioOnly, boolean isModeratedRoom,
    		String name, Long numberOfParticipent, Long roomType);
    
    Room delete(Integer id);
    
    Room getRoom(Integer id);

    List<Room> all();
}