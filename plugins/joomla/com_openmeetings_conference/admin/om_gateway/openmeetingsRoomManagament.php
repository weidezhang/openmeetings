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

defined('_JEXEC') or die('Restricted access');

require_once("openmeetings_gateway.php");


class openmeetingsRoomManagament {
	function createRoomWithModeration(&$data) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			$openmeetings->name = $data['name'];
			$openmeetings->roomtypes_id = $data['roomtype_id'];
			$openmeetings->comment = $data['comment'];
			$openmeetings->numberOfPartizipants = $data['number_of_partizipants'];
			$openmeetings->ispublic = $data['is_public'];
			$openmeetings->appointment = $data['appointment'];
			$openmeetings->isDemoRoom = 0;
			$openmeetings->demoTime = "";
			$openmeetings->isModeratedRoom = $data['is_moderated_room'];

			$roomid = $openmeetings_gateway->openmeetings_createroomwithmod($openmeetings);

			return $roomid;
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
		}
	}

	function updateRoomWithModeration(&$data) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			$openmeetings->name = $data['name'];
			$openmeetings->room_id = $data['room_id'];
			$openmeetings->roomtypes_id = $data['roomtype_id'];
			$openmeetings->comment = $data['comment'];
			$openmeetings->numberOfPartizipants = $data['number_of_partizipants'];
			$openmeetings->ispublic = $data['is_public'];
			$openmeetings->appointment = $data['appointment'];
			$openmeetings->isDemoRoom = 0;
			$openmeetings->demoTime = "";
			$openmeetings->isModeratedRoom = $data['is_moderated_room'];

			$roomid = $openmeetings_gateway->updateRoomWithModeration($openmeetings);
			return $roomid;
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
		}
	}

	function deleteRoom($data) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			$openmeetings->room_id = $data['room_id'];
			$roomid = $openmeetings_gateway->deleteRoom($openmeetings);
			return $roomid;
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration";
		}
	}
}
?>
