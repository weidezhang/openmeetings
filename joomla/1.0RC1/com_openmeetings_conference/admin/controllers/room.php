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
?>
<div style="background-color: #EEEEEE; padding: 10px; font-size: 17px">
	<table width="100%" border="0">
		<tr>
			<td><font color="#888">Apache OpenMeetings [Incubating] <a
					href="http://incubator.apache.org/openmeetings/">Video Conference</a>
			</font></td>
		</tr>
	</table>
</div>
<?

// No direct access

defined( '_JEXEC' ) or die( );

// Require the base controller
jimport('joomla.application.component.controller');

//chdir('../');
//echo getcwd() . "\n";
require_once('components/com_openmeetings_conference/om_gateway/openmeetingsRoomManagament.php');
require_once("components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");
//echo getcwd() . "\n";


class RoomsControllerRoom extends JController
{

	function display() {
		parent::display();
	}

	/**
	 * constructor (registers additional tasks to methods)
	 * @return void
	 */
	function __construct()
	{
		parent::__construct();

		// Register Extra tasks
		$this->registerTask( 'add'  ,     'edit' );
	}


	/**
	 * display the edit form
	 * @return void
	 */
	function edit()
	{
		JRequest::setVar( 'view', 'room' );
		JRequest::setVar( 'layout', 'form'  );
		JRequest::setVar('hidemainmenu', 1);

		parent::display();
	}


	/**
	 * save a record (and redirect to main page)
	 * @return void
	 */
	function save()
	{

		$model = $this->getModel('room');

		if ($model->store()) {
			$msg = JText::_( 'Room Saved!' );
		} else {
			$msg = JText::_( 'Error Saving Room' ) . "<BR>" . $model->getError();
		}

		// Check the table in so it can be edited.... we are done with it anyway
		$link = 'index.php?option=com_openmeetings_conference';
		$this->setRedirect($link, $msg);
	}

	/**
	 * remove record(s)
	 * @return void
	 */
	function remove()
	{
		$model = $this->getModel('room');
		if(!$model->delete()) {
			$msg = JText::_( 'Error: One or more rooms could not be deleted' ) . "<BR>" . $model->getError();
		} else {
			$msg = JText::_( 'Rooms(s) Deleted' );
		}

		$this->setRedirect( 'index.php?option=com_openmeetings_conference', $msg );
	}

	/**
	 * cancel editing a record
	 * @return void
	 */
	function cancel()
	{
		$msg = JText::_( 'Operation Cancelled' );
		$this->setRedirect( 'index.php?option=com_openmeetings_conference', $msg );
	}


}
?>
