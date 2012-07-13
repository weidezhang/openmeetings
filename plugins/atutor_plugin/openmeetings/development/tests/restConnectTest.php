<?php

require ('../../lib/REST_openmeetings.php');

//$rest = $_config['openmeetings_location'].'/services/UserService/';
$rest = 'http://localhost:5080/openmeetings/services/';

// calls/values I have tested with
$calls = array('getSession', 'loginUser');
$rvs = array('session_id');


echo '<b>Rest API endpoint:</b> '.$rest.'<br><br>';
?>
<form action="" method="get">
Call<br><select name="call" >
<option value=""></option>
<?php
foreach ($calls as $call) {
	echo '<option value="'.$call.'">'.$call.'</option>';
}
?>
</select>
<br>Params (e.g. a=1&b=2)<br>
<input type="text" name="params" value="">
<br>Return value<br>
<select name="rv" >
<option value=""></option>
<?php
foreach ($rvs as $rv) {
	echo '<option value="'.$rv.'">'.$rv.'</option>';
}
?>
</select>
<input type="submit" value="Test"/>
</form>
<?php
$call = $_REQUEST['call'];
$returnVal = $_REQUEST['rv'];
$params = $_REQUEST['params']; 
if ($call) {
	//$fullRequest = $call.$params;
	
	$om = new REST_openmeetings($rest);
	
	$paramArray = array();
	if ($params) {
		$qsArray = explode('&', $params);
		foreach ($qsArray as $q) {
			$temp = explode('=', $q);
			$paramArray[$temp[0]] = $temp[1];
		
		}
	}
	echo 'Testing <b>'.$call.'</b> call with params:<br>';
	echo '<pre>';
	print_r($paramArray);
	echo '</pre><br>';
	$result = $om->_performAPICall($call, 'UserService', $paramArray);
	echo '<b>Result:</b><br>';
	if ($returnVal) {
		echo '['.$returnVal.'] => '.$result[$returnVal];
	} else {
		echo '<pre>';
		print_r($result);
		echo '</pre>';
	}
}

?>
