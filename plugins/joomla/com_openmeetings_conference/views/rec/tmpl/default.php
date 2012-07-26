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
defined('_JEXEC') or die('Restricted access');
?>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#FFFFFF" text="#333333" link="#FF3366" LEFTMARGIN="0"
	TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">

	<script type="text/javascript">
			function getRecording()
			{
				
				for (var i = 0; i < document.getElementsByName("rec_select").length; i++) {
					  	
						  if (document.getElementsByName("rec_select")[i].checked == true) {
						  		
						  			window.parent.jInsertEditorText(' [openmeetings recId='+document.getElementsByName("rec_select")[i].value+'] ', "text");
							}					  			
			    		}		
				
				
				window.parent.document.getElementById('sbox-window').close();
				return false;
			}
</script>

	<?php
	echo '<div align="center"><table class="adminlist"><thead><tr><td width="20px"></td><td width="50px" style=text-align:center>ID</td><td style=text-align:center>Name</td></thead>';

	//There is an error in the parsing of 1-length array's'
	if ($this->om_recordings_return["numberOfRecords"] == 1) {

		echo '<tr><td align="right"><input type="checkbox" value='.$this->om_recordings_return["recordings"]['flvRecordingId'].' name="rec_select"></input></td>
			    <td bgcolor="#DDDDDD" style=text-align:center>&nbsp;'
			    .$this->om_recordings_return["recordings"]['flvRecordingId'].'</td>';
			    echo '<td style='
			    .'"border:1px solid #DDDDDD;text-align:right">'
			    .$this->om_recordings_return["recordings"]['fileName'].'&nbsp;</td></tr>';

	} else {

		for($i=0; $i< $this->om_recordings_return["numberOfRecords"]; $i++)
		{
			echo '<tr><td align="right"><input type="checkbox" value='.$this->om_recordings_return["recordings"][$i]['flvRecordingId'].' name="rec_select"></input></td>
			    <td bgcolor="#DDDDDD" style=text-align:center>&nbsp;'
			    .$this->om_recordings_return["recordings"][$i]['flvRecordingId'].'</td>';
			    echo '<td text-align:right">'
			    .$this->om_recordings_return["recordings"][$i]['fileName'].'&nbsp;</td></tr>';
		}
	}
	//echo '<td></td><td></td><td align="right"><button onclick="getRecording();">'.JText::_( 'insert recording' ).'</button></td>';
	echo '</table>
		  <button onclick="getRecording();">'.JText::_( 'insert recording' ).'</button>
		  </div>';
	//echo '<button align="right" onclick="getRecording();">'.JText::_( 'insert recording' ).'</button>';  //JText::_( 'PGB INS PAGEBRK' );


	?>