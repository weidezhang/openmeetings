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
require_once(JPATH_BASE."/administrator/components/com_openmeetings_conference/om_gateway/openmeetingsRoomManagament.php");
require_once(JPATH_BASE."/administrator/components/com_openmeetings_conference/om_gateway/openmeetingsRecordingManagament.php");
require_once(JPATH_BASE."/administrator/components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");

//don't allow other scripts to grab and execute our file
defined('_JEXEC') or die('Direct Access to this location is not allowed.');


$db = &JFactory::getDBO();

$user =& JFactory::getUser();
$userid=$user->get('id');


$query = "SELECT o_room.*, TIMESTAMPDIFF(MINUTE,TIMESTAMP(o_room.date_type,o_room.time_type),NOW()) as delta_time " .
			"FROM #__om_rooms as o_room " .
			"LEFT JOIN #__om_rooms_users as o_room_user ON o_room.id = o_room_user.om_room_id " .
			"LEFT JOIN #__om_rooms_flexigroups as o_rooms_flexigroups ON o_room.id = o_rooms_flexigroups.om_room_id " .
			"WHERE  " .
			"(  " .
			" o_room.is_public = 1 OR o_room_user.user_id = " . $userid . " " .
			") " .
			"AND  " .
			"( " .
			" o_room.room_validity = 0  " .
			"  OR " .
			" ( " .
			"  TIMESTAMPDIFF(MINUTE,TIMESTAMP(o_room.date_type,o_room.time_type),NOW()) > 0 AND TIMESTAMPDIFF(MINUTE,TIMESTAMP(o_room.date_type,o_room.time_type),NOW()) < o_room.duration " .
			" ) " .
			"  OR " .
			" ( " .
			"  o_room.weekday_type = DAYOFWEEK( NOW( ) ) " .
			" ) " .
			") " .
			"GROUP BY o_room.id ";

$db->setQuery($query);
$items = ($items = $db->loadObjectList())?$items:array();

echo "<ul>";
if ($items) {
	foreach ($items as $item) {
		echo "<li><img src='components/com_openmeetings_conference/templates/conference/user_people.png' align='absmiddle' border='0'> <a href='".JURI::root().'index.php?option=com_openmeetings_conference&view=om&format=raw&room=' . urlencode($item->room_id) . "' target='_BLANK'><B>".$item->name."</B></a></li>";
	}
} else {
	echo "<li>No public or owned video conference rooms.</li>";
}
echo "</ul>";
echo "<span class='header-3'>Openmeetings Recordings</span>";



//Recordings
$om_recordings=new openmeetingsRecordingManagament();
$om_recordings_return = $om_recordings->getFlvRecordingByExternalUserId($user->id);

if($om_recordings_return != null) {
	if (!is_array($om_recordings_return[0])) {
		$om_recordings_return = array($om_recordings_return);
	}
	echo "<ul>";
	for ($i = 0; $i < count($om_recordings_return); $i++) {
		echo "<li><img src='components/com_openmeetings_conference/templates/conference/Webcam_16.png' align='absmiddle' border='0'> <a href='".JURI::root().'index.php?option=com_openmeetings_conference&view=rec_link&format=rec_link&rec=' . urlencode($om_recordings_return[$i]['flvRecordingId']) . "' target='_BLANK'><B>".$om_recordings_return[$i]['fileName']."</B></a>"
		. "<a href='".JURI::root().'index.php?option=com_openmeetings_conference&view=delrec&format=delrec&rec=' . urlencode($om_recordings_return[$i]['flvRecordingId']) . "' target='_self'><img src='components/com_openmeetings_conference/templates/conference/process-stop.png' align='absmiddle' border='0'></a></li>";
	}
	echo "</ul>";
}
?>
