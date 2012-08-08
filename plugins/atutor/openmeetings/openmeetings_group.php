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
//$_custom_css = $_base_path . 'mods/openmeetings/module.css'; // use a custom stylesheet

//local variables
$course_id = $_SESSION['course_id'];

// Check access
checkAccess($course_id);

$_GET['gid'] = intval($_GET['gid']);

//Initiate Openmeeting
$om_obj = new Openmeetings($course_id, $_SESSION['member_id']);

//Login
$om_obj->om_login();

//Group meetings
$sql = "SELECT title FROM ".TABLE_PREFIX."groups WHERE group_id=$_GET[gid] ORDER BY title";
//TODO: Check group permission from group table.
$result = mysql_query($sql, $db);
$row = mysql_fetch_assoc($result);

if (mysql_numrows($result) == 0){
	echo '<div class="openmeetings">'._AT('openmeetings_no_group_meetings').'</div>';
	require (AT_INCLUDE_PATH.'footer.inc.php');
	exit;
} 

//Check in the db and see if this group has a meeting alrdy, create on if not.
$om_obj->setGid($_GET['gid']);
if ($om_obj->om_getRoom()){
	//Log into the room
	$room_id = $om_obj->om_addRoom($room_name);
	header('Location: '.AT_BASE_HREF.'mods/openmeetings/view_meetings.php?room_id='.$room_id.SEP.'sid='.$om_obj->getSid());
	exit;
} else {
	//Header begins here
	require (AT_INCLUDE_PATH.'header.inc.php');
	echo '<div class="openmeetings">'.$row['title'].'<a href="mods/openmeetings/add_group_meetings.php?group_id='.$_GET['gid'].'"> Start a conference </a></div>';
	require (AT_INCLUDE_PATH.'footer.inc.php');
}
?>
