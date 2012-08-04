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

if (empty($_SESSION['groups'])) {
	echo '<div class="openmeetings"><h5>'._AT('openmeetings_group_conference').'</h5>';
	echo _AT('openmeetings_no_group_meetings').'</div>';
} else {
	echo '<div class="openmeetings"><h5>'._AT('openmeetings_group_conference').'</h5>';
	$group_list = implode(',', $_SESSION['groups']);
	$sql = "SELECT group_id, title FROM ".TABLE_PREFIX."groups WHERE group_id IN ($group_list) ORDER BY title";

	$result = mysql_query($sql, $db);

	echo '<ul>';
	//loop through each group and print out a link beside them 
	while ($row = mysql_fetch_assoc($result)) {
		//Check in the db and see if this group has a meeting alrdy, create on if not.
		$om_obj->setGid($row['group_id']);
		if ($om_obj->om_getRoom()){
			//Log into the room
			$room_id = $om_obj->om_addRoom($room_name);
			echo '<li>'.$row['title'].' <a href="mods/openmeetings/view_meetings.php?room_id='.$room_id.SEP.'sid='.$om_obj->getSid().'"> Room-id: '.$room_id.'</a>';
			if ($om_obj->isMine($room_id) || authenticate(AT_PRIV_OPENMEETINGS, true)) {
				//if 'I' created this room, then I will have the permission to edit/delete it from the database.
				echo ' <a href="mods/openmeetings/add_group_meetings.php?edit_room=yes&room_id='.$room_id.'">['._AT('edit').']</a>';
				echo ' <a href="mods/openmeetings/openmeetings_delete.php?room_id='.$room_id.'">['._AT('delete').']</a>';
				
			}
			echo '</li>';
		} else {
			echo '<li>'.$row['title'].' <a href="mods/openmeetings/add_group_meetings.php?group_id='.$row['group_id'].'"> Start a conference </a>'.'</li>';
		}
	}
	echo '</ul></div>';
}

?>
