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

class RoomsViewRoom extends JView
{
	/**
	 * display method
	 * @return void
	 **/
	function display($tpl = null)
	{
		//get the hello
		$croom        =& $this->get('Data');
		$isNew        = ($croom->id < 1);
		$preselectLogo = '';

		$text = $isNew ? JText::_( 'New' ) : JText::_( 'Edit' );
		JToolBarHelper::title(   JText::_( 'Room' ).': <small><small>[ ' . $text.' ]</small></small>' );
		JToolBarHelper::save();
		if ($isNew)  {
			JToolBarHelper::cancel();
		} else {
			// for existing items the button is renamed `close`
			JToolBarHelper::cancel( 'cancel', 'Close' );
		}

		$this->assignRef('croom', $croom);
		if (!isset($this->croom->room_id)) {
			$this->croom->room_id = 0;
			$this->croom->name = '';
			$this->croom->comment = '';
			$this->croom->logo = '';
		}


		$db	= JFactory::getDBO();
		$user = JFactory::getUser();

		// get available  user groups
		$gids 	= $user->getAuthorisedGroups();
		$gids 	= implode( ',', $gids );

		// get list of usernames
		$recipients = array( JHTML::_('select.option',  '0', '- '. JText::_( 'Select User' ) .' -' ) );
		$query = $query = 'SELECT u.id AS value, u.username AS text FROM #__users u'
		. ' INNER JOIN #__user_usergroup_map uugm ON (u.id = uugm.user_id)'
		. ' WHERE uugm.group_id IN ( '.$gids.' )'
		. ' ORDER BY u.name'
		;
		$db->setQuery( $query );
		$recipients = array_merge( $recipients, $db->loadObjectList() );

		$preselected = array();

		if ($croom->id != 0) {

			$query = ' SELECT user_id FROM #__om_rooms_users ' .
					' WHERE om_room_id = '. $croom->id . '';
			$db->setQuery( $query );
			$preselected = $db->loadResultArray();

		}

		$ownerlist = JHTML::_('select.genericlist', $recipients, 'owner[]', 'class="inputbox" size="10"  multiple', 'value', 'text', $preselected);
		$this->assignRef('owner', $ownerlist);


		$groupsFlexi = array( JHTML::_('select.option',  '0', '- '. JText::_( 'Select FlexiGroup' ) .' -' ) );
		$preselectedFlexiGroups = array();

		if ($croom->logo != "") {
			$query = ' SELECT logo FROM #__om_rooms '.
                '  WHERE id = '.$croom->id.'';
			$db->setQuery( $query );
			$preselectLogo = $db->loadResult();
		}
		$path = 'images/logos/';
		$logolist = JHTML::_( 'list.images', 'logo', $preselectLogo , null, $path );

		$this->assignRef('logo', $logolist);

		//$ownerlist = JHTML::_('select.genericlist', $recipients, 'owner', 'class="inputbox" name="owner" size="10"  multiple', 'value', 'text', $croom->owner);
		$flexiGroupslist = JHTML::_('select.genericlist', $groupsFlexi, 'flexigroups[]', 'class="inputbox" size="10"  multiple', 'value', 'text', $preselectedFlexiGroups);

		$this->assignRef('flexigroup', $flexiGroupslist);


		parent::display($tpl);
	}
}
