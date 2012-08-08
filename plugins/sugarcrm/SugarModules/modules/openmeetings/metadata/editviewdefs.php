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

$module_name = 'openmeetings';
$viewdefs [$module_name] = 
array (
  'EditView' => 
  array (
    'templateMeta' => 
    array (
      'maxColumns' => '2',
      'widths' => 
      array (
        0 => 
        array (
          'label' => '10',
          'field' => '30',
        ),
        1 => 
        array (
          'label' => '10',
          'field' => '30',
        ),
      ),
      'useTabs' => false,
      'syncDetailEditViews' => false,
    ),
    'panels' => 
    array (
      'default' => 
      array (
        0 => 
        array (
          0 => 'name',
          1 => 'assigned_user_name',
        ),
        1 => 
        array (
          0 => 
          array (
            'name' => 'number_of_partizipants',
            'studio' => 'visible',
            'label' => 'LBL_NUMBER_OF_PARTIZIPANTS',
          ),
          1 => 
          array (
            'name' => 'room_type_id',
            'studio' => 'visible',
            'label' => 'LBL_ROOM_TYPE_ID',
          ),
        ),
        2 => 
        array (
          0 => 
          array (
            'name' => 'is_public',
            'label' => 'LBL_IS_PUBLIC',
          ),
          1 => 
          array (
            'name' => 'is_moderated_room',
            'label' => 'LBL_IS_MODERATED_ROOM',
          ),
        ),
        3 => 
        array (
          0 => 
          array (
            'name' => 'allow_user_questions',
            'label' => 'LBL_ALLOW_USER_QUESTIONS',
          ),
          1 => 
          array (
            'name' => 'is_audio_only',
            'label' => 'LBL_IS_AUDIO_ONLY',
          ),
        ),
        4 => 
        array (
          0 => 
          array (
            'name' => 'allow_recording',
            'label' => 'LBL_ALLOW_RECORDING',
          ),
          1 => '',
        ),
        5 => 
        array (
          0 => 'description',
          1 => '',
        ),
      ),
    ),
  ),
);
?>
