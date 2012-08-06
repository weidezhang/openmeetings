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
global $MESS;
$PathInstall = str_replace("\\", "/", __FILE__);
$PathInstall = substr($PathInstall, 0, strlen($PathInstall)-strlen("/index.php"));
IncludeModuleLangFile($PathInstall."/install.php");

require_once($PathInstall . "/../classes/general/openmeetings.php"); //Need to get COpenmeetings::GetString method

if(class_exists("openmeetings")) return;

Class openmeetings extends CModule {
	var $MODULE_ID = "openmeetings";
	var $MODULE_VERSION;
	var $MODULE_VERSION_DATE;
	var $MODULE_NAME;
	var $MODULE_DESCRIPTION;
	var $MODULE_GROUP_RIGHTS = "Y";

	function openmeetings() {
		$arModuleVersion = array();

		$path = str_replace("\\", "/", __FILE__);
		$path = substr($path, 0, strlen($path) - strlen("/index.php"));
		include($path."/version.php");

		if (is_array($arModuleVersion) && array_key_exists("VERSION", $arModuleVersion)) {
			$this->MODULE_VERSION = $arModuleVersion["VERSION"];
			$this->MODULE_VERSION_DATE = $arModuleVersion["VERSION_DATE"];
		} else {
			$this->MODULE_VERSION = OPENMEETINGS_VERSION;
			$this->MODULE_VERSION_DATE = OPENMEETINGS_VERSION_DATE;
		}

		$this->MODULE_NAME = COpenmeetings::GetString(GetMessage("OPENMEETINGS_MODULE_NAME"));
		$this->MODULE_DESCRIPTION = COpenmeetings::GetString(GetMessage("OPENMEETINGS_MODULE_DESCRIPTION"));
	}

	function DoInstall() {
		global $DB, $DOCUMENT_ROOT, $APPLICATION, $step, $errors, $public_dir;

		$OPENMEETINGS_RIGHT = $APPLICATION->GetGroupRight("openmeetings");
		if ($OPENMEETINGS_RIGHT=="W") {
			if($this->InstallDB()) {
				$this->InstallEvents();
				$this->InstallFiles();
			}
			//$GLOBALS["errors"] = $this->errors;
		}
		return true;
	}

	function InstallDB() {
		global $DB, $DBType, $APPLICATION;
		$this->errors = false;

		// Database tables creation
		if(!$DB->Query("SELECT 'x' FROM b_openmeetings WHERE 1=0", true))
			$this->errors = $DB->RunSQLBatch($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/db/".strtolower($DB->type)."/install.sql");

		if($this->errors !== false) {
			$APPLICATION->ThrowException(implode("<br>", $this->errors));
			return false;
		} else {
			RegisterModule("openmeetings");
			CModule::IncludeModule("openmeetings");

			//COption::SetOptionString("openmeetings", "OPENMEETINGS_DIR", "");

			//RegisterModuleDependences("main", "OnBeforeProlog", "main", "", "", 100, "/modules/openmeetings/keepvoting.php");

			return true;
		}
	}

	function InstallFiles($arParams = array()) 	{
		global $DB;

		CopyDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/admin", $_SERVER["DOCUMENT_ROOT"]."/bitrix/admin");
		//CopyDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/themes/", $_SERVER["DOCUMENT_ROOT"]."/bitrix/themes/", true, true);
		CopyDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/images", $_SERVER["DOCUMENT_ROOT"]."/bitrix/images/openmeetings", true, true);
		CopyDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/components", $_SERVER["DOCUMENT_ROOT"]."/bitrix/components", true, true);
		CopyDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/js", $_SERVER["DOCUMENT_ROOT"]."/bitrix/js/openmeetings", true, true);

		return true;
	}

	function DoUninstall() {
		global $APPLICATION;
		$OPENMEETINGS_RIGHT = $APPLICATION->GetGroupRight("openmeetings");
		if ($OPENMEETINGS_RIGHT=="W") {
			$this->UnInstallDB();
			$this->UnInstallFiles();
			//$GLOBALS["errors"] = $this->errors;
		}
		return true;
	}

	function UnInstallDB($arParams = array())	{
		global $DB, $DBType, $APPLICATION;
		$this->errors = false;

		$this->errors = $DB->RunSQLBatch($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/db/".strtolower($DB->type)."/uninstall.sql");

		$db_res = $DB->Query("SELECT ID FROM b_file WHERE MODULE_ID = 'openmeetings'");
		while($arRes = $db_res->Fetch())
			CFile::Delete($arRes["ID"]);

		UnRegisterModule("openmeetings");

		if($this->errors !== false) {
			$APPLICATION->ThrowException(implode("<br>", $this->errors));
			return false;
		}

		return true;
	}

	function UnInstallFiles() {
		DeleteDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/admin/", $_SERVER["DOCUMENT_ROOT"]."/bitrix/admin");
		//DeleteDirFiles($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/openmeetings/install/themes/.default/", $_SERVER["DOCUMENT_ROOT"]."/bitrix/themes/.default");//css
		//DeleteDirFilesEx("/bitrix/themes/.default/icons/openmeetings/");//icons
		DeleteDirFilesEx("/bitrix/images/openmeetings/");//images
		DeleteDirFilesEx("/bitrix/components/openmeetings");//component
		DeleteDirFilesEx("/bitrix/js/openmeetings/");//images
		return true;
	}

}
