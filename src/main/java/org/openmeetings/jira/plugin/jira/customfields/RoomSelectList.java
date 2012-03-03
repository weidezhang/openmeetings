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
package org.openmeetings.jira.plugin.jira.customfields;

import org.openmeetings.jira.plugin.ao.omrooms.Room;
import org.openmeetings.jira.plugin.ao.omrooms.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.atlassian.jira.issue.context.JiraContextNode;
//import com.atlassian.jira.issue.customfields.converters.SelectConverter;
//import com.atlassian.jira.issue.customfields.converters.StringConverter;
//import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
//import com.atlassian.jira.issue.customfields.manager.OptionsManager;
//import com.atlassian.jira.issue.customfields.option.Options;
//import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
//import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
//import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
//import com.atlassian.jira.issue.Issue;
//import com.atlassian.jira.issue.fields.CustomField;
//import com.atlassian.jira.issue.fields.config.FieldConfig;
//import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

import java.util.List;
import java.util.Map;

//import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.util.ErrorCollection;



import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

	public class RoomSelectList extends AbstractSingleFieldType<Integer> {
		
	    private static final Logger log = LoggerFactory.getLogger(RoomSelectList.class);
	    
		private final RoomService roomService;
 	    
		public RoomSelectList(CustomFieldValuePersister customFieldValuePersister,
	            GenericConfigManager genericConfigManager, RoomService roomService) {
	        super(customFieldValuePersister, genericConfigManager);
	        this.roomService = roomService;
	    }  
	
	@Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem)
     {
		
		User currentUser = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);
                        
        // This method is also called to get the default value, in
        // which case issue is null so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }        
         //FieldConfig fieldConfig = field.getRelevantConfig(issue);                  
         //List<Room> rooms =  roomService.allNotDeleted();   
        if(currentUser != null){
        	//List<Room> rooms =  roomService.allNotDeleted();   
         List<Room> rooms =  roomService.allNotDeletedByUserName(currentUser.getName());
         map.put("rooms", rooms); 
        }        
         return map;
    }
    
    @Override
    protected PersistenceFieldType getDatabaseType()
    {
        return PersistenceFieldType.TYPE_LIMITED_TEXT;
    } 
    
 
    @Override
    protected Integer getObjectFromDbValue(final Object databaseValue)
            throws FieldValidationException
    {
        return getSingularObjectFromString((String) databaseValue);
    }
 
    @Override
    public Integer getSingularObjectFromString(final String string)
            throws FieldValidationException
    {
        if (string == null)
            return null;
        try
        {
            final Integer decimal = new Integer(string);
            
            return decimal;
        }
        catch (NumberFormatException ex)
        {
            throw new FieldValidationException("Not a valid number: "+string);
        }
    }

	@Override
	public String getStringFromSingularObject(Integer singularObject) {
		if (singularObject == null)
            return "";
        // format
        return singularObject.toString();
	}

	@Override
	protected Object getDbValueFromObject(Integer customFieldObject) {
		// TODO Auto-generated method stub
		return getStringFromSingularObject(customFieldObject);
	}

}