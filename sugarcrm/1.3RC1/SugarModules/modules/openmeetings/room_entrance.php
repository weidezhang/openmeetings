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

require_once("openmeetings_gateway/openmeetings_gateway.php");
//require_once("modules/Meetings/Meeting.php");

global $current_user;
global $system_config;
	
$openmeetings_gateway = new openmeetings_gateway();
		
if ($openmeetings_gateway->openmeetings_loginuser()) {
		
		$picture = $current_user->picture;
		$first_name = $current_user->first_name;					
		$last_name = $current_user->last_name;
		if(empty($picture)) {
			$picture = "none";
		}
		if(empty($first_name)) {
			$first_name = "none";
		}
		if(empty($last_name)) {
			$last_name = "none";
		}
		$roomhash = $openmeetings_gateway->openmeetings_setUserObjectAndGenerateRoomHash(
							$current_user->user_name, 
							$first_name,						
							$last_name,
							$picture,
							$current_user->email1,
							$current_user->id,
							"SugarCRM", 
							$_REQUEST['roomid'], 
							1, 
							1
							);

												
		if (!empty($roomhash)) {

			$iframe_d = "http://".$system_config->settings['info_openmeetings_url'].":".
					$system_config->settings['info_openmeetings_http_port'].
					"/openmeetings/?" .
					"scopeRoomId=" . $_REQUEST['roomid'] .
					"&secureHash=" .$roomhash.								
					"&language=".$system_config->settings['info_openmeetings_language'].
					"&lzproxied=solo";					
					
					
			printf("<iframe src='%s' width='%s' height='600px' />",$iframe_d,
					"100%");

		}
	} else {
		die("Could not login User to OpenMeetings, check your OpenMeetings Module Configuration");
	}
?>
