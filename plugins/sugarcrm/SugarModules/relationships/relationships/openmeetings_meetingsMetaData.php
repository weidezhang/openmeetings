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
// created: 2011-09-04 16:40:38
$dictionary["openmeetings_meetings"] = array (
  'true_relationship_type' => 'one-to-many',
  'relationships' => 
  array (
    'openmeetings_meetings' => 
    array (
      'lhs_module' => 'openmeetings',
      'lhs_table' => 'openmeetings',
      'lhs_key' => 'id',
      'rhs_module' => 'Meetings',
      'rhs_table' => 'meetings',
      'rhs_key' => 'id',
      'relationship_type' => 'many-to-many',
      'join_table' => 'openmeetings_meetings_c',
      'join_key_lhs' => 'conf_op0b58nigs_ida',
      'join_key_rhs' => 'conf_op1327ings_idb',
    ),
  ),
  'table' => 'conf_opeetnigs_meetings_c',
  'fields' => 
  array (
    0 => 
    array (
      'name' => 'id',
      'type' => 'varchar',
      'len' => 36,
    ),
    1 => 
    array (
      'name' => 'date_modified',
      'type' => 'datetime',
    ),
    2 => 
    array (
      'name' => 'deleted',
      'type' => 'bool',
      'len' => '1',
      'default' => '0',
      'required' => true,
    ),
    3 => 
    array (
      'name' => 'conf_op0b58nigs_ida',
      'type' => 'varchar',
      'len' => 36,
    ),
    4 => 
    array (
      'name' => 'conf_op1327ings_idb',
      'type' => 'varchar',
      'len' => 36,
    ),
  ),
  'indices' => 
  array (
    0 => 
    array (
      'name' => 'openmeetings_meetingsspk',
      'type' => 'primary',
      'fields' => 
      array (
        0 => 'id',
      ),
    ),
    1 => 
    array (
      'name' => 'openmeetings_meetings_ida1',
      'type' => 'index',
      'fields' => 
      array (
        0 => 'conf_op0b58nigs_ida',
      ),
    ),
    2 => 
    array (
      'name' => 'openmeetings_meetings_alt',
      'type' => 'alternate_key',
      'fields' => 
      array (
        0 => 'conf_op1327ings_idb',
      ),
    ),
  ),
);