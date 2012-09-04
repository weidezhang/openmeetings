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
// no direct access
defined('_JEXEC') or die('Restricted access');

class TableRoom extends JTable
{
	/**
	 * Primary Key
	 *
	 * @var int
	 */
	var $id = null;

	/**
	 * @var string
	 */
	var $owner = null;

	/**
	 * @var string
	 */
	var $name = null;

	/**
	 * @var int
	 */
	var $roomtype_id = null;

	/**
	 * @var string
	 */
	var $comment = null;

	/**
	 * @var int
	 */
	var $number_of_partizipants = null;

	/**
	 * @var int
	 */
	var $is_public = null;

	/**
	 * @var int
	 */
	var $appointment = null;

	/**
	 * @var int
	 */
	var $is_moderated_room = null;

	/**
	 *
	 * @var int
	 */
	var $user_id = null;

	/**
	 *
	 * @var int
	 */
	var $room_id = null;



	/**
	 * @var int
	 */
	var $room_validity = null;

	/**
	 * @var date
	 */
	var $date_type = null;

	/**
	 * @var time
	 */
	var $time_type = null;

	/**
	 *
	 * @var int
	 */
	var $duration = null;

	/**
	 *
	 * @var int
	 */
	var $repeat_type = null;

	/**
	 *
	 * @var int
	 */
	var $weekday_type = null;


	/**
	 * Constructor
	 *
	 * @param object Database connector object
	 */
	function TableRoom( &$db ) {
		parent::__construct('#__om_rooms', 'id', $db);
	}
}
?>
