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
if (!defined("B_PROLOG_INCLUDED") || B_PROLOG_INCLUDED !== true) die();

if (!CModule::IncludeModule("openmeetings")) {
    ShowError(COpenmeetings::GetString(GetMessage("OPENMEETINGS_MODULE_IS_NOT_INSTALLED")));
    return;
}

if (!$USER->IsAuthorized()) {
	return;
}

function getRoom($id, &$arResult, &$comp) {
	if (isset($id) && is_numeric($id) && $id > 0) {
		$hash = COpenmeetings::GetRoomHash($id);
		if ($hash) {
			$arResult["ROOM"] = $id;
			$arResult["URL"] = COpenmeetings::GetOMUrl();
			$arResult["HASH"] = $hash;
			$arResult["LANGUAGE"] = COpenmeetings::GetString(GetMessage("OPENMEETINGS_LANGUAGE"));
			$comp->__templateName = "show_room";
			return true;
		}
	}
	return false;
}

if (!getRoom($arParams["ID"], $arResult, $this) && !getRoom($_REQUEST["ID"], $arResult, $this)) {
	$arResult["ROOMS"] = COpenmeetings::GetRoomList();
}

$this->IncludeComponentTemplate();
