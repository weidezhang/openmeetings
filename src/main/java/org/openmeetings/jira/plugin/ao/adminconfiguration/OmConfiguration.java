package org.openmeetings.jira.plugin.ao.adminconfiguration;

import net.java.ao.Entity;
import net.java.ao.Preload;
 
@Preload
public interface OmConfiguration extends Entity
{
    String getDescription();
 
    void setDescription(String description);
 
    boolean isComplete();
 
    void setComplete(boolean complete);
}