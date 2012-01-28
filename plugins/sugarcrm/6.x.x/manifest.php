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


    $manifest = array (
         'acceptable_sugar_versions' => 
          array (
            
          ),
          'acceptable_sugar_flavors' =>
          array(
            'CE', 'PRO','ENT'
          ),
          'readme'=>'',
          'key'=>'conf',
          'author' => 'eugen.schwert@googlemail.com',
          'description' => 'Integrate OpenMeeting Web-Conferencing-System with SugarCRM.',
          'icon' => '',
          'is_uninstallable' => true,
          'name' => 'openmeetings',
          'published_date' => '2011-09-04 14:40:38',
          'type' => 'module',
          'version' => '1.2.2',
          'remove_tables' => 'prompt',
          );
$installdefs = array (
  'id' => 'openmeetings',
  /*
  'logic_hooks' => array(
	array(
		'module' => 'openmeetings',
		'hook' => 'before_save',
		'order' => 1,
		'description' => 'Get room_id form OpenMeetings',
		'file' => 'modules/openmeetings/openmeetings_gateway/openmeetingsRoomManagament.php',
		'class' => 'openmeetingsRoomManagament',
		'function' => 'createRoomWithMod',
	),
  ),*/
  'beans' => 
  array (
    0 => 
    array (
      'module' => 'openmeetings',
      'class' => 'openmeetings',
      'path' => 'modules/openmeetings/openmeetings.php',
      'tab' => true,
    ),
  ),
  'menu'=> array( 
	array('from'=> '<basepath>/SugarModules/modules/Administration/Ext/Menus/menu.ext.php',
	'to_module'=> 'Administration', ), 
  ),
  'layoutdefs' => 
  array (
    0 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/layoutdefs/openmeetings_meetings_openmeetings.php',
      'to_module' => 'openmeetings',
    ),
  ),
  'relationships' => 
  array (
    0 => 
    array (
      'meta_data' => '<basepath>/SugarModules/relationships/relationships/openmeetings_meetingsMetaData.php',
    ),
  ),
  'image_dir' => '<basepath>/icons',
  'copy' => 
  array (
    0 => 
    array (
      'from' => '<basepath>/SugarModules/modules/openmeetings',
      'to' => 'modules/openmeetings',
    ),    
    1 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/DetailViewOpenmeetings.html',
      'to' => 'modules/Administration/DetailViewOpenmeetings.html',
    ),
    2 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/DetailViewOpenmeetings.php',
      'to' => 'modules/Administration/DetailViewOpenmeetings.php',
    ),
    3 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/EditViewOpenmeetings.php',
      'to' => 'modules/Administration/EditViewOpenmeetings.php',
    ),
    4 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/EditViewOpenmeetings.html',
      'to' => 'modules/Administration/EditViewOpenmeetings.html',
    ),
    5 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/SaveOpenmeetings.php',
      'to' => 'modules/Administration/SaveOpenmeetings.php',
    ),      
    6 =>
    array (
      'from' => '<basepath>/SugarModules/logic_hooks.php',
      'to' => 'custom/modules/openmeetings/logic_hooks.php',
    ), 
    8 =>
    array (
      'from' => '<basepath>/SugarModules/modules/Meetings/logic_hooks.php',
      'to' => 'custom/modules/Meetings/logic_hooks.php',
    )
  ),
  'language' => 
  array (
    0 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/language/Meetings.php',
      'to_module' => 'Meetings',
      'language' => 'en_us',
    ),
    1 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/language/openmeetings.php',
      'to_module' => 'openmeetings',
      'language' => 'en_us',
    ),
    2 => 
    array (
      'from' => '<basepath>/SugarModules/language/application/en_us.lang.php',
      'to_module' => 'application',
      'language' => 'en_us',
    ),
    3 => 
    array (
      'from' => '<basepath>/SugarModules/modules/Administration/language/en_us_openmeetings.lang.php',
      'to_module' => 'Administration',
      'language' => 'en_us',
    ),  
  ),
  'vardefs' => 
  array (
    0 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/vardefs/openmeetings_meetings_Meetings.php',
      'to_module' => 'Meetings',
    ),
    1 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/vardefs/openmeetings_meetings_openmeetings.php',
      'to_module' => 'openmeetings',
    ),
  ),
  'layoutfields' => 
  array (
    0 => 
    array (
      'additional_fields' => 
      array (
        'Meetings' => 'conf_opeigs_meetings_name',
      ),
    ),
  ),
);