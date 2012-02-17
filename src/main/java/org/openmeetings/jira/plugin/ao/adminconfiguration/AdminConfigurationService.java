package org.openmeetings.jira.plugin.ao.adminconfiguration;

import com.atlassian.activeobjects.tx.Transactional;
import java.util.List;

@Transactional
public abstract interface AdminConfigurationService
{
  public abstract AdminConfiguration saveConfiguration(String paramString1, String paramString2, 
		  String paramString3, String paramString4, String paramString5, String paramString6);

  public abstract List<AdminConfiguration> all();

  public abstract AdminConfiguration get(String paramString);
}
