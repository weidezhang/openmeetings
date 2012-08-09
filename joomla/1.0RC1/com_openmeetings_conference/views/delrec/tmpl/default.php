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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body bgcolor="#5a5152" text="#333333" link="#FF3366" LEFTMARGIN="0"
	TOPMARGIN="0" MARGINWIDTH="0" MARGINHEIGHT="0">
	<?
	require_once("administrator/components/com_openmeetings_conference/om_gateway/openmeetings_gateway.php");

	$user =& JFactory::getUser();


	$openmeetings_gateway = new openmeetings_gateway();
	if ($openmeetings_gateway->openmeetings_loginuser()) {

		$ref = $_SERVER['HTTP_REFERER'];

		$openmeetings_gateway->deleteFlvRecording($this->rec_id);

		$msg = 'deleted';
		$mainframe = JFactory::getApplication();
		$mainframe->redirect($ref, $msg);


	}
	?>
	<object width="100%" height="100%">
		<param name="movie" value="<?=$swfurl?>"></param>
		<param name="salign" value="lt">
		<param name="scale" value="noscale"></param>
		<param name="allowFullScreen" value="true"></param>
		<param name="allowscriptaccess" value="always"></param>
		<embed width="100%" height="100%" scale="noscale" salign="lt"
			src="<?=$swfurl?>" type="application/x-shockwave-flash"
			allowscriptaccess="always" allowfullscreen="true"></embed>
	</object>
	<noscript>
		<p align="center">
			<strong>This content requires the Adobe Flash Player: <a
				href="http://www.macromedia.com/go/getflash/">Get Flash</a> </strong>!
		</p>
	</noscript>