<?php 
/*********************************************************************************
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
*  ********************************************************************************/
 

if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point'); 

global $mod_strings;
global $current_user;
if (file_exists("custom/modules/Administration/Ext/Language/en_us.lang.ext.php")){
require_once("custom/modules/Administration/Ext/Language/en_us.lang.ext.php");
}
if (file_exists("custom/modules/Administration/Ext/Language/ge_ge.lang.ext.php")){
require_once("custom/modules/Administration/Ext/Language/ge_ge.lang.ext.php");
}

//$guserid=$_REQUEST['record'];

        $module_menu[] = 
    Array("index.php?module=Administration&action=DetailViewOpenmeetings", $mod_strings['OPENMEETINGS_LINK_EDIT'],"logo");

?>
