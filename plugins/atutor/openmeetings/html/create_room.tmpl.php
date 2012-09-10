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
<div class="row">
	<p><label for="openmeetings_roomtype"><?php echo _AT('openmeetings_roomtype'); ?></label></p>
	<?php
		($_POST['openmeetings_roomtype']== 1)? $om_roomtype_conference = 'checked="checked"' : $om_roomtype_audience = 'checked="checked"';
	?>
	<input type="radio" name="openmeetings_roomtype" id="openmeetings_roomtype_conference" value="1" <?php echo $om_roomtype_conference;?>/><label for="openmeetings_roomtype_conference"><?php echo _AT('openmeetings_conference');  ?></label> 
	<input type="radio" name="openmeetings_roomtype" id="openmeetings_roomtype_audience" value="0" <?php echo $om_roomtype_audience;?>/><label for="openmeetings_roomtype_audience"><?php echo _AT('openmeetings_audience');  ?></label> 
</div>
<div class="row">
	<p><label for="openmeetings_num_of_participants"><?php echo _AT('openmeetings_num_of_participants'); ?></label></p>	
	<input type="text" name="openmeetings_num_of_participants" value="<?php echo $_POST['openmeetings_num_of_participants']; ?>" id="openmeetings_num_of_participants" size="80" style="min-width: 95%;" />
</div>
<div class="row">
	<p><label for="openmeetings_ispublic"><?php echo _AT('openmeetings_ispublic'); ?></label></p>
	<?php
		($_POST['openmeetings_ispublic']== 1)? $om_ispublic_y = 'checked="checked"' : $om_ispublic_n = 'checked="checked"';
	?>
	<input type="radio" name="openmeetings_ispublic" id="openmeetings_ispublic_y" value="1" <?php echo $om_ispublic_y;?>/><label for="openmeetings_ispublic_y"><?php echo _AT('yes');  ?></label> 
	<input type="radio" name="openmeetings_ispublic" id="openmeetings_ispublic_n" value="0" <?php echo $om_ispublic_n;?>/><label for="openmeetings_ispublic_n"><?php echo _AT('no');  ?></label> 
</div>
