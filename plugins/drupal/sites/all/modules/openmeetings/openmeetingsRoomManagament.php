<?php


require_once("openmeetings_gateway.php");


class openmeetingsRoomManagament {


	function createMyRoomWithMod($data) {
		
		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			
			$openmeetings = new stdClass;
			
			$openmeetings->name = $data['name'];
			$openmeetings->roomtypes_id = $data['roomtype_id'];
			$openmeetings->comment = $data['comment'];
			$openmeetings->numberOfPartizipants = $data['number_of_partizipants'];
			$openmeetings->ispublic = $data['is_public'];
			$openmeetings->isAudioOnly = $data['isAudioOnly'];
			$openmeetings->allowUserQuestions = $data['allowUserQuestions'];
			$openmeetings->isDemoRoom = 0;
			$openmeetings->demoTime = "";
			$openmeetings->isModeratedRoom = $data['is_moderated_room'];
								
			$roomid = $openmeetings_gateway->openmeetings_createRoomWithModAndTypeAndAudioOption($openmeetings);	
				
			return $roomid;
						
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration [4]";			
		}
	}
	
	function updateRoomWithModerationAndQuestions($data) {
		

		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			
			$openmeetings = new stdClass;
			
			$openmeetings->name = $data['name'];
			$openmeetings->room_id = $data['room_id'];
			$openmeetings->roomtypes_id = $data['roomtype_id'];
			$openmeetings->comment = $data['comment'];
			$openmeetings->numberOfPartizipants = $data['number_of_partizipants'];
			$openmeetings->ispublic = $data['is_public'];
			$openmeetings->appointment = $data['appointment'];
			$openmeetings->isDemoRoom = 0;
			$openmeetings->demoTime = "";
			$openmeetings->isModeratedRoom = $data['is_moderated_room'];
			$openmeetings->isAudioOnly = $data['isAudioOnly'];
			$openmeetings->allowUserQuestions = $data['allowUserQuestions'];
			
			$roomid = $openmeetings_gateway->openmeetings_updateRoomWithModerationAndQuestions($openmeetings);	
			
			return $roomid;
						
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration [7]";
			
		}
	}
	
	function deleteRoom ($data){

		$openmeetings_gateway = new openmeetings_gateway();
		if ($openmeetings_gateway->openmeetings_loginuser()) {
			
			$openmeetings = new stdClass;
			
			$openmeetings->room_id = $data['room_id'];	
			if ($openmeetings->room_id > 0) {
				$roomid = $openmeetings_gateway->deleteRoom($openmeetings);					
				return $roomid;
			} else {
				return -1;
			}
						
		} else {
			echo "Could not login User to OpenMeetings, check your OpenMeetings Module Configuration [8]";
			
		}


	}


}


?>