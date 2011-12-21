<?php
$module_name = 'conf_openmeetnigs';
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
