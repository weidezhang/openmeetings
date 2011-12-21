<?php
 // created: 2011-09-04 16:40:38
$layout_defs["conf_openmeetnigs"]["subpanel_setup"]['conf_opemeetnigs_meetings'] = array (
  'order' => 100,
  'module' => 'Meetings',
  'subpanel_name' => 'default',
  'sort_order' => 'asc',
  'sort_by' => 'id',
  'title_key' => 'LBL_CONF_OPENMEETNIGS_MEETINGS_FROM_MEETINGS_TITLE',
  'get_subpanel_data' => 'conf_opemeetnigs_meetings',
  'top_buttons' => 
  array (
    0 => 
    array (
      'widget_class' => 'SubPanelTopButtonQuickCreate',
    ),
    1 => 
    array (
      'widget_class' => 'SubPanelTopSelectButton',
      'mode' => 'MultiSelect',
    ),
  ),
);
