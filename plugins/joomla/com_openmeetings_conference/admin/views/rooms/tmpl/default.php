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
<?php
function ms2tm($ms)
{
	$seconds=floor($ms/1000);
	$minutes=floor($seconds/60);
	$seconds=$seconds-$minutes*60;
	$hours=floor($minutes/60);
	$minutes=$minutes-$hours*60;
	return $hours."h ".$minutes."m ".$seconds."s";
}

?>

<form action="index.php" method="post" name="adminForm">
	<div id="editcell">
		<table class="adminlist">
			<thead>
				<tr>
					<th width="5"><?php echo JText::_( 'ID' ); ?>
					</th>
					<th width="20">
						<!-- checkbox to check all items --> <input type="checkbox"
						name="toggle" value=""
						onclick="checkAll( <?php echo count($this->items); ?> );" />
					</th>
					<th><?php echo JText::_( 'Room' ); ?>
					</th>
					<!--  <th><?php echo JText::_( 'Credits' ); ?></th> -->
					<th><?php echo JText::_( 'Room Link' ); ?></th>
				</tr>
			</thead>

			<?php
			$k = 0;
			//print_r($this->items);
			//exit();
			for ($i=0, $n=count( $this->items ); $i < $n; $i++)
			{
				$row =& $this->items[$i];
				$checked = JHTML::_('grid.id', $i, $row->id );       //get checkbox HTML
				// get link HTML to edit task for this greeting

				$link = JRoute::_( 'index.php?option=com_openmeetings_conference&controller=room&task=edit&cid[]='.$row->id );
				$participantlink=JURI::root() .JRoute::_( 'administrator/index.php?option=com_openmeetings_conference&view=om&format=raw&room='. urlencode($row->room_id));
				?>
			<tr class="<?php echo "row$k"; ?>">
				<td><?php echo $row->id; ?>
				</td>
				<td align="center"><?php echo $checked; ?></td>
				<td align="center"><a href="<?php echo $link; ?>"><?php echo $row->name; ?>
				</a>
				</td>
				<!--     <td align="center"><?
						echo ms2tm($row->timeused)." / ".($row->credits ? ms2tm($row->credits * 60000).($row->creditsreset?" ".JText::_( 'each' )." ".$row->creditsreset." ".JText::_( 'days' ):"") : JText::_( 'Unlimited' ));
						?></td>-->
				<td><a target="_blank" href="<?php echo $participantlink; ?>"><?php echo $participantlink; ?>
				</a></td>
			</tr>
			<?php
			$k = 1 - $k; //switch row class
			}
			?>
		</table>
	</div>
	<input type="hidden" name="option" value="com_openmeetings_conference" />
	<input type="hidden" name="task" value="" /> <input type="hidden"
		name="boxchecked" value="0" /> <input type="hidden" name="controller"
		value="room" />
</form>
