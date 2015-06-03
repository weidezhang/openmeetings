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
package org.apache.openmeetings.test.calendar;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.Calendar;
import java.util.List;

import org.apache.openmeetings.db.dao.calendar.AppointmentReminderTypDao;
import org.apache.openmeetings.db.entity.calendar.AppointmentReminderType;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestDatabaseStructureAppointmentReminderTyp extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestDatabaseStructureAppointmentReminderTyp.class, webAppRootKey);

	@Autowired
	private AppointmentReminderTypDao reminderTypeDao;

	@Test
	public void testAddingGroup() {

		try {

			Calendar cal = Calendar.getInstance();
			cal.set(2008, 9, 2);
			cal.get(Calendar.DAY_OF_MONTH);
			cal.getTime();

			reminderTypeDao.add(1L, "test 5 min", -1);
			List<AppointmentReminderType> listAppoints = reminderTypeDao.get();

			log.debug("Anzahl: " + listAppoints.size());

			for (AppointmentReminderType appoints : listAppoints) {
				log.debug("AppointmentReminderType: " + appoints.getName());
			}

		} catch (Exception err) {

			log.error("[testAddingGroup]", err);

		}

	}

}
