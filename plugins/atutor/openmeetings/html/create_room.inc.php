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

//Get default values if POST is empty
$_POST = loadDefaultValues($_POST);	//openmeetings.inc.php

?>
<form action="<?php $_SERVER['PHP_SELF']; ?>" method="post">
	<div class="input-form">
		<div class="row"><p><?php echo _AT('openmeetings_create_room_instr'); ?></p></div>
	</div>
	<div class="input-form">
		<?php include ('create_room.tmpl.php'); ?>

		<div class="row buttons">
			<input type="submit" name="create_room" value="<?php echo _AT('openmeetings_create_room'); ?>"  />
		</div>
	</div>
</form>

<script type="text/javascript">
	function om_toggler(name, enabled){
		var obj_w = document.getElementById(name + '_w');
		var obj_h = document.getElementById(name + '_h');
		if(enabled==true) {
			obj_w.disabled = "";	
			obj_h.disabled = "";	
		} else if (enabled==false){
			obj_w.disabled = "disabled";
			obj_h.disabled = "disabled";
		}
	}
</script>
