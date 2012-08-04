<?php
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

if (!defined('AT_INCLUDE_PATH')) { exit; }
//require('SOAP_openmeetings.php');
require('REST_openmeetings.php');

class Openmeetings {
	var $_sid = '';		//Openmeetings session id
	var $_course_id = '';
	var $_member_id = '';
	var $_group_id = '';

	//Constructor
	function Openmeetings($course_id, $member_id, $group_id=0){
		$this->_course_id	= abs($course_id);
		$this->_member_id	= abs($member_id);
		$this->_group_id	= abs($group_id);

	}

	/**
	 * Login to openmeetings
	 * Login process is, login, saveuserinstance
	 */
	/*function om_login() {
		global $_config;
		//$om = new REST_openmeetings($_config['openmeetings_location'].'/services/UserService/');
		$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/UserService?wsdl');
		$param = array (	'username' => $_config['openmeetings_username'], 
							'userpass' => $_config['openmeetings_userpass']);
		/**
		 * Login to the openmeetings
		 * ref: http://code.google.com/p/openmeetings/wiki/DirectLoginSoapGeneralFlow
		 
		$result = $om->login($param);
		if ($result < 0){
			debug($om->getError($result), 'error');
			return;
		} 
		
		//If no error, then get the generated OM session id
		$this->_sid = $om->getSid();

		//exit($this->_sid);

		//Retrieve members information
		$sql = 'SELECT login, first_name, last_name, email FROM '.TABLE_PREFIX.'members WHERE member_id='.$this->_member_id;
		$result = mysql_query($sql);
		$row = mysql_fetch_assoc($result);

		// Save user instance
		$params = array(
					"username"				=> $row['login'],
					"firstname"				=> $row['first_name'],
					"lastname"				=> $row['last_name'],
					"profilePictureUrl"		=> '',
					"email"					=> $row['email']
				  );
		$om->saveUserInstance($params);
	}*/

	//new function
	function om_login() {

		global $_config;
		
		$om = new REST_openmeetings($_config['openmeetings_location'].'/services/');
		
		//$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/UserService?wsdl');
		$param = array ('username' => $_config['openmeetings_username'], 
				'userpass' => $_config['openmeetings_userpass']);
		
		/**
		 * Login to the openmeetings
		 * ref: http://code.google.com/p/openmeetings/wiki/DirectLoginSoapGeneralFlow
		 */
		$result = $om->login($param);

		//print_r($result);
		//echo '<br><br>';

		if ($result < 0){
			debug($om->getError($result), 'error');
			return;
		} 
		
		//If no error, then get the generated OM session id
		$this->_sid = $om->getSid();
		//echo $this->_sid;
		//echo '<br><br>';
		//exit($this->_sid);

		//Retrieve members information
		$sql = 'SELECT login, first_name, last_name, email FROM '.TABLE_PREFIX.'members WHERE member_id='.$this->_member_id;
		//echo $sql;
		//echo '<br><br>';
		$result = mysql_query($sql);
		$row = mysql_fetch_assoc($result);

		// Save user instance
		$params = array(
					"username"				=> $row['login'],
					"firstname"				=> $row['first_name'],
					"lastname"				=> $row['last_name'],
					"profilePictureUrl"		        => '',
					"email"					=> $row['email'],
					// TODO: determine if user is instructor here, 1 vs. 0
					"becomeModeratorAsInt"                  => 1
				  );

		
		$om->saveUserInstance($params);
	}

	/**
	 * Add a room to the db iff it has not been created.  Each course should only have one room to it.
	 * @param int		sid is the auth session id that was logged in into openmeetings.
	 * @param array		the specification for openmeetings 
	 * @return room # of the created room, or the room # of the existed room
	 */
	function om_addRoom($room_name, $om_param=array()){
		//exit('adding room failed');
		global $_config;

		if ($this->_course_id < 0){
			return false;
		}

		//Check if the room has already been created for this
		if (($room_id = $this->om_getRoom()) !=false){
			//instead of returning room id, we might have to delete it and carry on.
			return $room_id;
		}
		
		//Add this room
		//$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/RoomService?wsdl');
		$om = new REST_openmeetings($_config['openmeetings_location'].'/services/');

		/*$param = array (	
					'SID'					=> $this->_sid,
					'name'					=> $room_name,
					'roomtypes_id'			=> $om_param['openmeetings_roomtype'],
					'numberOfPartizipants'	=> $om_param['openmeetings_num_of_participants'],
					'ispublic'				=> $om_param['openmeetings_ispublic'],
					'videoPodWidth'			=> $om_param['openmeetings_vid_w'],
					'videoPodHeight'		=> $om_param['openmeetings_vid_h'],
					'showWhiteBoard'		=> $om_param['openmeetings_show_wb'],
					'whiteBoardPanelWidth'	=> $om_param['openmeetings_wb_w'],
					'whiteBoardPanelHeight'	=> $om_param['openmeetings_wb_h'],
					'showFilesPanel'		=> $om_param['openmeetings_show_fp'],
					'filesPanelHeight'		=> $om_param['openmeetings_fp_h'],
					'filesPanelWidth'		=> $om_param['openmeetings_fp_w']
					);*/

		$param = array (	
					'SID'					=> $this->_sid,
					'name'					=> $room_name,
					'roomtypes_id'			=> $om_param['openmeetings_roomtype'],
					'numberOfPartizipants'	=> $om_param['openmeetings_num_of_participants'],
					'ispublic'				=> $om_param['openmeetings_ispublic'],
					'appointment'			=> 0,
					'isDemoRoom'                    => 0,
					'demoTime'                      => 1000,
					// TODO Check if it is
					'isModeratedRoom'		=>0
					);
		$result = $om->addRoom($param);
		//TODO: Check for error, and handles success/failure
		if ($result){
			//TODO: On success, add to DB entry.
			$sql = 'INSERT INTO '.TABLE_PREFIX.'openmeetings_rooms SET rooms_id='.$result['return'].', course_id='.$this->_course_id 
				 . ', owner_id=' . $this->_member_id;
			$rs  = mysql_query($sql);
			if (!$rs){
				return false;
			}
			$om_id = mysql_insert_id();
			$sql = 'INSERT INTO '.TABLE_PREFIX."openmeetings_groups SET om_id=$om_id, group_id=$this->_group_id";
			$rs = mysql_query($sql);
	
			if ($rs){
				return $result['return'];
			}
		}
		return false;
	}

	
	/**
	 * update room
	 */
	function om_updateRoom($room_id, $om_param=array()){
		global $_config;

		//update this room
		//$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/RoomService?wsdl');
		$om = new REST_openmeetings($_config['openmeetings_location'].'/services/');
		/*$param = array (	
					'SID'					=> $this->_sid,
					'rooms_id'				=> $room_id,
					'name'					=> $om_param['openmeetings_room_name'],
					'roomtypes_id'			=> $om_param['openmeetings_roomtype'],
					'numberOfPartizipants'	=> $om_param['openmeetings_num_of_participants'],
					'ispublic'				=> $om_param['openmeetings_ispublic'],
					'videoPodWidth'			=> $om_param['openmeetings_vid_w'],
					'videoPodHeight'		=> $om_param['openmeetings_vid_h'],
					'showWhiteBoard'		=> $om_param['openmeetings_show_wb'],
					'whiteBoardPanelWidth'	=> $om_param['openmeetings_wb_w'],
					'whiteBoardPanelHeight'	=> $om_param['openmeetings_wb_h'],
					'showFilesPanel'		=> $om_param['openmeetings_show_fp'],
					'filesPanelHeight'		=> $om_param['openmeetings_fp_h'],
					'filesPanelWidth'		=> $om_param['openmeetings_fp_w']
					);*/	
		$param = array (	
					'SID'					=> $this->_sid,
					'rooms_id'				=> $room_id,
					'name'					=> $om_param['openmeetings_room_name'],
					'roomtypes_id'			=> $om_param['openmeetings_roomtype'],
					'numberOfPartizipants'	=> $om_param['openmeetings_num_of_participants'],
					'ispublic'				=> $om_param['openmeetings_ispublic'],
					'appointment'			=> 0,
					'isDemoRoom'                    => 0,
					'demoTime'                      => 1000,
					// TODO Check if it is
					'isModeratedRoom'		=> 0
					);

		$result = $om->updateRoom($param);
		return $result;
	}

	/**
	 * Retrieve Session id
	 */
	function getSid(){
		return $this->_sid;
	}

	/**
	 * Checks if there is a room for the given course id.
	 *
	 * @param	course id
	 * @return	the room id if there is a room already assigned to this course; false otherwise
	 */
	function om_getRoom(){
//		$sql = 'SELECT rooms_id FROM '.TABLE_PREFIX.'openmeetings_rooms INNER JOIN '.TABLE_PREFIX."openmeetings_groups WHERE 
//				course_id = $this->_course_id AND owner_id = $this->_member_id AND group_id = $this->_group_id";
		$sql = 'SELECT rooms_id FROM '.TABLE_PREFIX.'openmeetings_rooms r NATURAL JOIN '.TABLE_PREFIX."openmeetings_groups g WHERE 
				course_id = $this->_course_id AND group_id = $this->_group_id";
		$result = mysql_query($sql);
		if (mysql_numrows($result) > 0){
			$row = mysql_fetch_assoc($result);
			//instead of returning room id, we might have to delete it and carry on.
			return $row['rooms_id'];
		}
		return false;
	}


	/**
	 * Get room obj form the given room id
	 */
	function om_getRoomById($room_id){
		global $_config;
		if ($room_id == ''){
			return false;
		}
		//$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/RoomService?wsdl');
		$om = new REST_openmeetings($_config['openmeetings_location'].'/services/');
		$param = array (	
					'SID'			=> $this->_sid,
					'rooms_id'		=> $room_id
					);
		$result = $om->getRoomById($param);
		return $result;
	}

	/**
	 * Set the group id
	 * @param	int	group id.
	 */
	function setGid($gid){
		$this->_group_id = $gid;
	}

	/**
	 * Delete a room
	 */
	function om_deleteRoom($room_id){
		global $_config;
		//$om = new SOAP_openmeetings($_config['openmeetings_location'].'/services/RoomService?wsdl');
		$om = new REST_openmeetings($_config['openmeetings_location'].'/services/');
		$param = array (	
					'SID'			=> $this->_sid,
					'rooms_id'		=> $room_id
					);

		$result = $om->deleteRoom($param);
		$sql = 'DELETE r, g FROM (SELECT om_id FROM '.TABLE_PREFIX."openmeetings_rooms WHERE rooms_id=$room_id) AS t, ".TABLE_PREFIX
				.'openmeetings_rooms r NATURAL JOIN '.TABLE_PREFIX.'openmeetings_groups g WHERE r.om_id =t.om_id';
		mysql_query($sql);
	}


	/**
	 * Return true if this user created the given room.
	 * @param	int	room id
	 * @return	true if it is, false otherwise.
	 */
	function isMine($room_id){
		$sql = 'SELECT * FROM '.TABLE_PREFIX."openmeetings_rooms WHERE rooms_id=$room_id AND owner_id=$this->_member_id";
		$result = mysql_query($sql);
		if (mysql_numrows($result) > 0){
			return true;
		} 
		return false;
	}
}
?>
