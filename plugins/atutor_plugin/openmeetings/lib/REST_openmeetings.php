<?php


//if (!defined('AT_INCLUDE_PATH')) { exit; }

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

		// Report all PHP errors (notices, errors, warnings, etc.)  
		//error_reporting(E_ALL);

		// URI used for making REST call. Each Web Service uses a unique URL.  
		$request = $this->_rest.$service.'/'.$resource;
						
		// Initialize the session by passing the request as a parameter	
		$session = curl_init($request); 

		// Set curl options by passing session and flags
		// CURLOPT_HEADER allows us to receive the HTTP header
		curl_setopt($session, CURLOPT_HEADER, true);  
		  
		// CURLOPT_RETURNTRANSFER will return the response   
		curl_setopt($session, CURLOPT_RETURNTRANSFER, true);	
		
		$temp = array();
		//echo '<pre>'; print_r($params);echo '</pre>';
		foreach ($params as $key => $value) {
			array_push($temp, $key.'='.$value);
		}
		$qs = implode('&', $temp);
		$qs = str_replace(' ', '+', $qs);
		curl_setopt($session, CURLOPT_POSTFIELDS, $qs);	
		
		// Make the request  
		$response = curl_exec($session); 		
		  
		// Close the curl session  
		curl_close($session);
		
		// Confirm that the request was transmitted to the OpenMeetings! Image Search Service  
		if(!$response) {  
		   die('Request OpenMeetings! OpenMeetings Service failed and no response was returned.');  
		}  
		
		// Create an array to store the HTTP response codes  
		$status_code = array();  
		  
		// Use regular expressions to extract the code from the header  
		preg_match('/\d\d\d/', $response, $status_code);  
		  
		// Check the HTTP Response code and display message if status code is not 200 (OK)  
		switch( $status_code[0] ) {  
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
		        	die('Your call to OpenMeetings Web Services returned an unexpected HTTP status of: ' 
				    . $status_code[0].' Request '.$request);  
		} 
		
		// Get the XML from the response, bypassing the header
		if (!($xml = strstr($response, '<ns'))) {
			$xml = null;
		} 
		
		// NOTE: Need php-xml to use (sudo yum install php-xml)
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

	/*function addRoom($parameters = array()){
		return $this->_performAPICall(
			'addRoom',
			'RoomService',
			array(
			        'SID'				=> $parameters['SID'],
				'name'				=> $parameters['name'],
				'roomtypes_id'			=> $parameters['roomtypes_id'],
				'comment'			=> 'Room created by ATutor',
				'numberOfPartizipants'		=> $parameters['numberOfPartizipants'],
				'ispublic'			=> $parameters['ispublic'],
				'videoPodWidth'			=> $parameters['videoPodWidth'],
				'videoPodHeight'		=> $parameters['videoPodHeight'],
				'videoPodXPosition'		=> 2, 
				'videoPodYPosition'		=> 2, 
				'moderationPanelXPosition'	=> 400, 
				'showWhiteBoard'		=> $parameters['showWhiteBoard'],
				'whiteBoardPanelXPosition'	=> 276, 
				'whiteBoardPanelYPosition'	=> 2, 
				'whiteBoardPanelHeight'		=> $parameters['whiteBoardPanelHeight'],
				'whiteBoardPanelWidth'		=> $parameters['whiteBoardPanelWidth'],
				'showFilesPanel'		=> $parameters['showFilesPanel'], 
				'filesPanelXPosition'		=> 2, 
				'filesPanelYPosition'		=> 284, 
				'filesPanelHeight'		=> $parameters['filesPanelHeight'], 
				'filesPanelWidth'		=> $parameters['filesPanelWidth']
			)
		);
	}*/

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
		// leftover function from nusoap implementation
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

	/*function updateRoom($parameters = array()) {
		return $this->_performAPICall(
			'updateRoom',
			'RoomService',
			array(
		    		'SID'				=> $parameters['SID'],
				'rooms_id'			=> $parameters['rooms_id'],
				'name'				=> $parameters['name'],
				'roomtypes_id'			=> $parameters['roomtypes_id'],
				'comment'			=> 'Room created by ATutor',
				'numberOfPartizipants'		=> $parameters['numberOfPartizipants'],
				'ispublic'			=> $parameters['ispublic'],
				'videoPodWidth'			=> $parameters['videoPodWidth'],
				'videoPodHeight'		=> $parameters['videoPodHeight'],
				'videoPodXPosition'		=> 2, 
				'videoPodYPosition'		=> 2, 
				'moderationPanelXPosition'	=> 400, 
				'showWhiteBoard'		=> $parameters['showWhiteBoard'],
				'whiteBoardPanelXPosition'	=> 276, 
				'whiteBoardPanelYPosition'	=> 2, 
				'whiteBoardPanelHeight'		=> $parameters['whiteBoardPanelHeight'],
				'whiteBoardPanelWidth'		=> $parameters['whiteBoardPanelWidth'],
				'showFilesPanel'		=> $parameters['showFilesPanel'], 
				'filesPanelXPosition'		=> 2, 
				'filesPanelYPosition'		=> 284, 
				'filesPanelHeight'		=> $parameters['filesPanelHeight'], 
				'filesPanelWidth'		=> $parameters['filesPanelWidth']
			  )
		);
	}*/

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
