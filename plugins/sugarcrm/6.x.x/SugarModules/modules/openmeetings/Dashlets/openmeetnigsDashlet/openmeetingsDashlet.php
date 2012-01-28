<?php
if(!defined('sugarEntry') || !sugarEntry) die('Not A Valid Entry Point');
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

require_once('include/Dashlets/DashletGeneric.php');
require_once('modules/openmeetings/openmeetings.php');

class openmeetingsDashlet extends DashletGeneric { 
    function openmeetingsDashlet($id, $def = null) {
		global $current_user, $app_strings;
		require('modules/openmeetings/metadata/dashletviewdefs.php');

        parent::DashletGeneric($id, $def);

        if(empty($def['title'])) $this->title = translate('LBL_HOMEPAGE_TITLE', 'openmeetings');

        $this->searchFields = $dashletData['openmeetingsDashlet']['searchFields'];
        $this->columns = $dashletData['openmeetingsDashlet']['columns'];

        $this->seedBean = new openmeetings();        
    }
}