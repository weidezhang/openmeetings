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

/**
 * Check against the array, if the value within is empty, replace each with the
 * default values.
 * @param	array	parameter
 * @return	array 
 */
function loadDefaultValues($post){
	$_om_config = array (
		'openmeetings_roomtype'				=> 1,	//conference
		'openmeetings_num_of_participants'	=> 5,
		'openmeetings_ispublic'				=> 1,	//true
		'openmeetings_vid_w'				=> 270,
		'openmeetings_vid_h'				=> 270,
		'openmeetings_show_wb'				=> 1,	//true
		'openmeetings_wb_w'					=> 600,
		'openmeetings_wb_h'					=> 660,
		'openmeetings_show_fp'				=> 1,	//true
		'openmeetings_fp_w'					=> 270,
		'openmeetings_fp_h'					=> 270
	);

	//replace each key if empty
	foreach ($_om_config as $key=>$value){
		if (empty($post[$key])){
			$post[$key] = $value;
		}
	}

	return $post;
}


/**
 * Check if openmeeting is being setup correctly.
 * @param	int	the course id
 */
function checkAccess($course_id){
	global $_config, $msg;
	if (!isset($_config['openmeetings_username']) || !isset($_config['openmeetings_userpass'])){
		include(AT_INCLUDE_PATH.'header.inc.php');
		$msg->addError('OPENMEETINGS_NOT_SETUP');
		include(AT_INCLUDE_PATH.'footer.inc.php');
		exit;
	}
}
?>
