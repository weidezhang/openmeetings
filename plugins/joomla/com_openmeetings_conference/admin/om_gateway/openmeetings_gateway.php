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

require_once('lib/openmeetings_rest_service.php');

class openmeetings_gateway {
	var $session_id = "";
	var $params = "";
	
	function getUrl() {
		if (!$this->params) {
			$this->params = &JComponentHelper::getParams('com_openmeetings_conference');
		}
		return $this->params->get('url') . "/" . $this->params->get('context') . "/services/";
	}

	function var_to_str($in) {
		if(is_bool($in)) {
			return $in ? "true" : "false";
		} else {
			return $in;
		}
	}
	
	function getRestService() {
		$restService = new openmeetings_rest_service();
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}
		return $restService;
	}
	
	function checkResult($restService, $result) {
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				//return $result["return"];
				return $result;
			}
		}
		return -1;
	}

	/**
	 * TODO: Get Error Service and show detailed Error Message
	 */
	function openmeetings_loginuser() {
		$restService = getRestService();

		$response = $restService->call($this->getUrl()."UserService/getSession","session_id");

		if (-1 != checkResult($restService, $response)) {
			$this->session_id = $response;

			$result = $restService->call($this->getUrl()."UserService/loginUser?"
				. "SID=".$this->session_id
				. "&username=" . urlencode($this->params->get('username'))
				. "&userpass=" . urlencode($this->params->get('password'))
				);

			return -1 == checkResult($restService, $result);
		}
		return false;
	}

	function getFlvRecordingByExternalUserId($user_id) {
		$restService = getRestService();
		
		$url = $this->getUrl()."RoomService/getFlvRecordingByExternalUserId?" .
				"SID=".$this->session_id .
				"&externalUserId=" . $user_id;
		
		$result = $restService->call($url,"");
			
		return checkResult($restService, $result);
	}

	function getFlvRecordingByExternalRoomTypeAndCreator($insertedBy) {
		$restService = getRestService();
		
		$url = $this->getUrl()."RoomService/getFlvRecordingByExternalRoomTypeAndCreator?" .
				"SID=".$this->session_id .
				"&insertedBy=" . urlencode($insertedBy) .
				"&externalRoomType=" . urlencode($this->params->get('moduleKey'));
		
		$result = $restService->call($url,"");
			
		return checkResult($restService, $result);
	}

	function getFlvRecordingByExternalRoomType() {
		$restService = getRestService();
		
		$url = $this->getUrl()."RoomService/getFlvRecordingByExternalRoomType?" .
				"SID=".$this->session_id .
				"&externalRoomType=".urlencode($this->params->get('moduleKey'));
		
		$result = $restService->call($url,"");
			
		return checkResult($restService, $result);
	}

	function deleteFlvRecording($flvRecordingId) {
		$restService = getRestService();
		
		$url = $this->getUrl()."RoomService/deleteFlvRecording?" .
				"SID=" . $this->session_id .
				"&flvRecordingId=" . $flvRecordingId;
		
		$result = $restService->call($url,"");
			
		return checkResult($restService, $result);
	}

	function setUserObjectAndGenerateRecordingHashByURL($openmeetings) {
		$restService = getRestService();
		$result = $restService->call($this->getUrl().'UserService/setUserObjectAndGenerateRecordingHashByURL?'.
				'SID='.$this->session_id .
				'&username='.urlencode($openmeetings->username) .
				'&firstname='.urlencode($openmeetings->firstname) .
				'&lastname='.urlencode($openmeetings->lastname) .
				'&externalUserId='.$openmeetings->externalUserId .
				'&externalUserType='.urlencode($this->params->get('moduleKey')) .
				'&recording_id='.$openmeetings->recording_id,
				'return'
		);
		
		return checkResult($restService, $result);
	}

	function openmeetings_createroomwithmod($openmeetings) {
		$restService = getRestService();
		
		$url = $this->getUrl()."RoomService/addRoomWithModerationAndRecordingFlags?" .
			"SID=" . $this->session_id .
			"&name" . urlencode($openmeetings->name) .
			"&roomtypes_id" . $openmeetings->roomtypes_id .
			"&comment" . $openmeetings->comment .
			"&numberOfPartizipants" . $openmeetings->numberOfPartizipants .
			"&ispublic" . $openmeetings->ispublic .
			"&appointment" . $openmeetings->appointment .
			"&isDemoRoom" . $openmeetings->isDemoRoom .
			"&demoTime" . $openmeetings->demoTime .
			"&isModeratedRoom" . $openmeetings->isModeratedRoom .
			"&externalRoomType" . urlencode($this->params->get('moduleKey')) .
			"&allowUserQuestions" . "true" .
			"&isAudioOnly" . "false" .
			"&waitForRecording" . "true" .
			"&allowRecording" . "true";
		
		
		$result = $restService->call($url,"");
			
		return checkResult($restService, $result);
	}

	function openmeetings_setUserObjectAndGenerateRoomHash($username, $firstname, $lastname,
		$profilePictureUrl, $email, $externalUserId, $room_id, $becomeModeratorAsInt,
		$showAudioVideoTestAsInt) {
		$restService = getRestService();
		
		$result = $restService->call($this->getUrl()."UserService/setUserObjectAndGenerateRoomHash?" .
				"SID=".$this->session_id.
				"&username=".urlencode($username).
				"&firstname=".urlencode($firstname).
				"&lastname=".urlencode($lastname).
				"&profilePictureUrl=".urlencode($profilePictureUrl).
				"&email=".urlencode($email).
				"&externalUserId=".urlencode($externalUserId).
				"&externalUserType=".urlencode($this->params->get('moduleKey')).
				"&room_id=".$room_id.
				"&becomeModeratorAsInt=".$becomeModeratorAsInt.
				"&showAudioVideoTestAsInt=".$showAudioVideoTestAsInt);
		
		return checkResult($restService, $result);
	}

	function deleteRoom($openmeetings) {
		$restService = getRestService();
		
		$result = $restService->call($this->getUrl()."RoomService/deleteRoom?" .
				"SID=".$this->session_id.
				"&rooms_id=".$openmeetings->room_id);
		
		return checkResult($restService, $result);
	}

	function updateRoomWithModeration($openmeetings) {
		$restService = getRestService();
			
		$result = $restService->call($this->getUrl()."RoomService/updateRoomWithModeration?" .
				"SID=".$this->session_id .
				"&room_id=" . $openmeetings->room_id .
				"&name=" . urlencode($openmeetings->name) .
				"&roomtypes_id=" . urlencode($openmeetings->roomtypes_id) .
				"&comment=" . urlencode($openmeetings->comment) .
				"&numberOfPartizipants=" . $openmeetings->numberOfPartizipants .
				"&ispublic=" . $openmeetings->ispublic .
				"&appointment=" . $openmeetings->appointment .
				"&isDemoRoom=" . $openmeetings->isDemoRoom .
				"&demoTime=" . $openmeetings->demoTime .
				"&isModeratedRoom=" . $openmeetings->isModeratedRoom);
		
		return checkResult($restService, $result);
	}
}
?>
