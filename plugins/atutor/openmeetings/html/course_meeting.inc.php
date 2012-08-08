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

//Check if the room is open, if not.  Print error msg to user.
if (!$om_obj->om_getRoom()):
?>
	<div class="openmeetings">
		<h5><?php echo _AT('openmeetings_course_conference'); ?></h5>
		<?php echo _AT('openmeetings_no_course_meetings'); ?>
	</div>

<?php
else:
	//Get the room id
	//TODO: Course title added/removed after creation.  Affects the algo here.
	if (isset($_SESSION['course_title']) && $_SESSION['course_title']!=''){
		$room_name = $_SESSION['course_title'];
	} else {
		$room_name = 'course_'.$course_id;
	}

	//Log into the room
	$room_id = $om_obj->om_addRoom($room_name);
	?>
	<div class="openmeetings">
		<h5><?php echo _AT('openmeetings_course_conference'); ?></h5>
		<ul>
			<li><a href="mods/openmeetings/view_meetings.php?room_id=<?php echo $room_id . SEP; ?>sid=<?php echo $om_obj->getSid(); ?>"><?php echo $_SESSION['course_title']; ?></a></li>
		</ul>
	</div><br/>

<?php endif; ?>
