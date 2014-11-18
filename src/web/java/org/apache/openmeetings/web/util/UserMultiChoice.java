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
package org.apache.openmeetings.web.util;

import java.util.Collection;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.wicketstuff.select2.Select2MultiChoice;

public class UserMultiChoice extends Select2MultiChoice<User> {
	private static final long serialVersionUID = 1L;

	public UserMultiChoice(String id, IModel<Collection<User>> model) {
		super(id, model, new UserChoiceProvider());
		getSettings().setFormatResult("formatOmUser");
		getSettings().setFormatSelection("formatOmUser");
		getSettings().setEscapeMarkup("escapeOmUserMarkup");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(getClass(), "user-multi-choice.js"), "user-multi-choice")));
	}
}
