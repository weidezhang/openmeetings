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

$dictionary['openmeetings'] = array(
	'table'=>'openmeetings',
	'audited'=>true,
		'duplicate_merge'=>true,
		'fields'=>array (
  'is_public' => 
  array (
    'required' => false,
    'name' => 'is_public',
    'vname' => 'LBL_IS_PUBLIC',
    'type' => 'bool',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
  ),
  'is_moderated_room' => 
  array (
    'required' => false,
    'name' => 'is_moderated_room',
    'vname' => 'LBL_IS_MODERATED_ROOM',
    'type' => 'bool',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
  ),
  'allow_user_questions' => 
  array (
    'required' => false,
    'name' => 'allow_user_questions',
    'vname' => 'LBL_ALLOW_USER_QUESTIONS',
    'type' => 'bool',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
  ),
  'is_audio_only' => 
  array (
    'required' => false,
    'name' => 'is_audio_only',
    'vname' => 'LBL_IS_AUDIO_ONLY',
    'type' => 'bool',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
  ),
  'allow_recording' => 
  array (
    'required' => false,
    'name' => 'allow_recording',
    'vname' => 'LBL_ALLOW_RECORDING',
    'type' => 'bool',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
  ),
  'room_id' => 
  array (
    'required' => false,
    'name' => 'room_id',
    'vname' => 'LBL_ROOM_ID',
    'type' => 'int',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
    'enable_range_search' => false,
    'disable_num_format' => '',
  ),
  'room_link' => 
  array (
    'required' => false,
    'name' => 'room_link',
    'vname' => 'LBL_ROOM_LINK',
    'type' => 'url',
    'massupdate' => 0,
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => '255',
    'size' => '20',
    'dbType' => 'varchar',
    'gen' => '',
    'link_target' => '_self',
  ),
  'room_type_id' => 
  array (
    'required' => false,
    'name' => 'room_type_id',
    'vname' => 'LBL_ROOM_TYPE_ID',
    'type' => 'enum',
    'massupdate' => 0,
    'default' => '1',
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => 100,
    'size' => '20',
    'options' => 'room_type_list',
    'studio' => 'visible',
    'dependency' => false,
  ),
  'number_of_partizipants' => 
  array (
    'required' => false,
    'name' => 'number_of_partizipants',
    'vname' => 'LBL_NUMBER_OF_PARTIZIPANTS',
    'type' => 'enum',
    'massupdate' => 0,
    'default' => '2',
    'comments' => '',
    'help' => '',
    'importable' => 'true',
    'duplicate_merge' => 'disabled',
    'duplicate_merge_dom_value' => '0',
    'audited' => false,
    'reportable' => true,
    'len' => 100,
    'size' => '20',
    'options' => 'number_of_partizipants_list',
    'studio' => 'visible',
    'dependency' => false,
  ),
),
	'relationships'=>array (
),
	'optimistic_locking'=>true,
		'unified_search'=>true,
	);
if (!class_exists('VardefManager')){
        require_once('include/SugarObjects/VardefManager.php');
}
VardefManager::createVardef('openmeetings','openmeetings', array('basic','assignable'));