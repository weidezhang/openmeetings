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
if (!defined("B_PROLOG_INCLUDED") || B_PROLOG_INCLUDED!==true)die();

if (empty($arResult["HASH"])) {
	return true;
}
$swfurl = $arResult["URL"]
	. "/?"
	. "scopeRoomId=" . $arResult["ROOM"]
	. "&secureHash=" . $arResult["HASH"]
	. "&lzproxied=solo"
	. "&language=" . $arResult["LANGUAGE"];

?>
<?=COpenmeetings::GetString(GetMessage("TEXT_GREETINGS"));?>
&nbsp;<a style="font-weight: bold; text-decoration: underline;" href="<?=$swfurl?>" target="_BLANK"><?=COpenmeetings::GetString(GetMessage("TEXT_HERE"));?></a>
<?=COpenmeetings::GetString(GetMessage("TEXT_MIDDLE"));?><?=$swfurl?><br/>
<?=COpenmeetings::GetString(GetMessage("TEXT_FOOTER"));?>
