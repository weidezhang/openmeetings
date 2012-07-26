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
defined('_JEXEC') or die('Restricted access'); ?>

<form action="index.php" method="post" name="adminForm" id="adminForm">

	<div class="col100">
		<fieldset class="adminform">
			<legend>
			<?php echo JText::_( 'Room Details' ); ?>
			</legend>
			<table class="admintable">

				<tr>
					<td width="100" align="right" class="key"><label for="maxbandwidth">
					<?php echo JText::_( 'Roomname' ); ?>: </label>
					</td>
					<td><input class="text_area" type="text" name="name" id="name"
						size="32" maxlength="64" value="<?php echo $this->croom->name;?>" />
					</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Roomtype:' ); ?>
					</td>
					<td><select name="roomtype_id" id="roomtype_id">
							<option value="1"
							<?=($this->croom->roomtype_id==1?"selected":"")?>>
								<?php echo JText::_( 'Conference (max 20 Users recommended, Democratic)' ); ?>
							</option>
							<option value="2"
							<?=($this->croom->roomtype_id==2?"selected":"")?>>
								<?php echo JText::_( 'Audience (max 50 Users recommended, Moderated)' ); ?>
							</option>
							<option value="3"
							<?=($this->croom->roomtype_id==3?"selected":"")?>>
								<?php echo JText::_( 'Restricted (max 150 Users recommended, Moderated)' ); ?>
							</option>
							<option value="4"
							<?=($this->croom->roomtype_id==4?"selected":"")?>>
								<?php echo JText::_( 'Interview (Only 2 Users, no Whiteboard, Record Audio/Video, Moderated)' ); ?>
							</option>
							<option value="5"
							<?=($this->croom->roomtype_id==5?"selected":"")?>>
								<?php echo JText::_( 'Simple Room (2 Users only, No-Moderation/Democratic)' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'Type of that room.'); ?></td>
				</tr>
				<tr>
					<td width="100" align="right" class="key"><label for="layout"> <?php echo JText::_( 'Comment' ); ?>:
					</label>
					</td>
					<td><textarea name="comment" cols="64" rows="3" class="text_area"
							id="comment">
							<?php echo $this->croom->comment;?>
						</textarea>
					</td>
					<td>&nbsp;</td>
				</tr>

				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Number of partizipants' ); ?>:</td>
					<td><input class="text_area" type="text"
						name="number_of_partizipants" id="number_of_partizipants" size="8"
						maxlength="9" value="<?=($this->croom->number_of_partizipants)?>" />
					</td>
					<td><?php echo JText::_( 'The maximum users allowed in this room.'); ?>
					</td>
				</tr>

				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Public (Listed):' ); ?>
					</td>
					<td><select name="is_public" id="is_public">
							<option value="0" <?=(!$this->croom->is_public?"selected":"")?>>
							<?php echo JText::_( 'No' ); ?>
							</option>
							<option value="1" <?=($this->croom->is_public?"selected":"")?>>
							<?php echo JText::_( 'Yes' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'If this room is public, it will appear in the frontend for everybody.'); ?>
					</td>
				</tr>
				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Appointment:' ); ?>
					</td>
					<td><select name="appointment" id="appointment">
							<option value="0" <?=(!$this->croom->appointment?"selected":"")?>>
							<?php echo JText::_( 'No' ); ?>
							</option>
							<option value="1" <?=($this->croom->appointment?"selected":"")?>>
							<?php echo JText::_( 'Yes' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'Is it a Calendar Room (use false by default).'); ?>
					</td>
				</tr>
				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Moderated Room:' ); ?>
					</td>
					<td><select name="is_moderated_room" id="is_moderated_room">
							<option value="0"
							<?=(!$this->croom->is_moderated_room?"selected":"")?>>
								<?php echo JText::_( 'No' ); ?>
							</option>
							<option value="1"
							<?=($this->croom->is_moderated_room?"selected":"")?>>
								<?php echo JText::_( 'Yes' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'Users have to wait untill a Moderator arrives. Use the becomeModerator param in setUserObjectAndGenerateRoomHash to set a user as default Moderator.'); ?>
					</td>
				</tr>

				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Select User:' ); ?>
					</td>
					<td><?php echo $this->owner ?>
					</td>
					<td><?php echo JText::_( 'Users which will be able to use the room.'); ?>
					</td>
				</tr>

				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Room-Validity:' ); ?>
					</td>
					<td><select name="room_validity" id="room_validity" onchange="enableTimeRepeatButtons()">
							<option value="0" <?=(!$this->croom->room_validity?"selected":"")?>>
								<?php echo JText::_( 'Static' ); ?>
							</option>
							<option value="1" <?=($this->croom->room_validity?"selected":"")?>>
								<?php echo JText::_( 'Dynamic' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'Static rooms are always visible on the front end. Dynamic rooms are visible only in a time period.'); ?>
					</td>
				</tr>

				<tr>
					<td width="100" align="right" class="key"><label for="timeout"> <?php echo JText::_( 'Date' ); ?>:
					</label></td>
					<td><input type="date" name="date_type" id="date_type"
						disabled="true"
						value="<?=(($this->croom->id > 0) ? $this->croom->date_type : '0000-00-00')?>" />
					</td>
					<td><?php echo JText::_( 'Date for dynamic rooms. On this date the room will appear in front end.' ); ?>
					</td>
				</tr>
				<tr>
					<td width="100" align="right" class="key"><label for="timeout"> <?php echo JText::_( 'Time' ); ?>:
					</label></td>
					<td><input type="time" name="time_type" id="time_type"
						disabled="true"
						value="<?=(($this->croom->id > 0) ? $this->croom->time_type : '00:00:00' )?>" />
						</ td>
					
					<td><?php echo JText::_( 'Time for dynamic rooms. The room will appear in front end until this time.' ); ?>
					</td>
				</tr>
				<tr>
					<td width="100" align="right" class="key"><label for="duration"> <?php echo JText::_( 'Duration' ); ?>:
					</label></td>
					<td>
						<!--
			<input  type="time" name="duration" id="duration"  disabled="true" value="<?=($this->croom->duration)?>" />
			 --> <select name="duration" id="duration">
							<option value="30"
							<?=($this->croom->duration==30?"selected":"")?>>
								<?php echo JText::_( '30 min' ); ?>
							</option>
							<option value="60"
							<?=($this->croom->duration==60?"selected":"")?>>
								<?php echo JText::_( '60 min' ); ?>
							</option>
							<option value="120"
							<?=($this->croom->duration==120?"selected":"")?>>
								<?php echo JText::_( '2 hours' ); ?>
							</option>
							<option value="180"
							<?=($this->croom->duration==180?"selected":"")?>>
								<?php echo JText::_( '3 hours' ); ?>
							</option>
							<option value="240"
							<?=($this->croom->duration==240?"selected":"")?>>
								<?php echo JText::_( '4 hours' ); ?>
							</option>
							<option value="300"
							<?=($this->croom->duration==300?"selected":"")?>>
								<?php echo JText::_( '5 hours' ); ?>
							</option>
							<option value="360"
							<?=($this->croom->duration==360?"selected":"")?>>
								<?php echo JText::_( '6 hours' ); ?>
							</option>
							<option value="420"
							<?=($this->croom->duration==420?"selected":"")?>>
								<?php echo JText::_( '7 hours' ); ?>
							</option>
							<option value="480"
							<?=($this->croom->duration==480?"selected":"")?>>
								<?php echo JText::_( '8 hours' ); ?>
							</option>
							<option value="540"
							<?=($this->croom->duration==540?"selected":"")?>>
								<?php echo JText::_( '9 hours' ); ?>
							</option>
							<option value="600"
							<?=($this->croom->duration==600?"selected":"")?>>
								<?php echo JText::_( '10 hours' ); ?>
							</option>
							<option value="660"
							<?=($this->croom->duration==660?"selected":"")?>>
								<?php echo JText::_( '11 hours' ); ?>
							</option>
							<option value="720"
							<?=($this->croom->duration==720?"selected":"")?>>
								<?php echo JText::_( '12 hours' ); ?>
							</option>
							<option value="1440"
							<?=($this->croom->duration==1440?"selected":"")?>>
								<?php echo JText::_( '24 hours' ); ?>
							</option>
							<option value="2160"
							<?=($this->croom->duration==2160?"selected":"")?>>
								<?php echo JText::_( '36 hours' ); ?>
							</option>
							<option value="2880"
							<?=($this->croom->duration==2880?"selected":"")?>>
								<?php echo JText::_( '48 hours' ); ?>
							</option>
					</select> </ td>
					
					<td><?php echo JText::_( 'Time for dynamic rooms. The room will appear in front end until this time.' ); ?>
					</td>
				</tr>

				<tr>
					<td width="100" align="right" class="key"><?php echo JText::_( 'Repeat:' ); ?>
					</td>
					<td><input type="checkbox" name="repeat_type" id="repeat_type"
						value="1" <?=($this->croom->repeat_type==1?"checked":"")?> />
					</td>
					<td><?php echo JText::_( 'Select when the room will used periodic.' ); ?>
					</td>
				</tr>

				<tr>
					<td align="right" class="key"><?php echo JText::_( 'Weekday:' ); ?>
					</td>
					<td><select name="weekday_type" id="weekday_type" disabled="">
							<option value="0"
							<?=($this->croom->weekday_type==0?"selected":"")?>>
								<?php echo JText::_( '-Select a weekday-' ); ?>
							</option>
							<option value="2"
							<?=($this->croom->weekday_type==2?"selected":"")?>>
								<?php echo JText::_( 'Monday' ); ?>
							</option>
							<option value="3"
							<?=($this->croom->weekday_type==3?"selected":"")?>>
								<?php echo JText::_( 'Tuesday' ); ?>
							</option>
							<option value="4"
							<?=($this->croom->weekday_type==4?"selected":"")?>>
								<?php echo JText::_( 'Wednesday' ); ?>
							</option>
							<option value="5"
							<?=($this->croom->weekday_type==5?"selected":"")?>>
								<?php echo JText::_( 'Thursday' ); ?>
							</option>
							<option value="6"
							<?=($this->croom->weekday_type==6?"selected":"")?>>
								<?php echo JText::_( 'Friday' ); ?>
							</option>
							<option value="7"
							<?=($this->croom->weekday_type==7?"selected":"")?>>
								<?php echo JText::_( 'Saturday' ); ?>
							</option>
							<option value="1"
							<?=($this->croom->weekday_type==1?"selected":"")?>>
								<?php echo JText::_( 'Sunday' ); ?>
							</option>
					</select></td>
					<td><?php echo JText::_( 'Select the weekday on which the room should appear in front end. '); ?>
					</td>
				</tr>
				<pre>
	</pre>
			</table>
		</fieldset>
		<br />

	</div>
	<div class="clr"></div>

	<input type="hidden" name="option" value="com_openmeetings_conference" />
	<input type="hidden" name="timecreated"
		value="<?=$this->croom->timecreated?>" /> <input type="hidden"
		name="id" value="<?php echo $this->croom->id; ?>" /> <input
		type="hidden" name="room_id"
		value="<?php echo $this->croom->room_id; ?>" /> <input type="hidden"
		name="task" value="" /> <input type="hidden" name="controller"
		value="room" />
</form>
<script language="javascript" type="text/javascript">
	window.onload(enableTimeRepeatButtons());

	function submitbutton(pressbutton) {
		var form = document.adminForm;
		if (pressbutton == 'cancel') {
			submitform( pressbutton );
			return;
		}
		
		// do field validation
		if (trim(document.adminForm.owner.value) == "0") {
			alert( "<?php echo JText::_( 'You must select a user.', true ); ?>" );
		} else {
			submitform( pressbutton );
		}
	}
	
	function enableTimeRepeatButtons() {
		 var bool = true;
		 if (document.adminForm.room_validity.value == 1) {
		 	bool = false;
		 }
		 document.adminForm.date_type.disabled=bool;
		 document.adminForm.time_type.disabled=bool; 
		 document.adminForm.duration.disabled=bool;
		 document.adminForm.repeat_type.disabled=bool;
		 document.adminForm.weekday_type.disabled=bool;
	}
	
</script>
