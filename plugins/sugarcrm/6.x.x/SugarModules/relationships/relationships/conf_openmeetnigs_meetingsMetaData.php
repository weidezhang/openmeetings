<?php
// created: 2011-09-04 16:40:38
$dictionary["conf_openmeetnigs_meetings"] = array (
  'true_relationship_type' => 'one-to-many',
  'relationships' => 
  array (
    'conf_openmeetnigs_meetings' => 
    array (
      'lhs_module' => 'conf_openmeetnigs',
      'lhs_table' => 'conf_openmeetnigs',
      'lhs_key' => 'id',
      'rhs_module' => 'Meetings',
      'rhs_table' => 'meetings',
      'rhs_key' => 'id',
      'relationship_type' => 'many-to-many',
      'join_table' => 'conf_opeetnigs_meetings_c',
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
      'name' => 'conf_opemeetnigs_meetingsspk',
      'type' => 'primary',
      'fields' => 
      array (
        0 => 'id',
      ),
    ),
    1 => 
    array (
      'name' => 'conf_opemeetnigs_meetings_ida1',
      'type' => 'index',
      'fields' => 
      array (
        0 => 'conf_op0b58nigs_ida',
      ),
    ),
    2 => 
    array (
      'name' => 'conf_opemeetnigs_meetings_alt',
      'type' => 'alternate_key',
      'fields' => 
      array (
        0 => 'conf_op1327ings_idb',
      ),
    ),
  ),
);