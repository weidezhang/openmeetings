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



//constructors
function com_zimbra_om(){
}
com_zimbra_om.prototype = new ZmZimletBase();
com_zimbra_om.prototype.constructor =com_zimbra_om;

//variables

var validFromDate,validFromTime,validToDate,validToTime;
var selected_server,selected_username,selected_password;
var new_sid;


//handling clicks

// function to convert double clicks into single clicks
com_zimbra_om.prototype.doubleClicked=
	function(){
		this.singleClicked();
		};

		
com_zimbra_om.prototype.singleClicked = 
		function(){
				this._selectserverdisplay();
			};



//preferences dialog box
com_zimbra_om.prototype.menuItemSelected = function(){
    this.pView = new DwtListView({parent:this.getShell(), noMaximize:false});
    this.pView.setSize("520","350");

    this.server1 = new DwtText ({parent:this.pView, name:"server1", id:"server1"});
    this.server1.setText("Server1");
    this.server1.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1.setLocation(20,35);
    

    this.server1_baseurl_text = new DwtText({parent:this.pView, name:"server1_baseurl_text", id:"server1_baseurl_text"});
    this.server1_baseurl_text.setText("URL");
    this.server1_baseurl_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_baseurl_text.setLocation(20,50);
    this.server1_baseurl = new DwtInputField ({parent:this.pView, name: "server1_baseurl", id: "server1_baseurl"});
    this.server1_baseurl.setValue(this.getUserProperty("server1_baseurl"));
    this.server1_baseurl.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_baseurl.setLocation(20,65);

    this.server1_username_text = new DwtText({parent:this.pView, name:"server1_username_text",id: "server1_username_text"});
    this.server1_username_text.setText("Username");
    this.server1_username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_username_text.setLocation(200,50);
    this.server1_username = new DwtInputField ({parent:this.pView, name: "server1_username", id: "server1_username"});
    this.server1_username.setValue(this.getUserProperty("server1_username"));
    this.server1_username.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_username.setLocation(200,65);

    this.server1_password_text = new DwtText ({parent:this.pView, name: "server1_passwor_text", id:"server1_password_text"});
    this.server1_password_text.setText("password");
    this.server1_password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_password_text.setLocation(380,50);
    this.server1_password = new DwtInputField ({parent:this.pView, name: "server1_password", id: "server1_password"});
    this.server1_password.setValue(this.getUserProperty("server1_password"));
    this.server1_password.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server1_password.setLocation(380,65);

    this.server2 = new DwtText({parent:this.pView, name:"server2",id:"server2"});
    this.server2.setText("Server2");
    this.server2.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2.setLocation(20,100);

    this.server2_baseurl_text = new DwtText({parent:this.pView, name:"server2_baseurl_text",id:"server2_baseurl_text"});
    this.server2_baseurl_text.setText("URL");
    this.server2_baseurl_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_baseurl_text.setLocation(20,120);
    this.server2_baseurl = new DwtInputField ({parent:this.pView, name: "server2_baseurl", id: "server2_baseurl"});
    this.server2_baseurl.setValue(this.getUserProperty("server2_baseurl"));
    this.server2_baseurl.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_baseurl.setLocation(20,135);

    this.server2_username_text = new DwtText({parent:this.pView, name:"server2_username_text", id:"server2_username_text"});
    this.server2_username_text.setText("Username");
    this.server2_username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_username_text.setLocation(200,120);
    this.server2_username = new DwtInputField ({parent:this.pView, name: "server2_username", id: "server2_username"});
    this.server2_username.setValue(this.getUserProperty("server2_username"));
    this.server2_username.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_username.setLocation(200,135);
    
    this.server2_password_text = new DwtText({parent:this.pView, name:"server2_password_text", id:"server2_password_text"});
    this.server2_password_text.setText("Password");
    this.server2_password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_password_text.setLocation(380,120);
    this.server2_password = new DwtInputField ({parent:this.pView, name: "server2_password" ,id: "server2_password"});
    this.server2_password.setValue(this.getUserProperty("server2_password"));
    this.server2_password.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server2_password.setLocation(380,135);
    
    this.server3 = new DwtText ({parent:this.pView, name:"server3", id:"server3"});
    this.server3.setText ("Server 3");
    this.server3.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3.setLocation(20,165);

    this.server3_baseurl_text = new DwtText ({parent:this.pView, name: "server3_baseurl_text", id:"server3_baseurl_text"});
    this.server3_baseurl_text.setText("URL");
    this.server3_baseurl_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_baseurl_text.setLocation(20,185);
    this.server3_baseurl  = new DwtInputField ({parent:this.pView, name:  "server3_baseurl" , id: "server3_baseurl"});
    this.server3_baseurl.setValue(this.getUserProperty("server3_baseurl"));
    this.server3_baseurl.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_baseurl.setLocation(20,200);

    this.server3_username_text = new DwtText ({parent:this.pView, name:"server3_username_text", id:"server3_username_text"});
    this.server3_username_text.setText("Username");
    this.server3_username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_username_text.setLocation(200,185)
    this.server3_username = new DwtInputField({parent:this.pView, name:"server3_username", id:"server_username"});
    this.server3_username.setValue(this.getUserProperty("server3_username"));
    this.server3_username.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_username.setLocation(200,200);

    this.server3_password_text = new DwtText ({parent:this.pView, name:"server3_password_text", id:"server3_password_text"});
    this.server3_password_text.setText("Password");
    this.server3_password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_password_text.setLocation(380,185);
    this.server3_password = new DwtInputField ({parent:this.pView, name: "server3_password", id:"server3_password"});
    this.server3_password.setValue(this.getUserProperty("server3_password"));
    this.server3_password.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server3_password.setLocation(380,200);

    this.server4 = new DwtText({parent:this.pView, name:"server4", id:"server4"});
    this.server4.setText("Server 4");
    this.server4.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4.setLocation(20,230);

    this.server4_baseurl_text = new DwtText ({parent:this.pView, name:"server4_baseurl_text", id :"server4_baseurl_text"});
    this.server4_baseurl_text.setText("URL");
    this.server4_baseurl_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_baseurl_text.setLocation(20,250);
    this.server4_baseurl = new DwtInputField ({parent:this.pView, name:"server4_baseurl", id:"server4_baseurl"});
    this.server4_baseurl.setValue(this.getUserProperty("server4_baseurl"));
    this.server4_baseurl.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_baseurl.setLocation(20,265);

    this.server4_username_text = new DwtText({parent:this.pView, name:"server4_username_text", id:"server4_username_text"});
    this.server4_username_text.setText("Username");
    this.server4_username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_username_text.setLocation(200,250);
    this.server4_username = new DwtInputField ({parent:this.pView, name: "server4_username", id: "server4_username"});
    this.server4_username.setValue(this.getUserProperty("server4_username"));
    this.server4_username.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_username.setLocation(200,265);

    this.server4_password_text = new DwtText({parent:this.pView, name:"server4_password_text", id:"server4_password_text"});
    this.server4_password_text.setText("Password");
    this.server4_password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_password_text.setLocation(380,250);
    this.server4_password = new DwtInputField ({parent:this.pView, name:"server4_password", id: "server4_password"});
    this.server4_password.setValue(this.getUserProperty("server4_password"));
    this.server4_password.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server4_password.setLocation(380,265);
    
    this.server5 = new DwtText ({parent:this.pView, name:"server5", id:"server5"});
    this.server5.setText ("Server5");
    this.server5.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5.setLocation(20,295);

    this.server5_baseurl_text =  new DwtText ({parent:this.pView, name:"server5_baseurl_text", id:"server5_baseurl_text"});
    this.server5_baseurl_text.setText("URL");
    this.server5_baseurl_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_baseurl_text.setLocation(20,315);
    this.server5_baseurl = new DwtInputField ({parent:this.pView , name:"server5_baseurl" ,id:"server5_baseurl"});
    this.server5_baseurl.setValue(this.getUserProperty("server5_baseurl"));
    this.server5_baseurl.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_baseurl.setLocation(20,330);
    
    this.server5_username_text = new DwtText ({parent:this.pView, name:"server5_username_text",id:"server5_username_text"});
    this.server5_username_text.setText("Username");
    this.server5_username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_username_text.setLocation(200,315);
    this.server5_username = new DwtInputField ({parent:this.pView , name: "server5_username", id:"server5_username"});
    this.server5_username.setValue(this.getUserProperty("server5_username"));
    this.server5_username.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_username.setLocation(200,330);

    this.server5_password_text = new DwtText ({parent:this.pView, name:"server5_password_text", id:"server5_password_text"});
    this.server5_password_text.setText("Password");
    this.server5_password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_password_text.setLocation(380,315);
    this.server5_password = new DwtInputField ({parent:this.pView, name :"server5_password", id:"server5_password"});
    this.server5_password.setValue(this.getUserProperty("server5_password"));
    this.server5_password.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.server5_password.setLocation(380,330);

    this.pbDialog = this._createDialog({title:"Zimlet Preference", view:this.pView, standardButtons: [DwtDialog.OK_BUTTON,DwtDialog.CANCEL_BUTTON]});
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,this._okBtnListenerpref));
    this.pbDialog.popup();
};



//preferences dialog box OK Button Listener
com_zimbra_om.prototype._okBtnListenerpref =
    function(){

        server1_baseurl = this.server1_baseurl.getValue();
        server1_username = this.server1_username.getValue();
        server1_password = this.server1_password.getValue();
        server2_baseurl = this.server2_baseurl.getValue();
        server2_username = this.server2_username.getValue();
        server2_password = this.server2_password.getValue();
        server3_baseurl = this.server3_baseurl.getValue();
        server3_username = this.server3_username.getValue();
        server3_password = this.server3_password.getValue();
        server4_baseurl = this.server4_baseurl.getValue();
        server4_username = this.server4_username.getValue();
        server4_password = this.server4_password.getValue();
        server5_baseurl = this.server5_baseurl.getValue();
        server5_username = this.server5_username.getValue();
        server5_password = this.server5_password.getValue();


        this.setUserProperty("server1_baseurl", server1_baseurl,1);
        this.setUserProperty("server1_username", server1_username,1);
        this.setUserProperty("server1_password", server1_password,1);
        this.setUserProperty("server2_baseurl",  server2_baseurl,1);
        this.setUserProperty("server2_username", server2_username,1);
        this.setUserProperty("server2_password", server2_password,1);
        this.setUserProperty("server3_baseurl", server3_baseurl,1);
        this.getUserProperty("server3_username", server3_username,1);
        this.getUserProperty("server3_password", server3_password,1);
        this.setUserProperty("server4_baseurl",  server4_baseurl,1);
        this.setUserProperty("server4_username", server4_username,1);
        this.setUserProperty("server4_password", server4_password,1);
        this.setUserProperty("server5_baseurl", server5_baseurl,1);
        this.setUserProperty("server5_username", server5_username,1);
        this.setUserProperty("server5_password", server5_password,1);
        this.pbDialog.popdown();


    };



//Calendar start date selector
com_zimbra_om.prototype.calendar_popup_startdate = function(){
	
			this._parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
			this._parentView.setSize("250", "150");
			this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);

			this.calendar = new DwtCalendar({parent:this._parentView, name:"calendar", id: "calendar"});
 			this.omDlg = this._createDialog({title:"Start date Selector", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON,DwtDialog.CANCEL_BUTTON]});
            this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.startdate_calendar_okbtnlistener));
			this.omDlg.popup();
};

//Calendar start date dialog OK Button Listener
com_zimbra_om.prototype.startdate_calendar_okbtnlistener = function(){
            start_date = this.calendar.getDate();
            var start_date_string = start_date + " ";
            var start_date_string_array = start_date_string.split(" ");
            var start_date_final = start_date_string_array [0] + " " + start_date_string_array[1] + " " + start_date_string_array[2] + " " + start_date_string_array[3];
            this.meeting_start_date_box.setValue(start_date_final,true);
            this.omDlg.popdown();

};




//Calendar end date selector
com_zimbra_om.prototype.calendar_popup_enddate = function(){
			
			this._parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
			this._parentView.setSize("250", "150");
			this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);

			this.calendar = new DwtCalendar({parent:this._parentView, name:"calendar", id: "calendar"});
 			this.omDlg = this._createDialog({title:"End Date Selector", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
            this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.enddate_calendar_okbtnlistener));
			this.omDlg.popup();

};


//Calendar end date selector OK Button Listener
com_zimbra_om.prototype.enddate_calendar_okbtnlistener = function(){
			//code for getting end date from the calendar and inserting it into the end date box
            end_date = this.calendar.getDate();
            var end_date_string = end_date + " ";
            var end_date_string_array = end_date_string.split(" ");
            var end_date_final = end_date_string_array [0] + " " + end_date_string_array [1] + " " + end_date_string_array[2] + " " + end_date_string_array[3];
            this.meeting_end_date_box.setValue(end_date_final,true);
            this.omDlg.popdown();
};


//Add room dialog box
com_zimbra_om.prototype.add_room_button_listener = function(){
    this.add_room_parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
    this.add_room_parentView.setSize("450","250");
    this.add_room_parentView.getHtmlElement().style.overflow = "auto";
    this.add_room_parentView.setPosition(DwtControl.RELATIVE_STYLE);

    this.add_room_roomname_text = new DwtText ({parent:this.add_room_parentView, name: "add_room_roomname_text", id: "add_room_roomname_text"});
    this.add_room_roomname_text.setText("Room Name");
    this.add_room_roomname_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.add_room_roomname_text.setLocation("20","20");

    this.add_room_roomname_box = new DwtInputField({parent:this.add_room_parentView, name: "add_room_roomname_box", id: "add_room_roomname_box"});
    this.add_room_roomname_box.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.add_room_roomname_box.setLocation("150","20")


    this.roomtype_text = new DwtText ({parent:this.add_room_parentView, name: "roomtype_text", id: "roomtype_text"});
    this.roomtype_text.setText("Room Type:");
    this.roomtype_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.roomtype_text.setLocation("20","50")


    this.roomtypes_conference = new DwtCheckbox({parent:this.add_room_parentView, name: "roomtype_conference", id:"roomtype_conference"});
    this.roomtypes_conference.setText("Conference");
    this.roomtypes_conference.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.roomtypes_conference.setLocation("20","80");

    this.roomtypes_audience = new DwtCheckbox({parent:this.add_room_parentView, name: "roomtype_audience" , id: "roomtype_audience"});
    this.roomtypes_audience.setText("Audience");
    this.roomtypes_audience.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.roomtypes_audience.setLocation("100","80");
    
    this.roomtypes_restricted = new DwtCheckbox({parent:this.add_room_parentView, name:"roomtype_restricted", id:"roomtype_restricted"});
    this.roomtypes_restricted.setText("Restricted");
    this.roomtypes_restricted.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.roomtypes_restricted.setLocation("180","80");
    
    this.roomtypes_interview = new DwtCheckbox({parent:this.add_room_parentView, name:"roomtype_interview", id:"roomtype_interview"});
    this.roomtypes_interview.setText("Interview");
    this.roomtypes_interview.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.roomtypes_interview.setLocation("260","80");
    
    
    this.comment_text = new DwtText({parent:this.add_room_parentView, name:"comment_text", id:"comment_text"});
    this.comment_text.setText("Comment");
    this.comment_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.comment_text.setLocation("20","110");
    
    this.comment = new DwtInputField ({parent:this.add_room_parentView, name:"comment", id:"comment"});
    this.comment.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.comment.setLocation("150","110");

    this.noOfPartizipants_text = new DwtText ({parent:this.add_room_parentView, name: "noOfPartizipants_text", id:"noOfPartizipants_text"});
    this.noOfPartizipants_text.setText("No of Participants");
    this.noOfPartizipants_text.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.noOfPartizipants_text.setLocation("20","140");

    this.noOfPartizipants_box = new DwtInputField ({parent:this.add_room_parentView, name: "noOfPartizipants_box", id:"noOfPartizipants_box"});
    this.noOfPartizipants_box.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.noOfPartizipants_box.setLocation("150","140");

    this.ispublic_checkbox = new DwtCheckbox ({parent:this.add_room_parentView, name:"ispublic_checkbox", id: "ispublic_checkbox"});
    this.ispublic_checkbox.setText("Public");
    this.ispublic_checkbox.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.ispublic_checkbox.setLocation("20","170");
    
    this.isModerated_checkbox = new DwtCheckbox({parent:this.add_room_parentView, name:"isModerated", id:"isModerated"});
    this.isModerated_checkbox.setText("Is Moderated");
    this.isModerated_checkbox.setPosition(DwtControl.ABSOLUTE_STYLE);
    this.isModerated_checkbox.setLocation("150","170");
    
    this.add_room_dialog = this._createDialog({title:"add Room", view:this.add_room_parentView, standartButtons:[DwtDialog.OK_BUTTON]});
    this.add_room_dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._add_room_okButtonListener));
    this.add_room_dialog.popup();
};


//Add room dialog box OK Button Listener
com_zimbra_om.prototype._add_room_okButtonListener = function(){
	this.add_room_dialog.popdown();
    var room_name = this.add_room_roomname_box.getValue();
    var roomtypes_id, isPublic, isModerated;
   
    if (this.roomtypes_conference.isSelected() == 1){
    	roomtypes_id = 1;
    }
    else if (this.roomtypes_audience.isSelected() == 1){
    	roomtypes_id = 2;
    }
    else if (this.roomtypes_restricted.isSelected() == 1){
    	roomtypes_id = 3;
    }
    else if (this.roomtypes_interview.isSelected() == 1){
    	roomtypes_id = 4;
    }
    
    var numberOfPartizipants = this.noOfPartizipants_box.getValue();    
    if (this.ispublic_checkbox.isSelected() == 1){
    	isPublic = 1;
    }
    else {
    	isPublic = 0;
    }
    
    if (this.isModerated_checkbox.isSelected() == 1){
    	isModerated = 1;
    }
    else {
    	isModerated = 0;
    }
    

    var request_url = selected_server + "services/RoomService/addRoomWithModeration?SID=" +
                     new_sid + "&name=" + room_name +
                     "&roomtypes_id=" + roomtypes_id +
                     "&comment="+
                     "&numberOfPartizipants=" + numberOfPartizipants +
                    "&ispublic=" + isPublic +
                    "&appointment=0&isDemoRoom=0&demoTime=0&isModeratedRoom=" + isModerated;

    var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(request_url);
    AjxRpc.invoke(null, url, null , new AjxCallback(this, this.add_room_responseHandler), true);

};



//Add rooms response handler
com_zimbra_om.prototype.add_room_responseHandler=
	function(response){
			try{
				var room_id = response.xml.getElementsByTagName("return"), new_room_id = [].map.call( sid, function(node){
                    return node.textContent || node.innerText || "";
				}).join("");
				}catch(e){
						this._showErrorMsg(e);
						}
				this.add_room_pView = new DwtListView({parent:this.getShell(),noMaximize:false});
			    this.add_room_pView.getHtmlElement().style.overflow = "auto";
			    this.add_room_pView.getHtmlElement().innerHTML = "<div> Room Id: " + new_room_id + "</div>";  
			    this.add_room_response_Dlg = this._createDialog({title:"Select Profile", view:this.add_room_pView, standardButtons : [DwtDialog.OK_BUTTON]});
			    this.add_room_response_Dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.add_room_response_Dlg_listener));
			    this.add_room_response_Dlg.popup();
			    this.add_room_dialog.popdown();
}
				
com_zimbra_om.prototype.add_room_response_Dlg_listener = 
		function(){
			this.add_room_response_Dlg.popdown();
				}


//Server Select dialog box
com_zimbra_om.prototype._selectserverdisplay =
    function(){
            this._profile_parentView = new DwtComposite ({parent:this.getShell()});
            this._profile_parentView.getHtmlElement().style.overflow = "auto";
            this._profile_parentView.setPosition(DwtControl.RELATIVE_STYLE);

    //        this._profile_parentView.getHtmlElement().innerHTML = "<div><select id = 'selected_server'><option value = 'server1'>"+ server1_baseurl + "</option>" + "<option value = 'server2'>" + server2_baseurl + "</option>" + "<option value = 'server3'>" + server3_baseurl + "</option>" + "<option value = 'server4'>" + server4_baseurl + "<option value = 'server5'>" + server5_baseurl + "</option>" + "</select></div>";
            this._profile_parentView.getHtmlElement().innerHTML = "<div><a href='#' id= 'link1'>"+ this.getUserProperty("server1_baseurl") +"</a></div>" +
                                                                  "<div><a href='#' id= 'link2'>"+ this.getUserProperty("server2_baseurl") +"</a></div>" +
                                                                  "<div><a href='#' id= 'link3'>"+ this.getUserProperty("server3_baseurl") +"</a></div>" +
                                                                  "<div><a href='#' id= 'link4'>"+ this.getUserProperty("server4_baseurl") +"</a></div>" +
                                                                  "<div><a href='#' id= 'link5'>"+ this.getUserProperty("server5_baseurl") +"</a></div>";

            var link_server1 = document.getElementById('link1');
            var link_server2 = document.getElementById('link2');
            var link_server3 = document.getElementById('link3');
            var link_server4 = document.getElementById('link4');
            var link_server5 = document.getElementById('link5');
       
            
            link_server1_arg = this.getUserProperty("server1_baseurl");
            link_server1.onclick = AjxCallback.simpleClosure(this.get_session,this,this.getUserProperty("server1_baseurl"));

            link_server2_arg = this.getUserProperty("server2_baseurl");
            link_server2.onclick = AjxCallback.simpleClosure(this.get_session,this,this.getUserProperty("server2_baseurl"));

            link_server3_arg = this.getUserProperty("server3_baseurl");
            link_server3.onclick = AjxCallback.simpleClosure(this.get_session,this,this.getUserProperty("server3_baseurl"));

            link_server4_arg = this.getUserProperty("server4_baseurl");
            link_server4.onclick = AjxCallback.simpleClosure(this.get_session,this,this.getUserProperty("server4_baseurl"));

            link_server5_arg = this.getUserProperty("server5_baseurl");
            link_server5.onclick = AjxCallback.simpleClosure(this.get_session,this,this.getUserProperty("server5_baseurl"));



            this.profile_select_Dlg = this._createDialog({title:"Select Profile", view:this._profile_parentView, standardButtons : [DwtDialog.OK_BUTTON]});
            this.profile_select_Dlg.popup();
    }



//Openmeetings dialog box
com_zimbra_om.prototype._displayDialog =
	function(){
			
			this._parentView = new DwtListView({parent:this.getShell(), noMaximize:false});
            this._parentView.setSize("500","400");
            this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);

            this.username_text = new DwtText({parent:this._parentView, name: "username_text", id:"username_text"});
            this.username_text.setText("Username:");
            this.username_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.username_text.setLocation(20,20);

            this.username_box = new DwtInputField ({parent:this._parentView, name:"username_box", id:"username_box"});
            this.username_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.username_box.setLocation(150,20);

            this.room_id_text = new DwtText ({parent:this._parentView, name:"room_id_text", id:"room_id_text"});
            this.room_id_text.setText("Room Id:");
            this.room_id_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.room_id_text.setLocation(20,50);

            this.room_id = new DwtInputField({parent:this._parentView, name:"room_id" , id:"room_id"});
            this.room_id.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.room_id.setLocation(150,50);

            this.add_room_button = new DwtButton({parent:this._parentView, name: "add_room_button", id: "add_room_button"});
            this.add_room_button.addSelectionListener(new AjxListener(this,this.add_room_button_listener));
            this.add_room_button.setText("Create Room");
            this.add_room_button.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.add_room_button.setLocation(300,50);

            this.password_check_box = new DwtCheckbox({parent:this._parentView, name: "password_checkbox", id:"password_checkbox"});
            this.password_check_box.setText("isPasswordProtected");
            this.password_check_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.password_check_box.setLocation(20,80);
            
            this.password_text = new DwtText({parent:this._parentView, name:"text_password", id:"text_password"});
            this.password_text.setText("Invitation password:");
            this.password_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.password_text.setLocation(20,110);

            this.password_box = new DwtInputField({parent:this._parentView, name: "password_box", id:"password_box"});
            this.password_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.password_box.setLocation(150,110);

            this.hash_value_text = new DwtText({parent:this._parentView, name: "hash_value_text", id:"hash_value_text"});
            this.hash_value_text.setText("Validity:");
            this.hash_value_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.hash_value_text.setLocation(20,140);

            this.checkbox1 = new DwtCheckbox({parent:this._parentView, name: "endless", id: "endless"});
            this.checkbox1.setText("endless");
            this.checkbox1.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.checkbox1.setLocation(20,170);

            this.checkbox2 = new DwtCheckbox({parent:this._parentView, name: "from_to_time", id: "from_to_time"});
            this.checkbox2.setText("from to time");
            this.checkbox2.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.checkbox2.setLocation(120,170);

            this.checkbox3 = new DwtCheckbox({parent:this._parentView, name: "one_time", id: "one_time"})
            this.checkbox3.setText("one time");
            this.checkbox3.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.checkbox3.setLocation(220,170);

            this.meeting_time = new DwtText({parent:this._parentView, name:"meeting_time_text", id:"meeting_time_text"});
            this.meeting_time.setText("Start Timing");
            this.meeting_time.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_time.setLocation(20,200);


            this.meeting_start_date_time = new DwtText ({parent:this._parentView, name:"meeting_start_date_text", id:"meeting_start_date_text"});
            this.meeting_start_date_time.setText("Date:");
            this.meeting_start_date_time.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_start_date_time.setLocation(20,230);

            this.meeting_start_date_box = new DwtInputField({parent:this._parentView, name:"meeting_start_date_box", id:"meeting_start_date_box"});
            this.meeting_start_date_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_start_date_box.setLocation(150,230);

            this.meeting_start_date_calendar_button = new DwtButton({parent:this._parentView, name: "meeting_start_date_calendar_button", id: "meeting_start_date_calendar_button"});
            this.meeting_start_date_calendar_button.addSelectionListener(new AjxListener(this, this.calendar_popup_startdate));
            this.meeting_start_date_calendar_button.setText("select date");
            this.meeting_start_date_calendar_button.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_start_date_calendar_button.setLocation(300,230);
        
            this.meeting_start_time_text = new DwtText ({parent:this._parentView, name:"meeting_start_time_text", id:"meeting_start_time_text"});
            this.meeting_start_time_text.setText("Time (hh:mm):");
            this.meeting_start_time_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_start_time_text.setLocation(20,260);
        
            this.meeting_start_time_box = new DwtInputField ({parent:this._parentView, name:"meeting_start_time_box", id:"meeting_start_time_box"});
            this.meeting_start_time_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_start_time_box.setLocation(150,260);

            this.meeting_end_date_text = new DwtText ({parent:this._parentView, name:"meeting_end_date_text", id:"meeting_end_date_text"});
            this.meeting_end_date_text.setText("End Timing:");
            this.meeting_end_date_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_date_text.setLocation(20,290);

            this.meeting_end_date = new DwtText ({parent:this._parentView, name:"meeting_end_date", id:"meeting_end_date"});
            this.meeting_end_date.setText("Date:");
            this.meeting_end_date.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_date.setLocation(20,310);

            this.meeting_end_date_box = new DwtInputField ({parent:this._parentView, name: "meeting_end_date_box", id:"meeting_end_date_box"});
            this.meeting_end_date_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_date_box.setLocation(150,310);

            this.meeting_end_date_calendar_button = new DwtButton({parent:this._parentView, name: "meeting_end_date_calendar_button", id: "meeting_end_date_calendar_button"});
            this.meeting_end_date_calendar_button.addSelectionListener(new AjxListener(this, this.calendar_popup_enddate));
            this.meeting_end_date_calendar_button.setText("Select Date");
            this.meeting_end_date_calendar_button.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_date_calendar_button.setLocation(300,310);

            this.meeting_end_time_text = new DwtText ({parent:this._parentView, name:"meeting_end_time_text", id: "meeting_end_time_text"});
            this.meeting_end_time_text.setText("Time(hh:mm):");
            this.meeting_end_time_text.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_time_text.setLocation(20,340);

            this.meeting_end_time_box = new DwtInputField ({parent:this._parentView, name:"meeting_end_time_box", id:"meeting_end_time_box"});
            this.meeting_end_time_box.setPosition(DwtControl.ABSOLUTE_STYLE);
            this.meeting_end_time_box.setLocation(150,340);

 			this.omDlg = this._createDialog({title:"Openmeetings", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
			this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.generateHash));
			this.omDlg.popup();
				};


com_zimbra_om.prototype.getURL =
    function (service, func, o) {
        var result = this.getUserProperty("server1_baseurl") + "services/" + service + "/" + func + "/?";
        if (o) {
            for (var key in o)
            {
                result += "&" + key + "=" + (o[key]);
            }
        }
        return (result);
    };

//get invitation hash request
com_zimbra_om.prototype.generateHash=
			function(){

                var username = this.username_box.getValue();
                var room_id = this.room_id.getValue();

                if (this.password_check_box.isSelected()==1){
                    isPasswordProtected =1;
                }
                else {
                    isPasswordProtected =0;
                }

                var invitationpass = this.password_box.getValue();

                if (this.checkbox1.isSelected() == 1){
                    valid = 1;
                    validFromDate = "";
                    validFromTime = "";
                    validToDate = "";
                    validToTime = "";
                }
                else if (this.checkbox2.isSelected() == 1){
                    valid = 2;
                    validFromDate = this.meeting_start_date_box.getValue();
                    validFromTime = this.meeting_start_time_box.getValue();
                    validToDate = this.meeting_end_date_box.getValue();
                    validToTime = this.meeting_end_time_box.getValue();
                }
                else if (this.checkbox3.isSelected() == 1){
                    valid =3;
                    validFromDate = "";
                    validFromTime = "";
                    validToDate = "";
                    validToTime = "";
                }


com_zimbra_om.prototype.date_format=
            function(str){
                var month;
                str_arr = str.split(" ");
                date = str_arr[2];
                year = str_arr[3];
                switch (str_arr[1]){
                    case "Jan":
                        month = 1;
                        break;
                    case "Feb":
                        month = 2;
                        break;
                    case "Mar":
                        month = 3;
                        break;
                    case "Apr":
                        month = 4;
                        break;
                    case "May":
                        month = 5;
                        break;
                    case "Jun":
                        month = 6;
                        break;
                    case "Jul":
                        month = 7;
                        break;
                    case "Aug":
                        month = 8;
                        break;
                    case "Sep":
                        month = 9;
                        break;
                    case "Oct":
                        month = 10;
                        break;
                    case "Nov":
                        month = 11;
                        break;
                    case "Dec":
                        month = 12;
                        break;
                }
                return (date+"."+ month +"."+year);
            }

                var request_url = this.getURL("RoomService", "getInvitationHash",{SID:new_sid, username:username, room_id: room_id, isPasswordProtected:isPasswordProtected, invitationpass:invitationpass,valid:valid, validFromDate:this.date_format(validFromDate), validFromTime:validFromTime,validToDate:this.date_format(validToDate),validToTime:validToTime});
				var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(request_url);
				AjxRpc.invoke(null , url, null , new AjxCallback(this, this.generateHash_responseHandler), true);
		}


//getinvitationHash response handler
com_zimbra_om.prototype.generateHash_responseHandler =
			function(response){
				try{
					 invitationHash =response.xml.getElementsByTagName("ns:return"), new_invitationHash = [].map.call( sid, function(node){
                        return node.textContent || node.innerText || "";
                    }).join("");
					} catch(e){
							this._showErrorMsg(e);
						}
				var invitation_url = selected_server + '?invitationHash=' + new_invitationHash ;
                msg = "Timing: \n" + "Start Timing:" +  validFromDate + "  " + validFromTime + "\n" + "End Timing:" + validToDate + " " + validToTime + "\n" + invitation_url;
				this.new_message(msg);
			}
             
//getSession Request
com_zimbra_om.prototype.get_session=
			function(str){
                    if (str == this.getUserProperty("server1_baseurl")){
                        selected_server = this.getUserProperty("server1_baseurl");
                        selected_username = this.getUserProperty("server1_username");
                        selected_password = this.getUserProperty("server1_password");
                    }
                    else if (str == this.getUserProperty("server2_baseurl")){
                        selected_server = this.getUserProperty("server2_baseurl");
                        selected_username = this.getUserProperty("server2_username");
                        selected_password = this.getUserProperty("server2_password");
                    }
                    else if (str == this.getUserProperty("server3_baseurl")){
                        selected_server = this.getUserProperty("server3_baseurl");
                        selected_username = this.getUserProperty("server3_username");
                        selected_password = this.getUserProperty("server3_password");
                    }
                    else if (str == this.getUserProperty("server4_baseurl")){
                        selected_server = this.getUserProperty("server4_baseurl");
                        selected_username = this.getUserProperty("server4_username");
                        selected_password = this.getUserProperty("server4_password");
                    }
                    else if (str == this.getUserProperty("server5_baseurl")){
                        selected_server = this.getUserProperty("server5_baseurl");
                        selected_username = this.getUserProperty("server5_username");
                        selected_password = this.getUserProperty("server5_password");
                    }


                get_session_url = this.getURL ("UserService", "getSession");
                  //  get_session_url = str + 'services/UserService/getSession';

					var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(get_session_url);
                AjxRpc.invoke(null, url, null , new AjxCallback(this, this._responseHandler) , true);
				};

				
//getSession request response Handler
com_zimbra_om.prototype._responseHandler=
		function(response){
				try{
					sid =response.xml.getElementsByTagName("ax24:session_id"),new_sid = [].map.call( sid, function(node){
                        return node.textContent || node.innerText || "";
                    }).join("");


                    this.login_user();
					}catch(e){
							this._showErrorMsg(e);
							}
        };


//loginUser request
com_zimbra_om.prototype.login_user = function(){
                var url = this.getURL ("UserService", "loginUser" , {SID:new_sid, username: selected_username, userpass:selected_password});
                var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
				
                AjxRpc.invoke(null, request_url, null , new AjxCallback(this, this._reponseHandler1), true);
                this._displayDialog();
        };


//loginUser request response Handler
com_zimbra_om.prototype._responseHandler1= 
				function(response){
								//will have to check if the returned value is positive or negative. if negative show the  error message.
								try{
									var errorid= response.getElementsByTagName("return");
                                    this.get_rooms();
									}catch(e){
										this._showErrorMsg(e);
											}
								if (errorid < 0){
										this._notlogged(errorid);
									}
						};
						

//getErrorByCode request
com_zimbra_om.prototype._notlogged = function (errorid){

				var language_id = 1;

                var url = this.getURL ("UserService","getErrorByCode",{SID:new_sid, errorid:errorid,language_id:1});
                var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
                AjxRpc.invoke(null, request_url, null , new AjxCallback(this, this._reponseHandler2), true);
								};


//getErrorByCode request response Handler							
com_zimbra_om.prototype._responseHandler2= 
				function(postCallback, response){
							// show the error message
							try {
								var msg = response.xml.getElementById("errmessage");
								this._showErrorMsg(msg);
								}catch(e){
									this._showErrorMsg(e);
									}
						};
			
			
//Response on failure
com_zimbra_om.prototype._showErrorMsg =
		function(msg){
				var msgDialog=appCtxt.getMsgDialog();
				msgDialog.reset();
				msgDialog.setMessage(msg,DwtMessageDialog.CRITICAL_STYLE);
				msgDialog.popup();
		};

//Error message
com_zimbra_om.prototype._showErrorMsg =
    function(msg) {
        var msgDialog = appCtxt.getMsgDialog();//get a message dialog
        msgDialog.reset(); // clean up earlier message
        msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE); //set new message
        msgDialog.popup();//display the dialog
    };

// Open new mail message
com_zimbra_om.prototype.new_message =
    function (msg) {
        var composeController = AjxDispatcher.run("GetComposeController");
        if (composeController) {
            var appCtxt = window.top.appCtxt;
            var zmApp = appCtxt.getApp();
            var newWindow = zmApp != null ? (zmApp._inNewWindow ? true : false) : true;
            var params = {action:ZmOperation.NEW_MESSAGE, inNewWindow:newWindow,
                toOverride:null, subjOverride:null, extraBodyText:msg, callback:null}
            composeController.doAction(params); // opens asynchronously the window.
            this.displayStatusMessage("Trying to open a new message dialog. Please wait ...");
        }
    }