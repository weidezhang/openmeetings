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

if (!defined('AT_INCLUDE_PATH')) { exit; }

class REST_openmeetings {
	var $_rest = '';
	function REST_openmeetings($rest) {
	
		$this->_rest = $rest;
		$getSession_obj	= $this->_performAPICall();

		//check for session id
		try {
			$this->_sid = $getSession_obj['session_id'];
		} catch (Exception $e) {
			$this->_sid = session_id();
			var_dump($e->getMessage());
			exit('Error: no \'session_id\' in REST API response');
		}
	}

	// make API calls w/ params
	// NOTE: code based off of Apache OpenMeetings Moodle plug-in
	function _performAPICall($resource='getSession', $service='UserService', $params=array()){

		// This will allow you to view errors in the browser       
		// Note: set 'display_errors' to 0 in production  
		//ini_set('display_errors',1);  
		ini_set('display_errors',0);

		// Report all PHP errors (notices, errors, warnings, etc.)  
		//error_reporting(E_ALL);

		// URI used for making REST call. Each Web Service uses a unique URL.  
		$request = $this->_rest.$service.'/'.$resource;

		// build the query string
		$qs = http_build_query($params);

		try {
			// NOTE: Need php-curl to use (sudo apt-get install php-curl)
			// Initialize the session by passing the request as a parameter	
			$session = curl_init($request); 
			  
			// CURLOPT_RETURNTRANSFER will return the response   
			curl_setopt($session, CURLOPT_RETURNTRANSFER, true);	

			// send query string
			curl_setopt($session, CURLOPT_POSTFIELDS, $qs);	
		
			// Make the request  
			$response = curl_exec($session); 		
			  
			// get the status code
			$status_code = curl_getinfo($session, CURLINFO_HTTP_CODE);

			// Close the curl session  
			curl_close($session);

		} catch (Exception $e) {
			exit('Error: PHP cUrl extension not found. Must install php-curl (sudo apt-get install php-curl)');
		}
		
		// Confirm that the request was transmitted to the OpenMeetings! Image Search Service  
		if(!$response) {  
		   //die('Request OpenMeetings! OpenMeetings Service failed and no response was returned.'); 
		   die('Please make sure an OpenMeetings instance is running. Unable to connect to OpenMeetings.'); 
		}  		
		  
		// Check the HTTP Response code and display message if status code is not 200 (OK)  
		switch($status_code) {  
		        case 200:  
		                // Success  
		                break;  
		        case 503:  
		                die('Your call to OpenMeetings Web Services failed and returned an HTTP status of 503.   
		                     That means: Service unavailable. An internal problem prevented us from returning'.  
		                     ' data to you.');  
		                break;  
		        case 403:  
		                die('Your call to OpenMeetings Web Services failed and returned an HTTP status of 403.   
		                     That means: Forbidden. You do not have permission to access this resource, or are over'.  
		                     ' your rate limit.');  
		                break;  
		        case 400:  
		                // You may want to fall through here and read the specific XML error  
		                die('Your call to OpenMeetings Web Services failed and returned an HTTP status of 400.   
		                     That means:  Bad request. The parameters passed to the service did not match as expected.   
		                     The exact error is returned in the XML response.');  
		                break;  
		        default:
				if ($status_code) {
					die('Your call to OpenMeetings Web Services returned an unexpected HTTP status of: ' 
				   	 . $status_code.' Request '.$request);  
				} else {
					die('Your call to OpenMeetings Web Services returned no HTTP status. 
                                             Have you installed php-curl on the ATutor server?');
				}

		} 
		
		// Get the XML from the response, bypassing the header
		if (!($xml = strstr($response, '<ns'))) {
			$xml = null;
		} 

		// uncomment these 3 lines to view raw response data from API
		// echo '<b>Request:</b> '.$request.'<br><b>Params:</b> ';
		// print_r($params);
		// echo '<br><b>Code:</b> '.$status_code.'<br><b>Response:</b> '.htmlspecialchars($xml).'<br><br>';

		// NOTE: Need php-xml to use (sudo apt-get install php-xml)
		try {
			$dom = new DOMDocument();
		} catch (Exception $e) {
			exit('Error: DOMDocument() class not found. Must install php-xml (sudo yum install php-xml)');
		}

		$dom->loadXML($xml);

		$returnNodeList = $dom->getElementsByTagName('*');
		$returnArray = array();
		// create associative array from response
		foreach ($returnNodeList as $returnNode) {
			$returnArray[$returnNode->localName] = $returnNode->nodeValue;
		}

		return $returnArray;
	}

	function getSid(){
		return $this->_sid;
	}

	function saveUserInstance($parameters = array()) {
		return $this->_performAPICall(
			'setUserObjectAndGenerateRoomHash',
		        'UserService',

			array(
				'SID'			 => $this->_sid,
				'username'		 => $parameters['username'],
				'firstname'		 => $parameters['firstname'],
				'lastname'		 => $parameters['lastname'],
				'profilePictureUrl'	 => $parameters['profilePictureUrl'],
				'email'			 => $parameters['email'],
				'externalUserId'         => 1,
				'externalUserType'       => 'ATutor',
				'room_id'		 => 1,
				'becomeModeratorAsInt'   => $parameters['becomeModeratorAsInt'],
				'showAudioVideoTestAsInt'=> 1
			)
		);
	}

	function login($parameters = array()) {
		if (!isset($parameters['username'])) {
		    return false;
		} 
		return $this->_performAPICall(
		  'loginUser',
		  'UserService',
		  array(
		    'SID'         => $this->_sid,
		    'username'    => $parameters['username'],
		    'userpass'    => $parameters['userpass']
		  )
		);
		
	}


	function addRoom($parameters = array()){
		return $this->_performAPICall(
			'addRoomWithModeration',
			'RoomService',
			array(
			        'SID'				=> $parameters['SID'],
				'name'				=> $parameters['name'],
				'roomtypes_id'			=> $parameters['roomtypes_id'],
				'comment'			=> 'Room created by ATutor',
				'numberOfPartizipants'		=> $parameters['numberOfPartizipants'],
				'ispublic'			=> $parameters['ispublic'],
				'appointment'			=> $parameters['appointment'],
				'isDemoRoom'                    => $parameters['isDemoRoom'],
				'demoTime'                      => $parameters['demoTime'],
				'isModeratedRoom'		=> $parameters['isModeratedRoom']
			)
		);
	}

	function myErrors() {
		// leftover function from soap implementation
		return '';
	}

	function getError($code) {
		return $this->_performAPICall(
			'getErrorByCode',
			'UserService',
			array(
				'SID'		=> $this->_sid,
				'errorid'	=> $code,
				'language_id'	=> 1
			)
		);
	}
	

	function getRoomById($parameters = array()) {
		return $this->_performAPICall(
			'getRoomById',
			'RoomService',
			array(
				'SID'		=> $parameters['SID'],
				'rooms_id'	=> $parameters['rooms_id']
			)
		);	
	}

	function updateRoom($parameters = array()) {
		return $this->_performAPICall(
			'updateRoomWithModeration',
			'RoomService',
			array(
		    		'SID'				=> $parameters['SID'],
				'rooms_id'			=> $parameters['rooms_id'],
				'name'				=> $parameters['name'],
				'roomtypes_id'			=> $parameters['roomtypes_id'],
				'comment'			=> 'Room created by ATutor',
				'numberOfPartizipants'		=> $parameters['numberOfPartizipants'],
				'ispublic'			=> $parameters['ispublic'],
				'appointment'			=> $parameters['appointment'],
				'isDemoRoom'                    => $parameters['isDemoRoom'],
				'demoTime'                      => $parameters['demoTime'],
				'isModeratedRoom'		=> $parameters['isModeratedRoom']
			  )
		);
	}

	function deleteRoom($parameters = array()) {
		return $this->_performAPICall(
			'deleteRoom',
			'RoomService',
			array(
				'SID'		=> $parameters['SID'],
				'rooms_id'	=> $parameters['rooms_id']
			)
		);
	}
}
?>
