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
// Check to ensure this file is included in Joomla!
defined('_JEXEC') or die();

jimport( 'joomla.application.component.model' );

require_once('components/com_openmeetings_conference/om_gateway/openmeetingsRoomManagament.php');
require_once("components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");


class RoomsModelRoom extends JModel
{

	/**
	 * Constructor that retrieves the ID from the request
	 *
	 * @access    public
	 * @return    void
	 */
	function __construct()
	{
		parent::__construct();

		$array = JRequest::getVar('cid',  0, '', 'array');
		$this->setId((int)$array[0]);
	}

	/**
	 * Method to set the hello identifier
	 *
	 * @access    public
	 * @param    int Hello identifier
	 * @return    void
	 */
	function setId($id)
	{
		// Set id and wipe data
		$this->_id        = $id;
		$this->_data    = null;
	}

	/**
	 * Method to get a hello
	 * @return object with data
	 */

	function &getData()
	{
		// Load the data
		if (empty( $this->_data )) {
			$query = ' SELECT * FROM #__om_rooms '.
                '  WHERE id = '.$this->_id;
			$this->_db->setQuery( $query );
			$this->_data = $this->_db->loadObject();
		}
		if (!$this->_data) {
			$this->_data = new stdClass();
			$this->_data->id = 0;
			$this->_data->room = null;
		}
		return $this->_data;
	}


	/**
	 * Method to store a record
	 *
	 * @access    public
	 * @return    boolean    True on success
	 */
	function store()
	{
		$row =& $this->getTable();
		$data = JRequest::get( 'post' );

		if (array_key_exists("flexigroups", $data)) {
			$flexigroups = $data["flexigroups"];
		}
		$owners = $data["owner"];
		$data["owner"] = -1;

		$repeat_type_bool = array_key_exists("repeat_type",$data);
		if (!$repeat_type_bool) {
			$data["repeat_type"] = 0;
		}
		//Update room
		if(!empty($data[room_id])){
			$omRoomManagament = 	new openmeetingsRoomManagament();
			$room_id = $omRoomManagament-> updateRoomWithModeration($data);

			//Make sure the Openmeetings Room was succsefully created
			if($room_id < 1){
				$this->setError('Could not login User to OpenMeetings, check your OpenMeetings Module Configuration');
				return false;
			}
			//Create new room
		}else{
			//create a Meetingroom in Openmeetings
			$omRoomManagament = 	new openmeetingsRoomManagament();
			$room_id = $omRoomManagament-> createRoomWithModeration($data);
			$data[room_id] = $room_id;

			//Make sure the Openmeetings Room was succsefully created
			if($room_id < 1){
				$this->setError('Could not login User to OpenMeetings, check your OpenMeetings Module Configuration');
				return false;
			}
		}

		// Bind the form fields to the  table
		if (!$row->bind($data)) {
			$this->setError($this->_db->getErrorMsg());
			return false;
		}

		// Make sure the hello record is valid
		if (!$row->check()) {
			$this->setError($this->_db->getErrorMsg());
			return false;
		}

		// Store the web link table to the database
		if (!$row->store()) {
			$this->setError($this->_db->getErrorMsg());
			return false;
		}
		if (!is_int($data["number_of_partizipants"])) {
			//TODO investigate!!
			//weird behavior number_of_partizipants added as 0 (not NULL)
			$query = ' UPDATE #__om_rooms SET number_of_partizipants = NULL WHERE room_id = ' . $room_id;
			$this->_db->setQuery( $query );
			$this->_db->query();
		}

		//Users individually
		$query = ' DELETE FROM #__om_rooms_users ' .
			' WHERE om_room_id = '. $row->id . '';
		$this->_db->setQuery( $query );
		$this->_db->query();

		foreach ($owners as $key => $owner_user_id) {
			//print_r("<BR/>".$owner_user_id);

			$query = ' INSERT INTO #__om_rooms_users '.
                '  (om_room_id,user_id) ' .
                ' VALUES ' .
                ' ('. $row->id .','. $owner_user_id .') ';
			$this->_db->setQuery( $query );
			$this->_db->query();

		}

		//FlexiGroups

		$query = ' DELETE FROM #__om_rooms_flexigroups ' .
			' WHERE om_room_id = '. $row->id . '';
		$this->_db->setQuery( $query );
		$this->_db->query();

		foreach ($flexigroups as $key => $flexigroup_id) {
			//print_r("<BR/>".$owner_user_id);

			$query = ' INSERT INTO #__om_rooms_flexigroups '.
                '  (om_room_id,flexigroup_id) ' .
                ' VALUES ' .
                ' ('. $row->id .','. $flexigroup_id .') ';
			$this->_db->setQuery( $query );
			$this->_db->query();

		}
		return true;
	}

	/**
	 * Method to delete record(s)
	 *
	 * @access    public
	 * @return    boolean    True on success
	 */
	function delete()
	{
		$cids = JRequest::getVar( 'cid', array(0), 'post', 'array' );
		$row =& $this->getTable();

		foreach($cids as $cid) {
			if (!$row->delete( $cid )) {
				$this->setError( $row->getErrorMsg() );
				return false;
			}
		}

		return true;
	}

}
