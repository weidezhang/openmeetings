package org.openmeetings.jira.plugin.ao.omrooms;

import net.java.ao.Entity;
import net.java.ao.Preload;
 
@Preload
public interface Room extends Entity
{
    String getName();
 
    void setName(String name);
    
    Long getNumberOfParticipent();
    
    void setNumberOfParticipent(Long numberOfParticipent);
    
    Long getRoomType();
    
    void setRoomType(Long roomType);
 
    boolean getIsModeratedRoom();
 
    void setIsModeratedRoom(boolean isModeratedRoom);
    
    boolean getIsAudioOnly();
    
    void setIsAudioOnly(boolean isAudioOnly);
    
    boolean getIsAllowedRecording();
    
    void setIsAllowedRecording(boolean isAllowedRecording);
    
    boolean getIsDeleted();
    
    void setIsDeleted(boolean isDeleted);
    
    Long getRoomId();
    
    void setRoomId(Long roomId);
    
    Integer getCreatedByUserId();
    
    void setCreatedByUserId(Long createdByUserId);
}
