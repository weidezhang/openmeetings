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
defined( '_JEXEC' ) or die( 'Restricted access' );
?>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#5a5152" text="#333333" link="#FF3366" LEFTMARGIN="0"
	TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">
	<?
	require_once("administrator/components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");

	$this->params = &JComponentHelper::getParams( 'com_openmeetings_conference' );
	$url_server = $this->params->get( 'url' );

	$user = JFactory::getUser();

	$openmeetings_gateway = new openmeetings_gateway();
	if ($openmeetings_gateway->openmeetings_loginuser()) {
		if ($user->authorise('core.admin') || $user->authorise('core.manage')) {
			//$becomemoderator=1;
		} else {
			//$becomemoderator=0;
		}

		if ($user->guest) {
			$openmeetings->username = "public";
			$openmeetings->firstname = "public";
			$openmeetings->lastname = "public";
			$openmeetings->externalUserId = 1;
			$openmeetings->recording_id = $this->rec_id;
		} else {
			$openmeetings->username = $user->username;
			$openmeetings->firstname = $user->name;
			$openmeetings->lastname = "";
			$openmeetings->externalUserId = $user->id;
			$openmeetings->recording_id = $this->rec_id;
		}


		$rechash = $openmeetings_gateway->setUserObjectAndGenerateRecordingHashByURL($openmeetings);

		$language= $user->getParam('language', 'en-GB');

		if($language == 'en-GB'){
			$om_laguage_id = 1;
		}else if($language == 'de-DE'){
			$om_laguage_id = 2;
		}else if($language == 'fr-FR'){
			$om_laguage_id = 3;
		}else if($language == 'it-IT'){
			$om_laguage_id = 4;
		}else if($language == 'pt-PT'){
			$om_laguage_id = 5;
		}else if($language == 'pt-BR'){
			$om_laguage_id = 6;
		}else if($language == 'es-ES'){
			$om_laguage_id = 7;
		}else if($language == 'ru-RU'){
			$om_laguage_id = 8;
		}else if($language == 'swedish'){
			$om_laguage_id = 9;
		}else if($language == 'default'){
			$om_laguage_id = 10;
		}else if($language == 'default'){
			$om_laguage_id = 11;
		}else if($language == 'ko-KR'){
			$om_laguage_id = 12;
		}else if($language == 'ar-AA'){
			$om_laguage_id = 13;
		}else if($language == 'default'){
			$om_laguage_id = 14;
		}else if($language == 'default'){
			$om_laguage_id = 15;
		}else if($language == 'default'){
			$om_laguage_id = 16;
		}else if($language == 'default'){
			$om_laguage_id = 17;
		}else if($language == 'ua-UA'){
			$om_laguage_id = 18;
		}else if($language == 'default'){
			$om_laguage_id = 19;
		}else if($language == 'persian'){
			$om_laguage_id = 20;
		}else if($language == 'default'){
			$om_laguage_id = 21;
		}else if($language == 'default'){
			$om_laguage_id = 22;
		}else if($language == 'default'){
			$om_laguage_id = 23;
		}else if($language == 'default'){
			$om_laguage_id = 24;
		}else if($language == 'default'){
			$om_laguage_id = 25;
		}else if($language == 'nl-NL'){
			$om_laguage_id = 26;
		}else if($language == 'default'){
			$om_laguage_id = 27;
		}else if($language == 'ca-ES'){
			$om_laguage_id = 28;
		}else if($language == 'default'){
			$om_laguage_id = 29;
		}else if($language == 'da-DK'){
			$om_laguage_id = 30;
		}else {
			$om_laguage_id = 2;
		}

		if (!empty($rechash)) {
			$swfurl = $url_server.
					"/openmeetings/?" .					
					"secureHash=" .$rechash.
					"&lzproxied=solo" .								
					"&language=".$om_laguage_id;	

		}
	}
	?>
	<iframe src="<?=$swfurl?>" width="100%" height="100%">
		<p align="center">
			<strong>This content requires the Adobe Flash Player: <a
				href="http://www.macromedia.com/go/getflash/">Get Flash</a> </strong>!
		</p>
	</iframe>
