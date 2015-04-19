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
package org.apache.openmeetings.web.user.dashboard;

import org.apache.openmeetings.web.app.Application;

import ro.fortsoft.wicket.dashboard.WidgetDescriptor;

public class StartWidgetDescriptor implements WidgetDescriptor {
	private static final long serialVersionUID = 1L;

	public String getName() {
		return Application.getString(774L);
	}

	public String getProvider() {
		return "Apache Openmeetings";
	}

	public String getDescription() {
		return Application.getString(804L);
	}

	public String getWidgetClassName() {
		return StartWidget.class.getName();
	}

	public String getTypeName() {
		return "om.widget.start";
	}
}
