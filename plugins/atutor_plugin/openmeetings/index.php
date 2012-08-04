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

define('AT_INCLUDE_PATH', '../../include/');
require (AT_INCLUDE_PATH.'vitals.inc.php');
require ('lib/openmeetings.class.php');
require ('lib/openmeetings.inc.php');

//css
$_custom_css = $_base_path . 'mods/openmeetings/module.css'; // use a custom stylesheet

//local variables
$course_id = $_SESSION['course_id'];

// Check access
checkAccess($course_id);

//Header begins here
require (AT_INCLUDE_PATH.'header.inc.php');

//Initiate Openmeeting
$om_obj = new Openmeetings($course_id, $_SESSION['member_id']);

//Login
// failing here

$om_obj->om_login();

//Handles form actions
if (isset($_GET['delete']) && isset($_GET['room_id'])){
	//have to makesure the user really do have permission over the paramater room id
	$_GET['room_id'] = intval($_GET['room_id']);
	if ($om_obj->isMine($_GET['room_id'])){
		$om_obj->om_deleteRoom($_GET['room_id']);
		$msg->addFeedback('OPENMEETINGS_DELETE_SUCEEDED');
	} else {
		$msg->addError('OPENMEETINGS_DELETE_FAILED');
	}
}

//Course meetings
include_once('html/course_meeting.inc.php');

//Group meetings
include_once('html/group_meeting.inc.php');

require (AT_INCLUDE_PATH.'footer.inc.php');
?>
