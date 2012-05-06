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
 
 // Nur einfache Fehler melden
error_reporting(E_ERROR | E_WARNING | E_PARSE);

require_once('openmeetings_gateway.php');

class openmeetings_config {
	var $openmeetings_red5host = "192.168.1.6";
	var $openmeetings_red5port = "5080";
	var $openmeetings_openmeetingsAdminUser = "swagner";
	var $openmeetings_openmeetingsAdminUserPass = "qweqwe";
	var $openmeetings_webappname = "openmeetings";
	var $openmeetings_openmeetingsModuleKey = "facebook";
}

$CFG = new openmeetings_config();


//echo " LOGGED IN";
  
$openmeetings_gateway = new openmeetings_gateway();

$openmeetings_gateway->openmeetings_loginuser();


$hash = $openmeetings_gateway->openmeetings_setUserObjectMainLandingZone(
				"hanstest", 
				"hans", "test", 
				"http://svn.apache.org/repos/asf/incubator/openmeetings/trunk/plugins/moodle_plugin/icon.gif", 
				"hans@openmeetings.de", 
				"12", "facebook_test");

$iframe_d = "http://".$CFG->openmeetings_red5host . ":" . $CFG->openmeetings_red5port .
					 	"/" . "openmeetings/?" .
						"secureHash=" . $hash . 
						"&scopeRoomId=" . $rooms_id .
						"&swf=maindebug.as3.swf10.swf" .
						//"&landingZone=main" . 
						"&language=" . 1 . 
						"&picture=" . $pic_square . 
						"&absolutePicUrl=1";       

  //echo "Frame ID ".$iframe_d;

  //$facebook->redirect($iframe_d);
  
  header('Location: '.$iframe_d );
  exit();
  

?>