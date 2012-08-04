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

define('AT_INCLUDE_PATH', '../../include/');
require (AT_INCLUDE_PATH.'vitals.inc.php');
require ('lib/openmeetings.class.php');

//local variables
$course_id = $_SESSION['course_id'];

//validate variables
$_REQUEST['room_id'] = intval($_REQUEST['room_id']);

//Initiate Openmeeting
$om_obj = new Openmeetings($course_id, $_SESSION['member_id']);

//Login
$om_obj->om_login();

//Handles form actions
if (isset($_POST['submit']) && $_REQUEST['room_id']){
	//have to makesure the user really do have permission over the paramater room id
	if ($om_obj->isMine($_REQUEST['room_id']) || authenticate(AT_PRIV_OPENMEETINGS, true)){
		$om_obj->om_deleteRoom($_REQUEST['room_id']);
		$msg->addFeedback('OPENMEETINGS_DELETE_SUCEEDED');
		header('Location: index.php');
		exit;
	} else {
		$msg->addError('OPENMEETINGS_DELETE_FAILED');
	}
} elseif (isset($_POST['cancel'])) {
	$msg->addFeedback('OPENMEETINGS_CANCELLED');
	header('Location: index.php');
	exit;
}

//Header begins here
require (AT_INCLUDE_PATH.'header.inc.php');
?>

<form action="<?php  $_SERVER['PHP_SELF']; ?>" method="post">
	<div class="input-form">
		<div class="row">
			<p><?php echo _AT('openmeetings_confirm_delete'); ?></p>	
		</div>
		<div class="row buttons">
			<input type="hidden" name="room_id" value="<?php echo $_REQUEST['room_id']; ?>" />
			<input type="submit" name="submit" value="<?php echo _AT('yes'); ?>"  />
			<input type="submit" name="cancel" value="<?php echo _AT('no'); ?>" />
		</div>
	</div>
</form>

<?php require (AT_INCLUDE_PATH.'footer.inc.php'); ?>
