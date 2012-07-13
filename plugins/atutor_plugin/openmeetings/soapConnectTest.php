<?php

define('AT_INCLUDE_PATH', '../../include/');
require (AT_INCLUDE_PATH.'vitals.inc.php');
require ('lib/openmeetings.class.php');
require ('lib/openmeetings.inc.php');
require ('lib/REST_openmeetings.php');

$rest = $_config['openmeetings_location'].'/services/UserService/';
echo '<b>Rest API endpoint:</b> '.$rest.'<br><br>';
echo 'Trying \'<b>getSession</b>\' call...<br><br>';
//$om = new openmeetings_rest_service($rest);
$om = new REST_openmeetings($rest);
//$result = $om->call('getSession', 'session_id');
//print_r($result);

?>
