<?php
// created: 2011-09-04 16:40:38
$dictionary["Meeting"]["fields"]["conf_opemeetnigs_meetings"] = array (
  'name' => 'conf_opemeetnigs_meetings',
  'type' => 'link',
  'relationship' => 'conf_openmeetnigs_meetings',
  'source' => 'non-db',
  'vname' => 'LBL_CONF_OPENMEETNIGS_MEETINGS_FROM_CONF_OPENMEETNIGS_TITLE',
  'id_name' => 'conf_op0b58nigs_ida',
);
$dictionary["Meeting"]["fields"]["conf_opeigs_meetings_name"] = array (
  'name' => 'conf_opeigs_meetings_name',
  'type' => 'relate',
  'source' => 'non-db',
  'vname' => 'LBL_CONF_OPENMEETNIGS_MEETINGS_FROM_CONF_OPENMEETNIGS_TITLE',
  'save' => true,
  'id_name' => 'conf_op0b58nigs_ida',
  'link' => 'conf_opemeetnigs_meetings',
  'table' => 'conf_openmeetnigs',
  'module' => 'conf_openmeetnigs',
  'rname' => 'name',
);
$dictionary["Meeting"]["fields"]["conf_op0b58nigs_ida"] = array (
  'name' => 'conf_op0b58nigs_ida',
  'type' => 'link',
  'relationship' => 'conf_openmeetnigs_meetings',
  'source' => 'non-db',
  'reportable' => false,
  'side' => 'right',
  'vname' => 'LBL_CONF_OPENMEETNIGS_MEETINGS_FROM_MEETINGS_TITLE',
);
