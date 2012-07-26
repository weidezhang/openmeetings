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
			<td><font color="#888">Openmeetings recordings </font></td>

		</tr>
	</table>
</div>
<?php

// No direct access

defined( '_JEXEC' ) or die( );

// Require the base controller
jimport('joomla.application.component.controller');

//chdir('../');
//echo getcwd() . "\n";
require_once('components/com_openmeetings_conference/om_gateway/openmeetingsRoomManagament.php');
require_once("components/com_openmeetings_conference/om_gateway/openmeetingsRecordingManagament.php");
require_once("components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");
//echo getcwd() . "\n";


class RoomsControllerRec extends JController
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
		//$this->registerTask('add' ,  'edit'); //'add'  ,     'edit' , 'rec'
		// $this->registerTask('rec' ,  'rec');
	}


	function rec(){
			

		JRequest::setVar( 'view', 'rec' );
		parent::display();
		//print_r($om_recordings_return->);


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
