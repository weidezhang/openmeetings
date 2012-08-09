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

require_once('lib/openmeetings_rest_service.php');

class openmeetings_gateway {

	var $session_id = "";
	var $url_server; 
	var $url_port;
	var $webapp_name;
	var $moduleKey;
	var $username;
	var $password;
	
	function getUrl() {
		$this->url_server = variable_get("openmeetings_url", "localhost"); 
		$this->url_port = variable_get("openmeetings_port", "5080");
		$this->username = variable_get('openmeetings_username', 'admin');
		$this->password = variable_get('openmeetings_password', 'red5test');
		$this->webapp_name = "openmeetings";
		$this->moduleKey = "drupal";
		//FIXME protocol should be added
		$port = $this->url_port == 80 ? "" : ":" . $this->url_port;
		return "http://" . $this->url_server . $port . "/" . $this->webapp_name;
	}

	function var_to_str($in)
	{
		if(is_bool($in))
		{
			if($in)
			return "true";
			else
			return "false";
		}
		else
		return $in;
	}


	/**
	 * TODO: Get Error Service and show detailed Error Message
	 */

	function openmeetings_loginuser() {
		global $CFG;

		$restService = new openmeetings_rest_service();

		$response = $restService->call($this->getUrl()."/services/UserService/getSession","session_id");

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				$this->session_id = $response;

				$result = $restService->call($this->getUrl()."/services/UserService/loginUser?"
				. "SID=".$this->session_id
				. "&username=" . urlencode($this->username)
				. "&userpass=" . urlencode($this->password)
				);

				if ($restService->getError()) {
					echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
				} else {
					$err = $restService->getError();
					if ($err) {
						echo '<h2>Error</h2><pre>' . $err . '</pre>';
					} else {
						$returnValue = $result;
					}
				}
			}
		}
		
		if ($returnValue>0){
			return true;
		} else {
			return false;
		}
	}


	function openmeetings_updateRoomWithModeration($openmeetings) {

		$restService = new openmeetings_rest_service();
		//echo $restService."<br/>";
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}
		$course_name = 'MOODLE_COURSE_ID_'.$openmeetings->course.'_NAME_'.$openmeetings->name;
			
		$isModeratedRoom = false;
		if ($openmeetings->isModeratedRoom == 1) {
			$isModeratedRoom = true;
		}

		$result = $restService->call($this->getUrl()."/services/RoomService/updateRoomWithModeration?" .
							"SID=".$this->session_id.
							"&room_id=".$openmeetings->room_id.
							"&name=".urlencode($course_name).
							"&roomtypes_id=".urlencode($openmeetings->type).
							"&comment=".urlencode("Created by SOAP-Gateway for Moodle Platform").
							"&numberOfPartizipants=".$openmeetings->max_user.
							"&ispublic=false".
							"&appointment=false".
							"&isDemoRoom=false".
							"&demoTime=0".
							"&isModeratedRoom=".$this->var_to_str($isModeratedRoom));

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}
		return -1;
	}
	
	function openmeetings_updateRoomWithModerationAndQuestions($openmeetings) {

		$restService = new openmeetings_rest_service();
		//echo $restService."<br/>";
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}
		$room_name = 'DRUPAL_ModeleKey:_'.$this->moduleKey.'_NAME_'.$openmeetings->name;
			
		$isModeratedRoom = ($openmeetings->isModeratedRoom == 1) ? 'true' : 'false';
    	$allowUserQuestions = ($openmeetings->allowUserQuestions == 1) ? 'true' : 'false';
    	$isAudioOnly = ($openmeetings->isAudioOnly == 1) ? 'true' : 'false';

		$url = $this->getUrl()."/services/RoomService/updateRoomWithModerationAndQuestions?" .
							"SID=".$this->session_id.
							"&room_id=".$openmeetings->room_id.
							"&name=".urlencode($room_name).
							"&roomtypes_id=".urlencode($openmeetings->roomtypes_id).
							"&comment=".urlencode("Created by SOAP-Gateway for Moodle Platform").
							"&numberOfPartizipants=".$openmeetings->numberOfPartizipants.
							"&ispublic=false".
							"&appointment=false".
							"&isDemoRoom=false".
							"&demoTime=0".
							"&isModeratedRoom=".$isModeratedRoom . 
							"&allowUserQuestions=".$allowUserQuestions;

		$result = $restService->call($url);

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}
		return -1;
	}

	/*
	 * public String setUserObjectAndGenerateRecordingHashByURL(String SID, String username, String firstname, String lastname,
					Long externalUserId, String externalUserType, Long recording_id)
	 */
	 function openmeetings_setUserObjectAndGenerateRecordingHashByURL($username, $firstname, $lastname, 
						$userId, $systemType, $recording_id) {
	    $restService = new openmeetings_rest_service();
	 	$result = $restService->call($this->getUrl().'/services/UserService/setUserObjectAndGenerateRecordingHashByURL?'.
			'SID='.$this->session_id .
			'&username='.urlencode($username) .
			'&firstname='.urlencode($firstname) .
			'&lastname='.urlencode($lastname) .
			'&externalUserId='.$userId .
			'&externalUserType='.urlencode($systemType) .
			'&recording_id='.$recording_id,
			'return'
			);
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}   
		return -1;
	}

	function openmeetings_setUserObjectAndGenerateRoomHashByURLAndRecFlag($username, $firstname, $lastname,
					$profilePictureUrl, $email, $userId, $systemType, $room_id, $becomeModerator, $allowRecording) {

		$restService = new openmeetings_rest_service();
		//echo $restService."<br/>";
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}

		$result = $restService->call($this->getUrl()."/services/UserService/setUserObjectAndGenerateRoomHashByURLAndRecFlag?" .
							"SID=".$this->session_id.
							"&username=".urlencode($username).
							"&firstname=".urlencode($firstname).
							"&lastname=".urlencode($lastname).
							"&profilePictureUrl=".urlencode($profilePictureUrl).
							"&email=".urlencode($email).
							"&externalUserId=".urlencode($userId).
							"&externalUserType=".urlencode($systemType).
							"&room_id=".urlencode($room_id).
							"&becomeModeratorAsInt=".$becomeModerator.
							"&showAudioVideoTestAsInt=1".
							"&allowRecording=".$this->var_to_str($allowRecording));

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}
		return -1;
	}

	function deleteRoom($openmeetings) {

		//echo $client_roomService."<br/>";
		$restService = new openmeetings_rest_service();
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}

		$url = $this->getUrl()."/services/RoomService/deleteRoom?" .
							"SID=".$this->session_id.
							"&rooms_id=".$openmeetings->room_id;
							
		$result = $restService->call($url);

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
			return $result;
		}
		return -1;
	}


	/**
	 * Generate a new room hash for entering a conference room
	 */
	function openmeetings_setUserObjectAndGenerateRoomHash($username,
									$firstname,
									$lastname,
									$profilePictureUrl,
									$email,
									$externalUserId,
									$externalUserType,
									$room_id,
									$becomeModeratorAsInt,
									$showAudioVideoTestAsInt) {

		$restService = new openmeetings_rest_service();
		
		$url = $this->getUrl()."/services/UserService/setUserObjectAndGenerateRoomHash?" .
					"SID=".$this->session_id.
					"&username=".urlencode($username).
					"&firstname=".urlencode($firstname).
					"&lastname=".urlencode($lastname).
					"&profilePictureUrl=".urlencode($profilePictureUrl).
					"&email=".urlencode($email).
					"&externalUserId=".urlencode($externalUserId).
					"&externalUserType=".urlencode($externalUserType).
					"&room_id=".$room_id.
					"&becomeModeratorAsInt=".$becomeModeratorAsInt.
					"&showAudioVideoTestAsInt=".$showAudioVideoTestAsInt;

		$result = $restService->call($url);

		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}

		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
			return $result;
		}
		return -1;
	}
	
	/**
	 * Create a new conference room
	 */
	function openmeetings_createRoomWithModAndType($openmeetings) {
	
		$restService = new openmeetings_rest_service();
    	$room_name = 'DRUPAL_ModeleKey:_'.$this->moduleKey.'_NAME_'.$openmeetings->name;
		
		$url = $this->getUrl().'/services/RoomService/addRoomWithModerationAndExternalType?' .
						'SID='.$this->session_id .
						'&name='.urlencode($room_name).
						'&roomtypes_id='.$openmeetings->roomtypes_id .
						'&comment='.urlencode('Created by SOAP/REST-Gateway for Drupal Platform') .
						'&numberOfPartizipants='.$openmeetings->numberOfPartizipants .
						'&ispublic='.$openmeetings->ispublic .
						'&appointment=false'.
						'&isDemoRoom=false'.
						'&demoTime=0' .
						'&isModeratedRoom='. $openmeetings->isModeratedRoom .
						'&externalRoomType='.urlencode($this->moduleKey)
						;
		
	 	$result = $restService->call($url, "return");
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}   
		return -1;
	}
	
	/**
	 * Create a new conference room
	 */
	function openmeetings_createRoomWithModAndTypeAndAudioOption($openmeetings) {
	
		$restService = new openmeetings_rest_service();
    	$room_name = 'DRUPAL_ModeleKey:_'.$this->moduleKey.'_NAME_'.$openmeetings->name;
		
    	$isModeratedRoom = ($openmeetings->isModeratedRoom == 1) ? 'true' : 'false';
    	$allowUserQuestions = ($openmeetings->allowUserQuestions == 1) ? 'true' : 'false';
    	$isAudioOnly = ($openmeetings->isAudioOnly == 1) ? 'true' : 'false';
		
		$url = $this->getUrl().'/services/RoomService/addRoomWithModerationExternalTypeAndAudioType?' .
						'SID='.$this->session_id .
						'&name='.urlencode($room_name).
						'&roomtypes_id='.$openmeetings->roomtypes_id .
						'&comment='.urlencode('Created by SOAP/REST-Gateway for Drupal Platform') .
						'&numberOfPartizipants='.$openmeetings->numberOfPartizipants .
						'&ispublic='.$openmeetings->ispublic .
						'&appointment=false'.
						'&isDemoRoom=false'.
						'&demoTime=0' .
						'&isModeratedRoom='. $isModeratedRoom .
						'&externalRoomType='.urlencode($this->moduleKey) .
						'&allowUserQuestions='. $allowUserQuestions .
						'&isAudioOnly='. $isAudioOnly
						;
						
	 	$result = $restService->call($url, "return");
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}   
		return -1;
	}
	
	function openmeetings_updateRoomWithModerationQuestionsAudioTypeAndHideOptions($openmeetings) {
	
		$restService = new openmeetings_rest_service();
    	$room_name = 'DRUPAL_ModeleKey:_'.$this->moduleKey.'_NAME_'.$openmeetings->name;
		
    	$isModeratedRoom = ($openmeetings->isModeratedRoom == 1) ? 'true' : 'false';
    	$allowUserQuestions = ($openmeetings->allowUserQuestions == 1) ? 'true' : 'false';
    	$isAudioOnly = ($openmeetings->isAudioOnly == 1) ? 'true' : 'false';
		
		$url = $this->getUrl().'/services/RoomService/updateRoomWithModerationQuestionsAudioTypeAndHideOptions?' .
						'SID='.$this->session_id .
						'&room_id='.$openmeetings->room_id .
						'&name='.urlencode($room_name).
						'&roomtypes_id='.$openmeetings->roomtypes_id .
						'&comment='.urlencode('Created by SOAP/REST-Gateway for Drupal Platform') .
						'&numberOfPartizipants='.$openmeetings->numberOfPartizipants .
						'&ispublic='.$openmeetings->ispublic .
						'&appointment=false'.
						'&isDemoRoom=false'.
						'&demoTime=0' .
						'&isModeratedRoom='. $isModeratedRoom .
						'&allowUserQuestions='. $allowUserQuestions .
						'&isAudioOnly='. $isAudioOnly .
						'&hideTopBar=false' .
						'&hideChat=false' .
						'&hideActivitiesAndActions=false' .
						'&hideFilesExplorer=false' .
						'&hideActionsMenu=false' .
						'&hideScreenSharing=false' .
						'&hideWhiteboard=false' 
						;
		
	 	$result = $restService->call($url, "return");
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($restService->getErrorMessage()); echo '</pre>';
		} else {
			return $result;
		}   
		return -1;
	}

	/**
	 * Get list of available recordings made by this Moodle instance
	 */
	function openmeetings_getRecordingsByExternalRooms() {
	
		$restService = new openmeetings_rest_service();
		
		$url = $this->getUrl()."/services/RoomService/getFlvRecordingByExternalRoomType?" .
					"SID=".$this->session_id .
					"&externalRoomType=".urlencode($CFG->openmeetings_openmeetingsModuleKey);

		$result = $restService->call($url,"");
					
		return $result;		
					
	}

}

?>
