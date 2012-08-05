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
IncludeModuleLangFile(__FILE__);

class COpenmeetings {
	function IsAdmin() {
		global $USER, $APPLICATION;
		if (!is_object($USER)) $USER = new CUser;
		if ($USER->IsAdmin()) return true;
		$OPENMEETINGS_RIGHT = $APPLICATION->GetGroupRight("openmeetings");
		if ($OPENMEETINGS_RIGHT>="W") return true;
	}

	function GetRoomList() {
		global $USER, $APPLICATION;
		$rsUser = CUser::GetByID($USER->GetID()); 
		$arUser = $rsUser->Fetch();
		
		$result = array();
		$photo = "";
		if($arUser['PERSONAL_PHOTO']) {
			$photo = "http://" . COption::GetOptionString("main", "server_name", "www.bitrixsoft.com") . CFile::GetPath($arUser['PERSONAL_PHOTO']);//CFile::ResizeImage(CFile::GetByID($arUser['PERSONAL_PHOTO']), 100);
		}
		$gate = new openmeetings_gateway();
		if (!$gate->openmeetings_loginuser()) {
			CMain::ThrowException(GetMessage("OPENMEETINGS_FAILED_TO_LOGIN"));
		}
		if ($gate->setUserObjectWithExternalUser($USER->GetLogin(), $USER->GetFirstName(), $USER->GetLastName(), $photo, $USER->GetEmail(), $USER->GetID()) > 0) {
			$rooms = $gate->getAvailableRooms();
			foreach ($rooms as $room) {
				$result[] = array("name" => $room["name"], "id" => $room["rooms_id"]);
			}
		}
		return $result;
	}
}
