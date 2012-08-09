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

jimport( 'joomla.application.component.view' );

class RoomsViewRooms extends JView
{
	/**
	 * view display method
	 * @return void
	 **/
	function display($tpl = null)
	{
		$user = JFactory::getUser();

		JToolBarHelper::title( JText::_( 'Rooms Manager' ), 'generic.png' );
		if ($user->authorise('core.delete', 'openmeetings_conference')) {
			JToolBarHelper::deleteList();
		}
		if ($user->authorise('core.edit', 'openmeetings_conference')) {
			JToolBarHelper::editListX();
		}
		if ($user->authorise('core.create', 'openmeetings_conference')) {
			JToolBarHelper::addNewX();
		}

		// Get data from the model
		$items =& $this->get( 'Data');

		$this->assignRef( 'items', $items );

		parent::display($tpl);
	}
}
