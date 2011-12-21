    <?php
/*********************************************************************************
 * SugarCRM Community Edition is a customer relationship management program developed by
 * SugarCRM, Inc. Copyright (C) 2004-2011 SugarCRM Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by the
 * Free Software Foundation with the addition of the following permission added
 * to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED WORK
 * IN WHICH THE COPYRIGHT IS OWNED BY SUGARCRM, SUGARCRM DISCLAIMS THE WARRANTY
 * OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with
 * this program; if not, see http://www.gnu.org/licenses or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 * 
 * You can contact SugarCRM, Inc. headquarters at 10050 North Wolfe Road,
 * SW2-130, Cupertino, CA 95014, USA. or at email address contact@sugarcrm.com.
 * 
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 * 
 * In accordance with Section 7(b) of the GNU Affero General Public License version 3,
 * these Appropriate Legal Notices must retain the display of the "Powered by
 * SugarCRM" logo. If the display of the logo is not reasonably feasible for
 * technical reasons, the Appropriate Legal Notices must display the words
 * "Powered by SugarCRM".
 ********************************************************************************/


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
          'version' => '1.2.0',
          'remove_tables' => 'prompt',
          );
$installdefs = array (
  'id' => 'openmeetings',
  /*
  'logic_hooks' => array(
	array(
		'module' => 'conf_openmeetnigs',
		'hook' => 'before_save',
		'order' => 1,
		'description' => 'Get room_id form OpenMeetings',
		'file' => 'modules/conf_openmeetnigs/openmeetings_gateway/openmeetingsRoomManagament.php',
		'class' => 'openmeetingsRoomManagament',
		'function' => 'createRoomWithMod',
	),
  ),*/
  'beans' => 
  array (
    0 => 
    array (
      'module' => 'conf_openmeetnigs',
      'class' => 'conf_openmeetnigs',
      'path' => 'modules/conf_openmeetnigs/conf_openmeetnigs.php',
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
      'from' => '<basepath>/SugarModules/relationships/layoutdefs/conf_openmeetnigs_meetings_conf_openmeetnigs.php',
      'to_module' => 'conf_openmeetnigs',
    ),
  ),
  'relationships' => 
  array (
    0 => 
    array (
      'meta_data' => '<basepath>/SugarModules/relationships/relationships/conf_openmeetnigs_meetingsMetaData.php',
    ),
  ),
  'image_dir' => '<basepath>/icons',
  'copy' => 
  array (
    0 => 
    array (
      'from' => '<basepath>/SugarModules/modules/conf_openmeetnigs',
      'to' => 'modules/conf_openmeetnigs',
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
      'to' => 'custom/modules/conf_openmeetnigs/logic_hooks.php',
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
      'from' => '<basepath>/SugarModules/relationships/language/conf_openmeetnigs.php',
      'to_module' => 'conf_openmeetnigs',
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
      'from' => '<basepath>/SugarModules/relationships/vardefs/conf_openmeetnigs_meetings_Meetings.php',
      'to_module' => 'Meetings',
    ),
    1 => 
    array (
      'from' => '<basepath>/SugarModules/relationships/vardefs/conf_openmeetnigs_meetings_conf_openmeetnigs.php',
      'to_module' => 'conf_openmeetnigs',
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