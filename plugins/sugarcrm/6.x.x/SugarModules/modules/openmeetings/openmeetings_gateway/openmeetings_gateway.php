<?php
/*********************************************************************************
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
*  ********************************************************************************/
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');

require_once('lib/openmeetings_rest_service.php');


class openmeetings_gateway {
	
	var $session_id = "";
	
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
		global $current_user;
		global $system_config;

		$restService = new openmeetings_rest_service();		
	
		$response = $restService->call("http://".$system_config->settings['info_openmeetings_url'].":".$system_config->settings['info_openmeetings_http_port']."/openmeetings/services/UserService/getSession");
				
		// Confirm that the request was transmitted to the OpenMeetings!
		if(!$response->asXML()) {  
   			die("Request to OpenMeetings og! OpenMeetings Service failed and no response was returned.");  
		}  		
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result); echo '</pre>';
				$this->session_id = $sid = $response->children('ns', true)->return->children('ax24', true)->session_id;
								
				$username = $system_config->settings['info_openmeetings_username'];
				$userpass = $system_config->settings['info_openmeetings_password'];
				$result = $restService->call('http://localhost:5080/openmeetings/services/UserService/loginUser?SID='.$this->session_id.'&username='.$username.'&userpass='.$userpass);
				
				if ($restService->getError()) {
					echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
				} else {
					$err = $restService->getError();
					if ($err) {
						echo '<h2>Error</h2><pre>' . $err . '</pre>';
					} else {
						//echo '<h2>Result</h2><pre>'; print_r($result); echo '</pre>';
						$returnValue = $result->children('ns', true)->return[0];
						//print_r($result->children('ns', true)->return[0]);
						//exit;	
						//echo '<h2>returnValue</h2><pre>'; printf($returnValue); echo '</pre>';		
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

	
	function openmeetings_createroomwithmod($openmeetings) {
		global $current_user;
		global $system_config;
		
		$restService = new openmeetings_rest_service();	
		//echo $restService."<br/>";
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
					
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/RoomService/addRoomWithModerationQuestionsAndAudioType?" .
							"SID=".$this->session_id.
							"&name=".$openmeetings->name.
							"&roomtypes_id=".$openmeetings->roomtypes_id.
							"&comment=".$openmeetings->comment.
							"&numberOfPartizipants=".$openmeetings->numberOfPartizipants.
							"&ispublic=".$openmeetings->ispublic.
							"&appointment=".$openmeetings->appointment.
							"&isDemoRoom=".$openmeetings->isDemoRoom.
							"&demoTime=".$openmeetings->demoTime.
							"&isModeratedRoom=".$openmeetings->isModeratedRoom.
							"&allowUserQuestions=true" .
							"&isAudioOnly=false");
		
		//$result = $client_roomService->call('addRoomWithModeration',$params);
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result->children('ns', true)->return[0]; //$result["return"];
			}
		}   
		return -1;
	}
	
	function openmeetings_addRoomWithModerationExternalTypeAndTopBarOption($openmeetings) {
		global $current_user;
		global $system_config;
			    	
		$restService = new openmeetings_rest_service();				
		//echo $client_roomService."<br/>";		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  		
		
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/RoomService/addRoomWithModerationExternalTypeAndTopBarOption?" .
							"SID=".$this->session_id.
							"&name=".urlencode($openmeetings->name).
							"&roomtypes_id=".$openmeetings->roomtypes_id.
							"&comment=".urlencode($openmeetings->comment).
							"&numberOfPartizipants=".$openmeetings->numberOfPartizipants.
							"&ispublic=".$this->var_to_str($openmeetings->ispublic).
							"&appointment=".$this->var_to_str($openmeetings->appointment).
							"&isDemoRoom=".$this->var_to_str($openmeetings->isDemoRoom).
							"&demoTime=".$openmeetings->demoTime.
							"&isModeratedRoom=".$this->var_to_str($openmeetings->isModeratedRoom).
							"&externalRoomType=".$openmeetings->externalRoomType.
							"&allowUserQuestions=" .$this->var_to_str($openmeetings->allowUserQuestions).
							"&isAudioOnly=".$this->var_to_str($openmeetings->isAudioOnly).
							"&waitForRecording=false".
							"&allowRecording=".$this->var_to_str($openmeetings->allowRecording).
							"&hideTopBar=false");
		
				
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				//return $result["return"];
				return $result->children('ns', true)->return[0];
			}
		}   
		return -1;
	}
	
	function updateRoomWithModerationAndQuestions($openmeetings) {
		global $current_user;
		global $system_config;		
	 	
		$restService = new openmeetings_rest_service();
		//echo $client_roomService."<br/>";
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
						
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/RoomService/updateRoomWithModerationAndQuestions?" .
							"SID=".$this->session_id.
							"&room_id=".$openmeetings->room_id.
							"&name=".urlencode($openmeetings->name).
							"&roomtypes_id=".$openmeetings->roomtypes_id.
							"&comment=".urlencode($openmeetings->comment).
							"&numberOfPartizipants=".$openmeetings->numberOfPartizipants.
							"&ispublic=".$this->var_to_str($openmeetings->ispublic).
							"&appointment=".$this->var_to_str($openmeetings->appointment).
							"&isDemoRoom=".$this->var_to_str($openmeetings->isDemoRoom).
							"&demoTime=".$openmeetings->demoTime.
							"&isModeratedRoom=".$this->var_to_str($openmeetings->isModeratedRoom).
							"&allowUserQuestions=".$this->var_to_str($openmeetings->allowUserQuestions));
		
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
			//print_r($params);
			//exit();
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';				
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				//return $result["return"];
				return $result->children('ns', true)->return[0];				
			}
		}   
		return -1;
				
	}
	
	function deleteRoom($openmeetings) {		
		global $current_user;
		global $system_config;
		
		//echo $client_roomService."<br/>";
		$restService = new openmeetings_rest_service();		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  		
		/*
		$params = array(
			'SID' => $this->session_id,
			'rooms_id' => $openmeetings->room_id
			
		);
				
		$result = $client_roomService->call('deleteRoom',$params);
		*/
		
		
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/RoomService/deleteRoom?" .
							"SID=".$this->session_id.
							"&rooms_id=".$openmeetings->room_id);
		
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				//return $result["return"];
				return $result->children('ns', true)->return[0];
			}
		}   
		return -1;
	}
	
	/*
	 * Usage if this Method will work if you have no need to simulate always the same user in 
	 * OpenMeetings, if you want to do this check the next method that also remembers the 
	 * ID of the external User
	 * 
	 * 
	 */
	function openmeetings_setUserObject($username, $firstname, $lastname, $profilePictureUrl, $email) {
	    	global $current_user;
		global $system_config;

	 	$restService = new nusoap_client("http://".$system_config->settings[info_openmeetings_url].":".$system_config->settings[info_openmeetings_http_port]."/openmeetings/services/UserService?wsdl", true);
		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		$params = array(
			'SID' => $this->session_id,
			'username' => $username,
			'firstname' => $firstname,
			'lastname' => $lastname,
			'profilePictureUrl' => $profilePictureUrl,
			'email' => $email
		);
		$result = $restService->call('setUserObject',$params);
		if ($client_roomService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result["return"];
			}
		}   
		return -1;
	}
	
	/**
	 * Sets the User Id and remembers the User, 
	 * the value for $systemType is any Flag but usually should always be the same, 
	 * it only has a reason if you have more then one external Systems, so the $userId will not 
	 * be unique, then you can use the $systemType to give each system its own scope
	 * 
	 * so a unique external user is always the pair of: $userId + $systemType
	 * 
	 * in this case the $systemType is 'moodle'
	 * 
	 */
	function openmeetings_setUserObjectWithExternalUser($username, $firstname, $lastname, $profilePictureUrl, $email, $userId, $systemType) {
	    	global $current_user;
		global $system_config;

	 	$restService = new nusoap_client("http://".$system_config->settings[info_openmeetings_url].":".$system_config->settings[info_openmeetings_http_port]."/openmeetings/services/UserService?wsdl", true);
		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		$params = array(
			'SID' => $this->session_id,
			'username' => $username,
			'firstname' => $firstname,
			'lastname' => $lastname,
			'profilePictureUrl' => $profilePictureUrl,
			'email' => $email,
			'externalUserId' => $userId,
			'externalUserType' => $systemType
		);
		$result = $restService->call('setUserObjectWithExternalUser',$params);
		if ($client_roomService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result["return"];
			}
		}   
		return -1;
	}

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
				
	    global $current_user;
		global $system_config;
	
		$restService = new openmeetings_rest_service();		
	
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/UserService/setUserObjectAndGenerateRoomHash?" .
							"SID=".$this->session_id.
							"&username=".$username.
							"&firstname=".$firstname.
							"&lastname=".$lastname.
							"&profilePictureUrl=".$profilePictureUrl.
							"&email=".$email.
							"&externalUserId=".$externalUserId.
							"&externalUserType=".$externalUserType.
							"&room_id=".$room_id.
							"&becomeModeratorAsInt=".$becomeModeratorAsInt.
							"&showAudioVideoTestAsInt=".$showAudioVideoTestAsInt);
			
				
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		
		if ($restService->getError()) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result->children('ns', true)->return[0];
				
			}
		}   
		return -1;
	}

	function openmeetings_sendInvitationHash($username, $message, $baseurl, $email, $subject, $room_id, $conferencedomain, $isPasswordProtected, $invitationpass, $valid, $validFromDate, $validFromTime, $validToDate, $validToTime, $language_id, $sendMail) {
	    	global $current_user;
		global $system_config;

	 	$restService = new nusoap_client("http://".$system_config->settings[info_openmeetings_url].":".$system_config->settings[info_openmeetings_http_port]."/openmeetings/services/RoomService?wsdl", true);
		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		$params = array(
			'SID' => $this->session_id,
			'username' => $username,
			'message' => $message,
			'baseurl' => $baseurl,
			'email' => $email,
			'subject' => $subject,
			'room_id' => $room_id,
			'conferencedomain' => $conferencedomain,
			'isPasswordProtected' => $isPasswordProtected,
			'invitationpass' => $invitationpass,
			'valid'=> $valid,
			'validFromDate' => $validFromDate,
			'validFromTime' => $validFromTime,
			'validToDate' => $validToDate,
			'validToTime' => $validToTime,
			'language_id' => $language_id,
			'sendMail' => $sendMail			
		);
		$result = $restService->call('sendInvitationHash', $params);
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result["return"];
			}
		}   
		return -1;
	}

	function sendInvitationHashWithDateObject($username, $message, $baseurl, $email, $subject, $room_id, $conferencedomain, $isPasswordProtected, $invitationpass, $valid, $fromDate, $toDate, $language_id, $sendMail) {
	    	global $current_user;
		global $system_config;

	 	$restService = new nusoap_client("http://".$system_config->settings[info_openmeetings_url].":".$system_config->settings[info_openmeetings_http_port]."/openmeetings/services/RoomService?wsdl", true);
		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		$params = array(
			'SID' => $this->session_id,
			'username' => $username,
			'message' => $message,
			'baseurl' => $baseurl,
			'email' => $email,
			'subject' => $subject,
			'room_id' => $room_id,
			'conferencedomain' => $conferencedomain,
			'isPasswordProtected' => $isPasswordProtected,
			'invitationpass' => $invitationpass,
			'valid'=> $valid,
			'fromDate' => $fromDate,
			'toDate' => $toDate,			
			'language_id' => $language_id,
			'sendMail' => $sendMail				
		);
		$result = $restService->call('sendInvitationHash', $params);
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				return $result["return"];
			}
		}   
		return -1;
	}

function getInvitationHash($username, $room_id, $isPasswordProtected, $invitationpass, $valid, $validFromDate, $validFromTime, $validToDate, $validToTime) {
	    global $current_user;
		global $system_config;

		$restService = new openmeetings_rest_service();		
		$err = $restService->getError();
		if ($err) {
			echo '<h2>Constructor error</h2><pre>' . $err . '</pre>';
			echo '<h2>Debug</h2><pre>' . htmlspecialchars($client->getDebug(), ENT_QUOTES) . '</pre>';
			exit();
		}  
		
		$result = $restService->call("http://".$system_config->settings['info_openmeetings_url'].
					":".$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/services/RoomService/getInvitationHash?" .
							"SID=".$this->session_id.
							"&username=".urlencode($username).
							"&room_id=".$room_id.
							"&isPasswordProtected=".$this->var_to_str($isPasswordProtected).
							"&invitationpass=".$invitationpass.
							"&valid=".$valid.
							"&validFromDate=".$validFromDate.
							"&validFromTime=".$validFromTime.
							"&validToDate=".$validToDate.
							"&validToTime=".$validToTime);
		
		
		if ($restService->fault) {
			echo '<h2>Fault (Expect - The request contains an invalid SOAP body)</h2><pre>'; print_r($result); echo '</pre>';
		} else {
			$err = $restService->getError();
			if ($err) {
				echo '<h2>Error</h2><pre>' . $err . '</pre>';
			} else {
				//echo '<h2>Result</h2><pre>'; print_r($result["return"]); echo '</pre>';
				//return $result["return"];
				return $result->children('ns', true)->return[0];
			}
		}   
		return -1;
	}



}

?>
