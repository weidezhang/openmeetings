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

require_once('components/com_openmeetings_conference/om_gateway/openmeetingsRoomManagament.php');
require_once("components/com_openmeetings_conference/om_gateway/openmeetingsRecordingManagament.php");
require_once("components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");

jimport( 'joomla.application.component.view' );

class RoomsViewRec extends JView
{


	/**
	 * view display method
	 * @return void
	 **/
	function display($tpl = null)
	{

		$user =& JFactory::getUser();

		$om_recordings_return = array();

		$om_recordings = new openmeetingsRecordingManagament();

		if ($user->authorise('core.create') || $user->authorise('core.edit')) {
			$om_recordings_return=$om_recordings->getFlvRecordingByExternalRoomTypeAndId($user->id);
		} else {
			$om_recordings_return=$om_recordings->getFlvRecordingByExternalRoomType();
		}


		$this->assignRef( 'om_recordings_return', $om_recordings_return );

		parent::display($tpl);
	}

}
?>