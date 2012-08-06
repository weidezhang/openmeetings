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
if (!defined("B_PROLOG_INCLUDED") || B_PROLOG_INCLUDED!==true) die();

$PathInstall = str_replace("\\", "/", __FILE__);
$PathInstall = substr($PathInstall, 0, strlen($PathInstall)-strlen(".description.php"));

require_once($PathInstall . "../../../modules/openmeetings/classes/general/openmeetings.php"); //Need to get COpenmeetings::GetString method

$arComponentDescription = array(
    "NAME" => COpenmeetings::GetString(GetMessage("OPENMEETINGS_ROOMS_NAME")),
    "DESCRIPTION" => COpenmeetings::GetString(GetMessage("OPENMEETINGS_ROOMS_DESCRIPTION")),
    "ICON" => "/images/icon.gif",
//    "SORT" => 300,
    "PATH" => array(
        "ID" => "communication",
        "CHILD" => array(
            "ID" => "openmeetings",
            "NAME" => COpenmeetings::GetString(GetMessage("OPENMEETINGS_NAME"))
        )
    ),
);
