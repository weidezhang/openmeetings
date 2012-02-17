package org.openmeetings.jira.plugin.ao.adminconfiguration;

import org.openmeetings.jira.plugin.servlet.AdminServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;


public class OmPluginSettings {
	
	private static final Logger log = LoggerFactory.getLogger(OmPluginSettings.class);
	
    final PluginSettingsFactory pluginSettingsFactory;
 
    public OmPluginSettings(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }
 
    public void storeSomeInfo(String key, String value) {
        // createGlobalSettings is nice and fast, so there's no need to cache it (it's memoised when necessary).
        pluginSettingsFactory.createGlobalSettings().put("my-plugin-namespace" + key, value);
    }
 
    public Object getSomeInfo(String key) {
        return pluginSettingsFactory.createGlobalSettings().get("my-plugin-namespace" + key);
    }
 
    public void storeSomeInfo(String projectKey, String key, String value) {
        // createSettingsForKey is nice and fast, so there's no need to cache it (it's memoised when necessary).
        pluginSettingsFactory.createSettingsForKey(projectKey).put("my-plugin-namespace" + key, value);
    }
 
    public Object getSomeInfo(String projectKey, String key) {
        return pluginSettingsFactory.createSettingsForKey(projectKey).get("my-plugin-namespace" + key);
    }
 
     
}