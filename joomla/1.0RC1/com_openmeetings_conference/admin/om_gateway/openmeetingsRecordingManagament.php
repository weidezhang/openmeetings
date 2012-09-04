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

class openmeetingsRecordingManagament {
	function setUserObjectAndGenerateRecordingHashByURL(&$data) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			return $openmeetings_gateway->setUserObjectAndGenerateRecordingHashByURL($data);
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration createRoomWithModeration";
		}
	}

	function getFlvRecordingByExternalRoomType() {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			return 	$openmeetings_gateway->getFlvRecordingByExternalRoomType();
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration createRoomWithModeration";
		}
	}

	function getFlvRecordingByExternalRoomTypeAndId($userId) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			return 	$openmeetings_gateway->getFlvRecordingByExternalRoomTypeAndCreator($userId);
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration createRoomWithModeration";
		}
	}

	function getFlvRecordingByExternalUserId($user_id) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			return 	$openmeetings_gateway->getFlvRecordingByExternalUserId($user_id);
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration createRoomWithModeration";
		}
	}

	function deleteFlvRecording($FlvRecordingId) {
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			return $openmeetings_gateway->deleteFlvRecording($FlvRecordingId);
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration createRoomWithModeration";
		}
	}
}
?>
