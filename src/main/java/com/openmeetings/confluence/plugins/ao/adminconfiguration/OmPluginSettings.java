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
package com.openmeetings.confluence.plugins.ao.adminconfiguration;

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
        pluginSettingsFactory.createGlobalSettings().put("openmeetings:" + key, value);
    }
 
    public Object getSomeInfo(String key) {
        return pluginSettingsFactory.createGlobalSettings().get("openmeetings:" + key);
    }
 
    public void storeSomeInfo(String projectKey, String key, String value) {
        // createSettingsForKey is nice and fast, so there's no need to cache it (it's memoised when necessary).
        pluginSettingsFactory.createSettingsForKey(projectKey).put("openmeetings:" + key, value);
    }
 
    public Object getSomeInfo(String projectKey, String key) {
        return pluginSettingsFactory.createSettingsForKey(projectKey).get("openmeetings:" + key);
    }
 
     
}