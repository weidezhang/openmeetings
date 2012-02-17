package org.openmeetings.jira.plugin.ao.adminconfiguration;

import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

@Preload
public abstract interface AdminConfiguration extends RawEntity<String>
{
  @PrimaryKey("identifier")
  @NotNull
  public abstract String getIdentifier();

  public abstract void setIdentifier(String paramString);

  public abstract String getOmUrl();

  public abstract String getOmPort();

  public abstract String getOmUserPass();

  public abstract String getOmUserName();

  public abstract String getOmKey();

  public abstract void setOmUrl(String paramString);

  public abstract void setOmPort(String paramString);

  public abstract void setOmUserPass(String paramString);

  public abstract void setOmUserName(String paramString);

  public abstract void setOmKey(String paramString);

}
