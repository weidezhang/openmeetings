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
package org.apache.openmeetings.test.session;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.openmeetings.db.dao.room.ClientDao;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDbSessionGetRoomIds extends AbstractJUnitDefaults {

	protected static final Logger log = Red5LoggerFactory.getLogger(
			TestDbSessionGetRoomIds.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ServerDao serverDao;

	@Autowired
	private ClientDao clientDao;

	@Test
	public void testDbSessionFunctions() {

		clientDao.cleanAllClients();
		
		List<Server> serverList = serverDao.getActiveServers();

		Server server = null;
		if (serverList.size() > 0) {
			server = serverList.get(0);
		} else {
			server = new Server();
			server.setName("Test Server");
			server.setActive(true);
			serverDao.update(server, null);
		}

		Client cl1 = new Client();
		cl1.setStreamid("1");
		cl1.setServer(server);
		cl1.setUserId(1L);
		cl1.setRoomId(1L);
		cl1.setPublicSID("public1");
		clientDao.add(cl1);

		Client cl2 = new Client();
		cl2.setStreamid("2");
		cl2.setServer(server);
		cl2.setRoomId(1L);
		cl2.setUserId(2L);
		cl2.setPublicSID("public2");
		clientDao.add(cl2);

		Client cl3 = new Client();
		cl3.setStreamid("3");
		cl3.setServer(server);
		cl3.setRoomId(3L);
		cl3.setUserId(3L);
		cl3.setPublicSID("public3");
		clientDao.add(cl3);
		
		List<Long> roomids = clientDao.getRoomsIdsByServer(server);

		assertEquals(2, roomids.size());
		
		//delete all
		clientDao.cleanAllClients();

		int countAll = clientDao.countClients();
		assertEquals(0, countAll);
	}

}
