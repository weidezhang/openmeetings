(function($) {
    var MacroJsOverrideForFields = function(fileTypes) {
        this.fileTypes = fileTypes;
    };
 
    // Overwrites how the parameter-values are visible to the user (in the macro browser window)
    MacroJsOverrideForFields.prototype.beforeParamsSet = function(params, inserting) {
        /* Enter here your code to modifie the parameter-values before they're visible.
            E.g. you have a list of account-numbers, but want to show the corresponding names to the user.
                  Then you can write here something like: 
                  params["accnr"] = getName(params["accnr"]);
                  In the Rich Text Editor you will see something like this: {mymacro:accnr=7234892} and in the Macro-Browser you will see a list of names.
            But don't forget to convert it back in the <beforeParamsRetrieved> function below.
        */
    	params["roomid"] = getName(params["roomid"]);
        return params;
    };
 
    // Overwrites how the parameter-values are returned back to your macro
    MacroJsOverrideForFields.prototype.beforeParamsRetrieved = function(params) {
        // Enter here your code to modifie the parameter-values before they're returned to the macro.
        // See <beforeParamsSet> above for more details.
    	params["roomid"] = getName(params["roomid"]);
        return params;
    };
 
    // Overwrites the parameter itself. Make sure you overwrite the correct parameter -> maybe you have more then one parameter of the same type
    MacroJsOverrideForFields.prototype.fields = {
        "enum" : function(param) {
        	/* Here you can modifie and/or populate your enums dynamically. This function is called each time the macro browser starts.
                   E.g.:
                       if(param.name == "nameOfYourParameter") {
                           param.enumValues = ["this", "is", "an", "example"];
                       }
        	*/
        	
        	if(param.name == "roomid") {
                param.enumValues = ["this", "is", "an", "example"];
            }
            var result = AJS.MacroBrowser.ParameterFields["enum"](param, null);
            return result;
        }
    };
 
    AJS.MacroBrowser.activateSmartFieldsAttachmentsOnPage = function(macroName, fileTypes) {
    	AJS.MacroBrowser.setMacroJsOverride("openmeetings-room-link", new MacroJsOverrideForFields("roomid"));
              // <mymacro> should be the name of your macro and <myparameter> the name of your parameter. But for the second one I'm not sure what it should really be.
              // I really don't know, what this function does exactly. But I haven't found some usefull documentation about it (to be honest: I've found nothing at all)
    }
 
})(AJS.$);