<?php
/*
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
*/

?>
<form action="<?php $_SERVER['PHP_SELF']; ?>" method="post">
<div class="input-form">
	<div class="row"><?php echo _AT('openmeetings_existing_room', $_SERVER['PHP_SELF'].'?action=view'.SEP.'room_id='.$room_id.SEP.'sid='.$om_obj->getSid()); ?></div>
	<div class="row"><?php echo _AT('openmeetings_deleting_warning'); ?></div>
	<div class="row buttons">
		<input type="hidden" name="room_id" value="<?php echo $room_id?>" />		
		<input type="submit" name="edit_room" value="<?php echo _AT('openmeetings_edit_room'); ?>"  />
		<input type="submit" name="delete_room" value="<?php echo _AT('openmeetings_delete_room'); ?>"  />
		<input type="submit" name="create_room" value="<?php echo _AT('openmeetings_create_room'); ?>"  />
	</div>
</div>

</form>
