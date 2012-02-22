package org.openmeetings.jira.plugin.jira.customfields;

import org.openmeetings.jira.plugin.ao.omrooms.Room;
import org.openmeetings.jira.plugin.ao.omrooms.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.converters.SelectConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.util.ErrorCollection;

public class RoomSelectList extends SelectCFType {
    private static final Logger log = LoggerFactory.getLogger(RoomSelectList.class);
	//private final OptionsManager optionsManager;
	//private static SelectConverter selectConverter;
	private final RoomService roomService;

    public RoomSelectList(CustomFieldValuePersister customFieldValuePersister, StringConverter stringConverter, SelectConverter selectConverter, OptionsManager optionsManager, GenericConfigManager genericConfigManager, RoomService roomService) {
    super(customFieldValuePersister, stringConverter, selectConverter, optionsManager, genericConfigManager);
    this.roomService = roomService;
    //this.optionsManager = optionsManager;
}
    
    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        // This method is also called to get the default value, in
        // which case issue is null so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }

         FieldConfig fieldConfig = field.getRelevantConfig(issue);
         //add what you need to the map here
         //field.getValue(issue);
         
         //fieldConfig.getConfigItems().get(1);
        	//getValueFromIssue
         //Object param = issue.getCustomFieldValue(field);
         //map.put("param", param);
         
         List<Room> rooms =  roomService.allNotDeleted();
         
         map.put("rooms", rooms);
         //map.put("value", param); 
         
        return map;
    }
    
    @Override
    public PersistenceFieldType getDatabaseType()
	{
		return PersistenceFieldType.TYPE_LIMITED_TEXT;
	}
    
    @Override
    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config)
    {

    }
    
//    @Override
//    public Options getOptions(FieldConfig config, JiraContextNode jiraContextNode)
//    {
//      return this.optionsManager.getOptions(config);
//    }
    
//    @Override
//    public Object getValueFromIssue(CustomField field, Issue issue){
//		
//    	issue.getCustomFieldValue(field);
//    	return issue;
//    	
//    }
//    
//    public Options getOptions(FieldConfig config, JiraContextNode jiraContextNode)
//    {
//      return this.optionsManager.getOptions(config);
//    }
}