--   Licensed under the Apache License, Version 2.0 (the "License");
--   you may not use this file except in compliance with the License.
--   You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--   Unless required by applicable law or agreed to in writing, software
--   distributed under the License is distributed on an "AS IS" BASIS,
--   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--   See the License for the specific language governing permissions and
--   limitations under the License.
DROP TABLE IF EXISTS `#__om_rooms`;

CREATE TABLE `#__om_rooms` (
  `id` int(10) unsigned NOT NULL auto_increment,  
  `name` varchar(100) NOT NULL,
  `room_id` int(10) NOT NULL,
  `owner` int(10) NOT NULL,
  `roomtype_id` int(10) NOT NULL,  
  `comment` varchar(100) NOT NULL,
  `number_of_partizipants` int(10) NULL,
  `is_public` tinyint(3) NOT NULL default '0',
  `appointment` tinyint(3) NOT NULL default '0',
  `is_moderated_room` tinyint(3) NOT NULL default '1',
  `allow_private_chat` tinyint(3) NOT NULL default '1',
  `hide_user_list_for_non_moderators` tinyint(3) NOT NULL default '1',
  
  `room_validity` int(10) NOT NULL default '0',
  `date_type` date NOT NULL default 0,
  `time_type` time NOT NULL default 0,
  `duration` int(10) NOT NULL default '0',
  `repeat_type` tinyint(3) NOT NULL default '0',
  `weekday_type` int(10) NOT NULL default '0',
  `logo` varchar(100) NOT NULL,
  
  
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COMMENT='Openmeetings: Conference Rooms' AUTO_INCREMENT=5 ;

CREATE TABLE `#__om_rooms_users` (
  `id` int(10) unsigned NOT NULL auto_increment,  
  `om_room_id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
    
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COMMENT='Openmeetings: Conference Rooms User' AUTO_INCREMENT=5 ;


CREATE TABLE `#__om_rooms_flexigroups` (
  `id` int(10) unsigned NOT NULL auto_increment,  
  `om_room_id` int(10) NOT NULL,
  `flexigroup_id` int(10) NOT NULL,
    
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COMMENT='Openmeetings: Conference Rooms Flexigroups' AUTO_INCREMENT=5 ;
