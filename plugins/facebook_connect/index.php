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

require 'src/facebook.php';
require_once('openmeetings_gateway.php');

// Create our Application instance (replace this with your appId and secret).
$facebook = new Facebook(array(
  'appId'  => '	******',
  'secret' => '******',
));

class openmeetings_config {
	var $openmeetings_red5host = "******";
	var $openmeetings_red5port = "*****";
	var $openmeetings_openmeetingsAdminUser = "******";
	var $openmeetings_openmeetingsAdminUserPass = "*******";
	var $openmeetings_webappname = "openmeetings";
	var $openmeetings_openmeetingsModuleKey = "facebook";
}

$CFG = new openmeetings_config();

// Get User ID
$user = $facebook->getUser();

// We may or may not have this data based on whether the user is logged in.
//
// If we have a $user id here, it means we know the user is logged into
// Facebook, but we don't know if the access token is valid. An access
// token is invalid if the user logged out of Facebook.

if ($user) {
  try {
    // Proceed knowing you have a logged in user who's authenticated.
    $user_profile = $facebook->api('/me');
  } catch (FacebookApiException $e) {
    error_log($e);
    $user = null;
  }
}

// Login or logout url will be needed depending on current user state.
if ($user) {

	//echo " LOGGED IN";
  
	$openmeetings_gateway = new openmeetings_gateway();
	
	$openmeetings_gateway->openmeetings_loginuser();
	
	$hash = $openmeetings_gateway->openmeetings_setUserObjectMainLandingZone(
						$user_profile['name'], 
						$user_profile['first_name'], $user_profile['last_name'], 
						$user_profile['pic_square'], $user_profile['last_name'].'@openmeetings.de', 
						$user, "facebook");
	
	$iframe_d = "http://".$CFG->openmeetings_red5host . ":" . $CFG->openmeetings_red5port .
						 	"/" . "openmeetings/?" .
							"secureHash=" . $hash . 
							//"&scopeRoomId=" . $rooms_id .
							//"&swf=maindebug.swf8.swf" .
							//"&landingZone=main" . 
							"&language=" . 1 . 
							"&picture=" . $pic_square . 
							"&absolutePicUrl=1";       

  //echo "Frame ID ".$iframe_d;

  //$facebook->redirect($iframe_d);
  
  header('Location: '.$iframe_d );
  exit();
  
  //echo " Redirect IN";
  
} else {
  $loginUrl = $facebook->getLoginUrl();
  
  echo "<a href=".$loginUrl.">Please login with your Facebook acount</a>";
}

?>