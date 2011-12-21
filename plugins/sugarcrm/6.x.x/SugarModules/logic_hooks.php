<?php

$hook_version = 1; 
$hook_array = Array(); 

$hook_array['before_save'] = Array();
$hook_array['before_save'][] = Array(1, 'OpenMeetings', 'modules/conf_openmeetnigs/openmeetings_gateway/openmeetingsRoomManagament.php','openmeetingsRoomManagament', 'addUpdateRoomWithModerationExternalTypeAndTopBarOption');

$hook_array['before_delete'] = Array();
$hook_array['before_delete'][] = Array(1, 'OpenMeetings', 'modules/conf_openmeetnigs/openmeetings_gateway/openmeetingsRoomManagament.php','openmeetingsRoomManagament', 'deleteRoom');

//$hook_array['before_save'][] = Array(2, 'OpenMeetings', 'modules/conf_openmeetnigs/openmeetings_gateway/openmeetingsRoomManagament.php','openmeetingsRoomManagament', 'getInvitationHash');


?>
