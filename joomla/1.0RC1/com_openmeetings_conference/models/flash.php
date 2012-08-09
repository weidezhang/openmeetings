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

header('Cache-Control: no-cache');
header('Pragma: no-cache');

jimport( 'joomla.application.component.model' );

class OmModelFlash extends JModel {
	function getOutput($task) {
		function hasrights($username, $usertype, $owner, $rights) {
			if (trim($rights)=="None") return 0;

			$rightslist=explode(",",$rights);
			foreach ($rightslist as $key => $value) $rightslist[$key]=trim($rightslist[$key]);

			if (in_array("All",$rightslist)) return 1;
			if (in_array($username,$rightslist)) return 1;
			if (in_array($usertype,$rightslist)) return 1;
			if (in_array("Admins",$rightslist) && (in_array($usertype, array('Super Administrator', 'Administrator', 'Manager')) || ($username==$owner&&$username))) return 1;
			if (in_array("Members",$rightslist) && $username) return 1;
			return 0;
		}
	}
}
