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
global $DB, $MESS, $APPLICATION;

IncludeModuleLangFile(__FILE__);

$DBType = strtolower($DB->type);

$arClassesList = array(
	// main classes
	"COpenmeetings"				=> "classes/general/openmeetings.php",
	"openmeetings_gateway"		=> "classes/general/openmeetings_gateway.php",
	"openmeetings_rest_service"	=> "classes/general/lib/openmeetings_rest_service.php",
	);

// fix strange update bug
if (method_exists(CModule, "AddAutoloadClasses")) {
	CModule::AddAutoloadClasses(
			"openmeetings",
			$arClassesList
	);
} else {
	foreach ($arClassesList as $sClassName => $sClassFile) {
		require_once($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/".$sClassFile);
	}
}

		