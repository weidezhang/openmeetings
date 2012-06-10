//constructors
function com_zimbra_om(){
}
com_zimbra_om.prototype = new ZmZimletBase();
com_zimbra_om.prototype.constructor =com_zimbra_om;
com_zimbra_om._url = "http://demo.dataved.ru/openmeetings/services/UserService/getSession";

//----------------------------------------------------------------------------------variables---------------------------------------------------------------------------------------------------------------------------------------------------------------------

var validTOTime;
var validFromTime;
var validFromDate;
var validfromdate;
var valid;
var invitationpass;
var isPasswordProtected;
var conferencedomain;
var email;
var baseurl;
var username;
var sid;
var profile;
var room_id;
var language_id;
var invitation_password;
var start_date;
var start_time;
var end_date;
var end_time;
var endless;
var fromtotime;
var onetime;
var email_id;
var subject;
var message;
var date;
var server1_baseurl;
var server1_username;
var server1_password;
var server2_baseurl;
var server2_username;
var server2_password;
var server3_baseurl;
var server3_username;
var server3_password;
var server4_baseurl;
var server4_username;
var server4_password;
var server5_baseurl;
var server5_username;
var server5_password;

//------------------------------------------------------------------------variables end---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//------------------------------------------------------------------------handling clicks------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// function to convert double clicks into single clicks
com_zimbra_om.prototype.doubleClicked=
	function(){
		this.singleClicked();
		};

		
com_zimbra_om.prototype.singleClicked = 
		function(){
				this._selectserverdisplay();
			};

//-----------------------------------------------------------------------handling clicks end---------------------------------------------------------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------------preferences dialog box--------------------------------------------------------------------------------------------------------------------------------------------------------------------

com_zimbra_om.prototype.menuItemSelected = function(){
    //this.pView = new DwtComposite(this.getShell());
    //this.pView.getHtmlElement().innerHTML = "<div>username <input type = 'text'/> <br/> password <input type = 'text'/> <br/> baseurl <input type = 'text'/> </div>";
    //this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
    this.pView = new DwtListView({parent:this.getShell(), noMaximize:false});

    this.server1 = new DwtText ({parent:this.pView, name:"server1", id:"server1"});
    this.server1.setText("Server1");

    this.server1_baseurl_text = new DwtText({parent:this.pView, name:"server1_baseurl_text", id:"server1_baseurl_text"});
    this.server1_baseurl_text.setText("URL");
    this.server1_baseurl = new DwtInputField ({parent:this.pView, name: "server1_baseurl", id: "server1_baseurl"});

    this.server1_username_text = new DwtText({parent:this.pView, name:"server1_username_text",id: "server1_username_text"});
    this.server1_username_text.setText("Username");
    this.server1_username = new DwtInputField ({parent:this.pView, name: "server1_username", id: "server1_username"});

    this.server1_password_text = new DwtText ({parent:this.pView, name: "server1_passwor_text", id:"server1_password_text"});
    this.server1_password_text.setText("password");
    this.server1_password = new DwtInputField ({parent:this.pView, name: "server1_password", id: "server1_password"});

    this.server2 = new DwtText({parent:this.pView, name:"server2",id:"server2"});
    this.server2.setText("Server2");

    this.server2_baseurl_text = new DwtText({parent:this.pView, name:"server2_baseurl_text",id:"server2_baseurl_text"});
    this.server2_baseurl_text.setText("URL");
    this.server2_baseurl =  new DwtInputField ({parent:this.pView, name: "server2_baseurl", id: "server2_baseurl"});

    this.server2_username_text = new DwtText({parent:this.pView, name:"server2_username_text", id:"server2_username_text"});
    this.server2_username_text.setText("Username");
    this.server2_username = new DwtInputField ({parent:this.pView, name: "server2_username", id: "server2_username"});

    this.server2_password_text = new DwtText({parent:this.pView, name:"server2_password_text", id:"server2_password_text"});
    this.server2_password_text.setText("Password");
    this.server2_password = new DwtInputField ({parent:this.pView, name: "server2_password" ,id: "server2_password"});

    //server3
    this.server3 = new DwtText ({parent:this.pView, name:"server3", id:"server3"});
    this.server3.setText ("Server 3");

    this.server3_baseurl_text = new DwtText ({parent:this.pView, name: "server3_baseurl_text", id:"server3_baseurl_text"});
    this.server3_baseurl_text.setText("URL");
    this.server3_baseurl  = new DwtInputField ({parent:this.pView, name:  "server3_baseurl" , id: "server3_baseurl"});

   //server3 username
    this.server3_username_text = new DwtText ({parent:this.pView, name:"server3_username_text", id:"server3_username_text"});
    this.server3_username_text.setText("Username");
    this.server3_username = new DwtInputField({parent:this.pView, name:"server3_username", id:"server_username"});

    this.server3_password_text = new DwtText ({parent:this.pView, name:"server3_password_text", id:"server3_password_text"});
    this.server3_password_text.setText("Password");
    this.server3_password = new DwtInputField ({parent:this.pView, name: "server3_password", id:"server3_password"});

    this.server4 = new DwtText({parent:this.pView, name:"server4", id:"server4"});
    this.server4.setText("Server 4");

    this.server4_baseurl_text = new DwtText ({parent:this.pView, name:"server4_baseurl_text", id :"server4_baseurl_text"});
    this.server4_baseurl_text.setText("URL");
    this.server4_baseurl = new DwtInputField ({parent:this.pView, name:"server4_baseurl", id:"server4_baseurl"});

    this.server4_username_text = new DwtText({parent:this.pView, name:"server4_username_text", id:"server4_username_text"});
    this.server4_username_text.setText("Username")
    this.server4_username = new DwtInputField ({parent:this.pView, name: "server4_username", id: "server4_username"});

    this.server4_password_text = new DwtText({parent:this.pView, name:"server4_password_text", id:"server4_password_text"});
    this.server4_password_text.setText("Password");
    this.server4_password = new DwtInputField ({parent:this.pView, name:"server4_password", id: "server4_password"});

    this.server5 = new DwtText ({parent:this.pView, name:"server5", id:"server5"});
    this.server5.setText ("Server5");

    this.server5_baseurl_text =  new DwtText ({parent:this.pView, name:"server5_baseurl_text", id:"server5_baseurl_text"});
    this.server5_baseurl_text.setText("URL");
    this.server5_baseurl = new DwtInputField ({parent:this.pView , name:"server5_baseurl" ,id:"server5_baseurl"});

    this.server5_username_text = new DwtText ({parent:this.pView, name:"server5_username_text",id:"server5_username_text"});
    this.server5_username_text.setText("Username");
    this.server5_username = new DwtInputField ({parent:this.pView , name: "server5_username", id:"server5_username"});

    this.server5_password_text = new DwtText ({parent:this.pView, name:"server5_password_text", id:"server5_password_text"});
    this.server5_password_text.setText("Password");
    this.server5_password = new DwtInputField ({parent:this.pView, name :"server5_password", id:"server5_password"});

    this.pbDialog = this._createDialog({title:"Zimlet Preference", view:this.pView, standardButtons: [DwtDialog.OK_BUTTON]});
    this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this,this._okBtnListenerpref));
    this.pbDialog.popup();
};


/**
com_zimbra_om.prototype._createPreferenceView =
    function() {
        var html = new Array();
        var i = 0;
        html[i++] = "<div>";
        html[i++] = "Server 1"
        html[i++] = "baseurl";
        html[i++] = "<input type = 'text' id = 'server1_baseurl'/>";
        html[i++] = "username";
        html[i++] = "<input type = 'text' id = 'server1_username'/>";
        html[i++] = "password";
        html[i++] = "<input type = 'text' id = 'server1_password'/>";
        html[i++] = "</div>"
        html[i++] = "<div>";
        html[i++] = "Server 2"
        html[i++] = "baseurl";
        html[i++] = "<input type = 'text' id = 'server2_baseurl'/>";
        html[i++] = "username";
        html[i++] = "<input type = 'text' id = 'server2_username'/>";
        html[i++] = "password";
        html[i++] = "<input type = 'text' id = 'server2_password'/>";
        html[i++] = "</div>"
        html[i++] = "<div>";
        html[i++] = "Server 3"
        html[i++] = "baseurl";
        html[i++] = "<input type = 'text' id = 'server3_baseurl'/>";
        html[i++] = "username";
        html[i++] = "<input type = 'text' id = 'server3_username'/>";
        html[i++] = "password";
        html[i++] = "<input type = 'text' id = 'server3_password'/>";
        html[i++] = "</div>"
        html[i++] = "<div>";
        html[i++] = "Server 4"
        html[i++] = "baseurl";
        html[i++] = "<input type = 'text' id = 'server4_baseurl'/>";
        html[i++] = "username";
        html[i++] = "<input type = 'text' id = 'server4_username'/>";
        html[i++] = "password";
        html[i++] = "<input type = 'text' id = 'server4_password'/>";
        html[i++] = "</div>"
        html[i++] = "<div>";
        html[i++] = "Server 5"
        html[i++] = "baseurl";
        html[i++] = "<input type = 'text' id = 'server5_baseurl'/>";
        html[i++] = "username";
        html[i++] = "<input type = 'text' id = 'server5_username'/>";
        html[i++] = "password";
        html[i++] = "<input type = 'text' id = 'server5_password'/>";
        html[i++] = "</div>"
        return html.join("");
    };

 **/

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
        this.setUserProperty("server1_baseurl", server1_baseurl);
        this.setUserProperty("server1_username", server1_username);
        this.setUserProperty("server1_password", server1_password);
        this.setUserProperty("server2_baseurl",  server2_baseurl );
        this.setUserProperty("server2_username", server2_username);
        this.setUserProperty("server2_password", server2_password);
        this.setUserProperty("server3_baseurl", server3_baseurl);
        this.getUserProperty("server3_username", server3_username);
        this.getUserProperty("server3_password", server3_password);
        this.setUserProperty("server4_baseurl",  server4_baseurl);
        this.setUserProperty("server4_username", server4_username);
        this.setUserProperty("server4_password", server4_password);
        this.setUserProperty("server5_baseurl", server5_baseurl);
        this.setUserProperty("server5_username", server5_username);
        this.setUserProperty("server5_password", server5_password);
        this.pbDialog.popdown();
    };

        //test

    /**

        this.testView = new DwtComposite({parent:this.getShell()});
        this.testView.setSize("400","400");
        this.testView.getHtmlElement().style.overflow = "auto";
        this.testView.setPosition(DwtControl.RELATIVE_STYLE);
        this.testView.getHtmlElement().innerHTML = "<div>" + server1_baseurl+ server1_username + server1_password+ "</div>" + "<div>" + this.getUserProperty("server2_baseurl") + this.getUserProperty("server2_username") + this.getUserProperty("server2_password") + "</div>";
        this.testViewDlg = this._createDialog({title:"test", view :this.testView ,standardButtons: [DwtDialog.OK_BUTTON]});
        this.testViewDlg.popup();
    };

    **/



//------------------------------------------------------------------------preferences dialog box end-----------------------------------------------------------------------------------------------------------------------------------------------------------


//------------------------------------------------------------------ start date selector calendar -------------------------------------------------------------------------------------------------------------------------------------------------------------

com_zimbra_om.prototype.calendar_popup_startdate = function(){
	
			this._parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
			this._parentView.setSize("250", "150");
			this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);

			this.calendar = new DwtCalendar({parent:this._parentView, name:"calendar", id: "calendar"});
           // this.calendar.addSelectionListener(new AjxListener(this,this.startdate_calendar_okbtnlistener));

 			this.omDlg = this._createDialog({title:"Start date Selector", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
            this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.startdate_calendar_okbtnlistener));
			this.omDlg.popup();
};

com_zimbra_om.prototype.startdate_calendar_okbtnlistener = function(){
            start_date = this.calendar.getDate();
            //var start_date_split = start_date.split(" ");
           // var final_start_split = start_date_split[0]+start_date_split[1]+start_date_split[2]+start_date_split[3];

			//this._startdate_calendar_okbtnlistener_parentView = new DwtComposite({parent:this.getShell(),noMaximize:false});
            //this._startdate_calendar_okbtnlistener_parentView.setSize("250","250");
            //this._parentView.getHtmlElement().style.overflow = "auto";
           // this._parentView.setPosition(DwtControl.RELATIVE_STYLE);
           // this._startdate_calendar_okbtnlistener_parentView.getHtmlElement().innerHTML = date;

            var start_date_string = start_date + " ";
            var start_date_string_array = start_date_string.split(" ");
            var start_date_final = start_date_string_array [0] + " " + start_date_string_array[1] + " " + start_date_string_array[2] + " " + start_date_string_array[3];
            this.meeting_start_date_box.setValue(start_date_final,true);
            this.omDlg.popdown();

            //this._startdate_calendar_okbtnlistener_dialog =this._createDialog({title:"just fun",view:this._startdate_calendar_okbtnlistener_parentView,standardButtons:[DwtDialog.OK_BUTTON]});
            //this._startdate_calendar_okbtnlistener_dialog.popup();
};

//------------------------------------------------------------------------start date selector calendar end ----------------------------------------------------------------------------------------------------------------------------------------------------



//------------------------------------------------------------------------end date selector calendar-----------------------------------------------------------------------------------------------------------------------------------------------------------

com_zimbra_om.prototype.calendar_popup_enddate = function(){
			
			this._parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
			this._parentView.setSize("250", "150");
			this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);

			this.calendar = new DwtCalendar({parent:this._parentView, name:"calendar", id: "calendar"});
           // this.calendar.addSelectionListener(new AjxListener(this,this.enddate_calendar_okbtnlistener));

 			this.omDlg = this._createDialog({title:"End Date Selector", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
            this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.enddate_calendar_okbtnlistener));
			this.omDlg.popup();

};

com_zimbra_om.prototype.enddate_calendar_okbtnlistener = function(){
			//code for getting end date from the calendar and inserting it into the end date box
            end_date = this.calendar.getDate();
            var end_date_string = end_date + " ";
            var end_date_string_array = end_date_string.split(" ");
            var end_date_final = end_date_string_array [0] + " " + end_date_string_array [1] + " " + end_date_string_array[2] + " " + end_date_string_array[3];
            this.meeting_end_date_box.setValue(end_date_final,true);
            this.omDlg.popdown();
};


//-----------------------------------------------------------------------end date selector calendar end-------------------------------------------------------------------------------------------------------------------------------------------------------


//-------------------------------------------------------------------get rooms--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
com_zimbra_om.prototype.get_rooms = function(){
    var url = 'http://demo.openmeetings.de/openmeetings/services/UserService/getRooms?SID=' +
                    sid ;
                        //HANDLE THE RETURNED VALUES
    //AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getSession"], new AjxCallback(this, this._reponseHandler, postCallback) , true);
    var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
    AjxRpc.invoke(null, request_url, null , new AjxCallback(this, this._reponseHandler) , true);
    //set room variables


    //this._get_rooms_parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
    //this._get_rooms_parentView.setSize("200","200");
    //write the selection box for returned values
    //this.get_rooms_dialog = this._createDialog({title:"Rooms", view:this._get_rooms_parentView, standardButtons : [DwtDialog.OK_BUTTON]});
    //this.get_rooms_dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this.get_rooms_okBtnListener));
    //this.get_rooms_dialog.popup();
};

com_zimbra_om.prototype.get_rooms_okBtnListener = function(){
    //get the value to the rooms box and popdown
}

//-----------------------------------------------------------------------get rooms end -----------------------------------------------------------------------------------------------------------------------------------------------------------------------


//-----------------------------------------------------------------------add room ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

com_zimbra_om.prototype.add_room_button_listener = function(){
    this.add_room_parentView = new DwtListView({parent:this.getShell(),noMaximize:false});
    this.add_room_parentView.setSize("500","300");
    this.add_room_parentView.getHtmlElement().style.overflow = "auto";
    this.add_room_parentView.setPosition(DwtControl.RELATIVE_STYLE);

    this.add_room_roomname_text = new DwtText ({parent:this.add_room_parentView, name: "add_room_roomname_text", id: "add_room_roomname_text"});
    this.add_room_roomname_text.setText("Room Name");

    this.add_room_roomname_box = new DwtInputField({parent:this.add_room_parentView, name: "add_room_roomname_box", id: "add_room_roomname_box"});


    this.roomtype_text = new DwtText ({parent:this.add_room_parentView, name: "roomtype_text", id: "roomtype_text"});
    this.roomtype_text.setText("Room Type");


    this.roomtype_conference = new DwtCheckbox({parent:this.add_room_parentView, name: "roomtype_conference", id:"roomtype_conference"});
    this.roomtype_conference.setText("Conference");

    this.roomtype_audience = new DwtCheckbox({parent:this.add_room_parentView, name: "roomtype_audience" , id: "roomtype_audience"});
    this.roomtype_audience.setText("Audience");


    this.noOfPartizipants_text = new DwtText ({parent:this.add_room_parentView, name: "noOfPartizipants_text", id:"noOfPartizipants_text"});
    this.noOfPartizipants_text.setText("No of Participants");

    this.noOfPartizipants_box = new DwtInputField ({parent:this.add_room_parentView, name: "noOfPartizipants_box", id:"noOfPartizipants_box"});

    this.ispublic_checkbox = new DwtCheckbox ({parent:this.add_room_parentView, name:"ispublic_checkbox", id: "ispublic_checkbox"});
    this.ispublic_checkbox.setText("Public");



    this.add_room_dialog = this._createDialog({title:"add Room", view:this.add_room_parentView, standartButtons:[DwtDialog.OK_BUTTON]});
    this.add_room_dialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._add_room_okButtonListener));
    this.add_room_dialog.popup();
};

com_zimbra_om.prototype._add_room_okButtonListener = function(){
    var room_name = document.getElementById('room_name');
    var roomtypes_id = document.getElementById('roomtypes_id');
    var numberOfPartizipants = document.getElementById('numberOfPartizipants');
    var ispublic = document.getElementById('ispublic');

    var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(com_zimbra_om._url);
    var request_url = 'http://demo.openmeetings.de/openmeetings/services/UserService/getRooms?SID=' +
        sid  + '&name'+
        room_name + '&roomtypes_id'+
        roomtypes_id + '&numberOfPartizipants'+
        numberOfPartizipants + '&ispublic'+
        ispublic;
    //AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getSession"], new AjxCallback(this, this._reponseHandler, postCallback) , true);
    AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getSession"], new AjxCallback(this, this._reponseHandler) , true);

    // SET THIS ROOM IN THE MAIN FORM
    this.add_room_dialog.popdown();
};
//------------------------------------------------------------------add room end ----------------------------------------------------------------------------------------------------------------------------------------------------------------------


//-----------------------------------------------------------------------profile select box ------------------------------------------------------------------------------------------------------------------------------------------------------------
com_zimbra_om.prototype._selectserverdisplay =
    function(){
            this._profile_parentView = new DwtComposite ({parent:this.getShell()});
            this._profile_parentView.getHtmlElement().style.overflow = "auto";
            this._profile_parentView.setPosition(DwtControl.RELATIVE_STYLE);

            this._profile_parentView.getHtmlElement().innerHTML = "<div><select><option value = 'server1'>"+ server1_baseurl + "</option>" + "<option value = 'server2'>" + server2_baseurl + "</option>" + "<option value = 'server3'>" + server3_baseurl + "</option>" + "<option value = 'server4'>" + server4_baseurl + "<option value = 'server5'>" + server5_baseurl + "</option>" + "</select></div>";

            this.profile_select_Dlg = this._createDialog({title:"Select Profile", view:this._profile_parentView, standardButtons : [DwtDialog.OK_BUTTON]});
            this.profile_select_Dlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._profile_okBtnListener));
            this.profile_select_Dlg.popup();
    }

com_zimbra_om.prototype._profile_okBtnListener =
    function(){
        this._invoke();
        this.login_user();
        this.get_rooms();
        // get the rooms
        //get the selected option
        this.profile_select_Dlg.popdown();
        this._displayDialog();
    }
//------------------------------------------------------------------------profile select box end --------------------------------------------------------------------------------------------------------------------------------------------------------

//---------------------------------------------------------------main dialog box ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

//opening a dialog box
com_zimbra_om.prototype._displayDialog =
	function(){
			
			this._parentView = new DwtListView({parent:this.getShell(), noMaximize:false});
            this._parentView.setSize("550","500");
            this._parentView.getHtmlElement().style.overflow = "auto";
            this._parentView.setPosition(DwtControl.RELATIVE_STYLE);


            //button
            //this.get_rooms_button = new DwtButton ({parent:this._parentView, name:"get_rooms_button", id:"get_rooms_button"});
            //this.get_rooms_button.addSelectionListener(new AjxListener(this,this.get_rooms_button_listener));
            //this.get_rooms_button.setText("get rooms");


            //this.add_room_button = new DwtButton({parent:this._parentView, name: "add_room_button", id: "add_room_button", listeners: new AjxListener(this, this.add_room_button_listener)});
            this.add_room_button = new DwtButton({parent:this._parentView, name: "add_room_button", id: "add_room_button"});
            this.add_room_button.addSelectionListener(new AjxListener(this,this.add_room_button_listener));
            this.add_room_button.setText("add room");

            this._language_text = new DwtText ({parent:this._parentView, name: "language_text" , id: " language_text"});
            this._language = new DwtInputField({parent:this._parentView, name: "language" , id:"language" });


    //    var view1 = new DwtComposite(this._parentView);
    //    view1.getHtmlElement().style.overflow = "hidden";
    //    view1.getHtmlElement().innerHTML = this._constructDialogView();
    //    this._parentView.addItem(view1, 1 , true, 1);


        //this.calendar = new DwtCalendar({parent:this._parentView, name: "calendar", id: "calendar"});


    //getRooms
    //        this.get_room = new DwtButton({parent:this._parentView});
    //        this.get_room.setText("get Rooms");

        //text language
        this.text_language = new DwtText({parent:this._parentView, name:"text_language", id:"text_language"});
        this.text_language.setText("language");
        //this.text_language.setTextPosition(this.text_language.TEXT_LEFT);

        this.language_box = new DwtInputField({parent:this._parentView, name: "language_box", id:"language_box"});



        // invitation password
        this.text_password = new DwtText({parent:this._parentView, name:"text_password", id:"text_password"});
        this.text_password.setText("invitation password");
        //this.text_password.setTextPosition(this.text_password.TEXT_LEFT);

        this.password_box = new DwtInputField({parent:this._parentView, name: "password_box", id:"password_box"});

        // Meeting time
        this.meeting_time = new DwtText({parent:this._parentView, name:"meeting_time_text", id:"meeting_time_text"});
        this.meeting_time.setText("Meeting Time");


        this.meeting_start_date_time = new DwtText ({parent:this._parentView, name:"meeting_start_date_text", id:"meeting_start_date_text"});
        this.meeting_start_date_time.setText("Start Date");

        this.meeting_start_date_box = new DwtInputField({parent:this._parentView, name:"meeting_start_date_box", id:"meeting_start_date_box"});
        this.meeting_start_date_calendar_button = new DwtButton({parent:this._parentView, name: "meeting_start_date_calendar_button", id: "meeting_start_date_calendar_button"});
        this.meeting_start_date_calendar_button.addSelectionListener(new AjxListener(this, this.calendar_popup_startdate));
        this.meeting_start_date_calendar_button.setText("select date");

        this.meeting_start_time_text = new DwtText ({parent:this._parentView, name:"meeting_start_time_text", id:"meeting_start_time_text"});
        this.meeting_start_time_text.setText("Start Time");

        this.meeting_start_time_box = new DwtInputField ({parent:this._parentView, name:"meeting_start_time_box", id:"meeting_start_time_box"});



        this.meeting_end_date_text = new DwtText ({parent:this._parentView, name:"meeting_end_date_text", id:"meeting_end_date_text"});
        this.meeting_end_date_text.setText("Meeting End Date");

        this.meeting_end_date_box = new DwtInputField ({parent:this._parentView, name: "meeting_end_date_box", id:"meeting_end_date_box"});
        this.meeting_end_date_calendar_button = new DwtButton({parent:this._parentView, name: "meeting_end_date_calendar_button", id: "meeting_end_date_calendar_button"});
        this.meeting_end_date_calendar_button.addSelectionListener(new AjxListener(this, this.calendar_popup_enddate));
        this.meeting_end_date_calendar_button.setText("Select Date");

        this.meeting_end_time_text = new DwtText ({parent:this._parentView, name:"meeting_end_time_text", id: "meeting_end_time_text"});
        this.meeting_end_time_text.setText("Meeting End Time");

        this.meeting_end_time_box = new DwtInputField ({parent:this._parentView, name:"meeting_end_time_box", id:"meeting_end_time_box"});


        //meeting code



        //    this.check = new DwtCheckbox({parent:this._parentView, name: "checkbox" , checked: true, posSytle: Dwt.RELATIVE_STYLE, id:"checkbox", index:1});
        //    this.check.setText("i am ankur ankan");
        //    this.check.setEnabled(false);
        //    this._parentView.addItem(this.check, 2 ,true , 2);

        //    this._textbox = new DwtInputField({parent:this._parentView, name: "text", id: "textbox", index:2});
        //    this._textbox.setEnabled(true);
        //    this._textbox.setHint("this is the hint");



        //hash value radiobutton
        this.hash_value_text = new DwtText({parent:this._parentView, name: "hash_value_text", id:"hash_value_text"});
        this.hash_value_text.setText("hash value");

     //   this.menu = new DwtMenuItem({parent:this._parentView, name:"menu"});

        this.checkbox1 = new DwtCheckbox({parent:this._parentView, name: "endless", id: "endless"});
        this.checkbox1.setText("endless");
        //this.radio1.setTextPosition(this.radio1.TEXT_LEFT);

        this.checkbox2 = new DwtCheckbox({parent:this._parentView, name: "from_to_time", id: "from_to_time"});
        this.checkbox2.setText("from to time");
        //this.radio2.setTextPosition(this.radio2.TEXT_LEFT);

        this.checkbox3 = new DwtCheckbox({parent:this._parentView, name: "one_time", id: "one_time"})
        this.checkbox3.setText("one time");
        //this.radio3.setTextPosition(this.radio3.TEXT_LEFT);

      /**      this.radio1 = new DwtCheckbox({parent:this._parentView, name: "endless", id: "endless"});
            this.radio1.setText("endless");
            //this.radio1.setTextPosition(this.radio1.TEXT_LEFT);

            this.radio2 = new DwtCheckbox({parent:this._parentView, name: "from_to_time", id: "from_to_time"});
            this.radio2.setText("from to time");
            //this.radio2.setTextPosition(this.radio2.TEXT_LEFT);

            this.radio3 = new DwtCheckbox({parent:this._parentView, name: "one_time", id: "one_time"})
            this.radio3.setText("one time");
            //this.radio3.setTextPosition(this.radio3.TEXT_LEFT);
       **/



    //	this._parentView.setSize("550", "600");
    //	this._parentView.getHtmlElement().style.overflow = "auto";
	//	this._parentView.getHtmlElement().innerHTML = this._constructDialogView();
 			this.omDlg = this._createDialog({title:"Openmeetings", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
			this.omDlg.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
			this.omDlg.popup();
				};


//-----------------------------------------------------------------------main dialog box end --------------------------------------------------------------------------------------------------------------------------------------------------------------------

/*** com_zimbra_om.prototype._constructDialogView =
		function(){
			var html = new Array();
			var i = 0;
html[i++] = "<form id = 'meetingform' class = 'rounded'>";
	html[i++] = "<h3> Openmeetings </h3>";
	
	html[i++] = "<div class = 'option'>";
			html[i++] = "<label for = 'profile'>profile:</label>";
			html[i++] = "<select id = 'profile'>";
            html[i++] = "<option> 1 </option>";
            html[i++] = "<option> 2 </option>";
            html[i++] = "<option> 3 </option>";
            html[i++] = "<option> 4 </option>";
            html[i++] = "<option> 5 </option>";
            html[i++] = "</select>";
			html[i++] = "<p class = 'hint'> Select Profile </p>";
	html[i++] = "</div>";
	
	html[i++] = "<div class = 'option'>";
			html[i++] = "<label for = 'room_id'>room_id</label>";
			html[i++] = "<select>";
			html[i++] = "<!-- fetch the list of rooms-->";
			html[i++] = "</select>";
			html[i++] = "<!-- option to create a new room-->";
			html[i++] = "<p class = 'hint'> Room </p>";
	html[i++] = "</div>";
	
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'language_id'> language </label>";
			html[i++] = "<input type = 'text' class = 'input' name='language_id' id = 'language_id'/>";
		html[i++] = "<!--	isPasswordProtected<input  type='checkbox' onClick = 'addInput('invitation_password','password_protected');' id='password_protected'><br/> -->";
			html[i++] = "<p class = 'hint'> select language </label>";
	html[i++] = "</div>";
	
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'invitation password'>invitation password</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'invitation_password' id= 'invitation_password'/>";
			html[i++] = "<p class = 'hint'> Invitation password </p>";
	html[i++] = "</div>";
	
	html[i++] = "<h3> Meeting time</h3></br>";
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'start_date'>Start date</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'start_date' id = 'start_date'/>";
            html[i++] = "<p class = 'hint'> Start date of the meeting </p>";
			
			html[i++] = "<label for ='start_time'>Time</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'start_time' id = 'start_time'/>";
			html[i++] = "<p class = 'hint'> Enter start time of meeting </p>";
	html[i++] = "</div>";
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'end_date'>End date</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'end_date' id = 'end_date'/>";
			html[i++] = "<p class = 'hint'> End date of the meeting </p>";
			
			html[i++] = "<label for = 'end_time'>Time</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'end_time' id = 'end_time'/><br/><!--calender should be available here showing the time of other meetings-->";
			html[i++] = "<p class = 'hint'>End time of the meeting </p>";
	html[i++] = "<div>";
	html[i++] = "<h3>hash validity:</h3>";
	
	html[i++] = "<div class = 'checkbox'>";
			html[i++] = "<label for = 'endless'>endless</label>";
			html[i++] = "<input type = 'checkbox' class = 'check' id = 'hash_endless'/>";
			html[i++] = "<p class = 'hint'> an endless hash</p>";
			
			html[i++] = "<label for = 'from to time'>from to time </label>";
			html[i++] = "<input type = 'checkbox' class = 'check' id = 'hash_fromtotime'/>";
			html[i++] = "<p class = 'hint'> limited time hash</p>";
			
			html[i++] = "<label for = 'onetime'>onetime</label>";
			html[i++] = "<input type = 'checkbox' class = 'check' id = 'hash_onetime'/><br/>";
			html[i++] = "<p class = 'hint'> onetime hash </p>";
	html[i++] = "</div>";
			
	html[i++] = "<h3>send email<h3>";
		html[i++] = "<!--	<input type = 'checkbox' onClick = 'addInput('email','send_email');' id = 'send_email'/><br/> -->";
	html[i++] = "<div class = 'field'>";
            html[i++] = "<label for = 'email_id'>email id</label>";
            html[i++] = "<input type = 'text' class = 'input' name = 'email_id' id = 'email_id'/><br/>";
			html[i++] = "<p class = 'hint'>Email id to send invitation.</p>";
	html[i++] = "</div>";
	
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'subject'>subject</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'subject' id = 'subject'/><br/>";
			html[i++] = "<p class = 'hint'>subject of the email</p>";
	html[i++] = "</div>";
			
	html[i++] = "<div class = 'field'>";
			html[i++] = "<label for = 'message'>message</label>";
			html[i++] = "<input type = 'text' class = 'input' name = 'message' id = 'message'/><br/>";
			html[i++] = "<p class = 'hint'> message to send</p>";
	html[i++] = "</div>";
	html[i++] = "</form>";
			return html.join("");
			};

**/

com_zimbra_om.prototype._okBtnListner=
			function(){
				profile = document.getElementById('profile');
				room_id = document.getElementById('room_id');
				language_id = document.getElementById('language_id');
				invitation_password = this.password_box.getValue();
				start_date = this.meeting_start_date_box.getValue();
                start_time = this.meeting_start_time_box.getValue();
                end_date = this.meeting_end_date_box.getValue();
                end_time = this.meeting_end_time_box.getValue();
                endless = document.getElementById('hash_endless');
                fromtotime = document.getElementById('hash_fromtotime');
                onetime = document.getElementById('hash_onetime');
                email_id = document.getElementById('email_id');
                subject = document.getElementById('subject');
                message = document.getElementById('message');
                this._parentView = new DwtComposite({parent:this.getShell(), noMaximize:false});
                this._parentView.setSize("550","500");
                this._parentView.getHtmlElement().style.overflow = "auto";
                this._parentView.setPosition(DwtControl.RELATIVE_STYLE);
                this._parentView.getHtmlElement().innerHTML = "  " + invitation_password + "<br/>" + start_date + "<br/>" + end_date + "<br/>";
                this.omDlg1 = this._createDialog({title:"Test", view:this._parentView, standardButtons : [DwtDialog.OK_BUTTON]});
                this.omDlg1.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
                this.omDlg1.popup();

                this.omDlg.popdown();
					};

//-----------------------------------------------------------------get Session-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//Ajax getSession request
com_zimbra_om.prototype._invoke=
			function(){
					var url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(com_zimbra_om._url);
			//		var request_url =   'http://demo.openmeetings.de/openmeetings/services/UserService/getSession';
					//AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getSession"], new AjxCallback(this, this._reponseHandler, postCallback) , true);

                    //AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getSession"], new AjxCallback(this, this._reponseHandler) , true);
                AjxRpc.invoke(null, url, null , new AjxCallback(this, this._reponseHandler) , true);
				};

				
//Ajax getSession request response
com_zimbra_om.prototype._responseHandler=
		function(response){
				try{
					sid = response.xml.getElementsByTagName("session_id");
					}catch(e){
							this._showErrorMessage(e);
							}
        };
//----------------------------------------------------------------------get session end -------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//--------------------------------------------------------------------------- login user -------------------------------------------------------------------------------------------------------------------------------------------------------------------------

//Ajax loginUser request
com_zimbra_om.prototype.login_user = function(){
				var url =  'http://demo.openmeetings.de/openmeetings/services/UserService/loginUser?SID=' +
									sid + '&username='+
									username + 
									'&userpass=' + 
									password;
                var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
				
				//AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/loginUser"], new AjxCallback(this, this._reponseHandler1, postCallback), true);
                  AjxRpc.invoke(null, request_url, null , new AjxCallback(this, this._reponseHandler1), true);
        };


//Ajax loginUser request response
com_zimbra_om.prototype._responseHandler1= 
				function(postCallback, response){
								//will have to check if the returned value is positive or negative. if negative show the  error message.
								try{
									var errorid= response.getElementsByTagName("return");
									}catch(e){
										this._showErrorMessage(e);
											}
								if (errorid < 0){
										this._notlogged(errorid);
									}
						};
						

//Ajax getErrorByCode request
com_zimbra_om.prototype._notlogged = function (errorid){

				var language_id = 1;
				var url =   'http://demo.openmeetings.de/openmeetings/services/UserService/getErrorByCode?SID=' +
									sid +
									'&errorid='+
									errorid +
									'&language_id='+
									1;
                var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(url);
				//AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/getErrorByCode"], new AjxCallback(this, this._reponseHandler2, postCallback), true);
                  AjxRpc.invoke(null, request_url, null , new AjxCallback(this, this._reponseHandler2), true);
								};
							

// Ajax getErrorByCode request response							
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

//----------------------------------------------------------------------login user end----------------------------------------------------------------------------------------------------------------------------------------------------------------------------



//-----------------------------------------------------------------------generating invitation hash-----------------------------------------------------------------------------------------------------------------------------------------------------------------
//Ajax request for generating invitation hash

var url = 'http://demo.openmeetings.de/openmeetings/services/UserService/loginUser?SID=' +
					sid + '&username=' + username + '&message=' + message +
					'&baseurl=' + baseurl + '&email=' + email + '&subject=' +
					subject + '&room_id=' + room_id + '&conferencedomain=' +
					conferencedomain + '&isPasswordProtected=' + isPasswordProtected + 
					'&invitationpass=' + invitationpass + '&valid=' + valid + '&validFromDate=' +
					validfromdate + '&validFromTime=' + validFromTime + '&validToDate=' + validFromDate +
					'&validToTime=' + validToTime + '&language_id=' + language_id + '&sendMail=' + sendMail;
var request_url = ZmZimletBase.PROXY + AjxStringUtil.urlComponentEncode(com_zimbra_om._url);
//AjxRpc.invoke(request_url, ["Content-Type", "application/soap+xml;charset=utf-8", "SOAPAction", "http://url/openmeetings/services/UserService/loginUser"], new AjxCallback(this, this._reponseHandler3, postCallback), true);
AjxRpc.invoke(null , request_url, null , new AjxCallback(this, this._reponseHandler3), true);


//Ajax request for generating invitation hash response
com_zimbra_om.prototype._responseHandler3 =
					function(postCallback,response){
						try{
							var invitationHash = response.xml.getElementById("sendInvitationHash");
							} catch(e){
									this._showErrorMsg(e);
								}
						var invitation_url = 'http://demo.openmeetings.de/openmeetings/?invitationHash=' + invitationHash ;
					}


//-------------------------------------------------------------------------------generate invitation hash end --------------------------------------------------------------------------------------------------------------------------------------------------------