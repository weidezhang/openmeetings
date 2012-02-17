 package org.openmeetings.jira.plugin.ao.adminconfiguration;
 
 import com.atlassian.activeobjects.external.ActiveObjects;
 import com.google.common.base.Preconditions;
 import com.google.common.collect.ImmutableList;
 import com.google.common.collect.ImmutableMap;
 import java.util.List;
 
 public class AdminConfigurationServiceImpl
   implements AdminConfigurationService
 {
   private final ActiveObjects ao;
 
   public AdminConfigurationServiceImpl(ActiveObjects ao)
   {
     this.ao = ((ActiveObjects)Preconditions.checkNotNull(ao));
   }
   
   //@Override
   public AdminConfiguration saveConfiguration(String omUrl, String omPort, String key, 
		   String omUserPass, String omUserName, String omKey)
   {
     AdminConfiguration adminConfiguration = get(key);
     if (adminConfiguration == null) {
       adminConfiguration = (AdminConfiguration)this.ao.create(AdminConfiguration.class, 
    		   						ImmutableMap.of("identifier", (Object)createIdentifierString(key)));
     }
     adminConfiguration.setOmUrl(omUrl);
     adminConfiguration.setOmPort(omPort);
     adminConfiguration.setOmUserName(omUserName);
     adminConfiguration.setOmUserPass(omUserPass);
     adminConfiguration.setOmKey(omKey);
     
     adminConfiguration.save();
     return adminConfiguration;
   }
 
   public List<AdminConfiguration> all()
   {
     AdminConfiguration[] result = (AdminConfiguration[])this.ao.find(AdminConfiguration.class);
     return ImmutableList.of(result);
   }
 
   private String createIdentifierString(String key) {
     return key;
   }
 
   public AdminConfiguration get(String key) {
     return (AdminConfiguration)this.ao.get(AdminConfiguration.class, createIdentifierString(key));
   }
			
 }

