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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.isUserOnline;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.web.common.PagingNavigatorPanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.plugins.fixedheadertable.FixedHeaderTableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class UserSearchPanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final static List<Integer> itemsPerPage = Arrays.asList(10, 25, 50, 75, 100, 200, 500, 1000, 2500, 5000);
	private String text;
	private String search;
	private String offer;
	private String orderBy = "u.firstname";
	private boolean asc = true;
	private boolean searched = false;
	private final MessageDialog newMessage;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final FixedHeaderTableBehavior fixedHeader = new FixedHeaderTableBehavior("#searchUsersTable", new Options("height", 400));

	private void refresh(IPartialPageRequestHandler handler) {
		handler.add(container.add(fixedHeader));
	}
	
	public UserSearchPanel(String id) {
		super(id);
		
		add(new Form<Void>("form") {
			private static final long serialVersionUID = 1L;
			{
				add(new TextField<String>("text", new PropertyModel<String>(UserSearchPanel.this, "text")));
				add(new TextField<String>("offer", new PropertyModel<String>(UserSearchPanel.this, "offer")));
				add(new TextField<String>("search", new PropertyModel<String>(UserSearchPanel.this, "search")));
				add(new AjaxFormSubmitBehavior(this, "onsubmit") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						searched = true;
						refresh(target);
					}
				});
			}
		});
		add(newMessage = new MessageDialog("newMessage", new CompoundPropertyModel<PrivateMessage>(new PrivateMessage())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
				if (send.equals(button)) {
					refresh(handler);
				}
			}
		});
		IDataProvider<User> dp = new IDataProvider<User>() {
			private static final long serialVersionUID = 1L;

			public void detach() {
			}

			public Iterator<? extends User> iterator(long first, long count) {
				return searched ? getBean(UserDao.class).searchUserProfile(getUserId(), text, offer, search, orderBy, (int)first, (int)count, asc).iterator()
						: new ArrayList<User>().iterator();
			}

			public long size() {
				return searched ? getBean(UserDao.class).searchCountUserProfile(getUserId(), text, offer, search) : 0;
			}

			public IModel<User> model(User object) {
				return new CompoundPropertyModel<User>(object);
			}
			
		};
		final UserInfoDialog d = new UserInfoDialog("infoDialog", newMessage);
		final DataView<User> dv = new DataView<User>("users", dp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<User> item) {
				final UserContactsDao contactsDao = getBean(UserContactsDao.class);
				User u = item.getModelObject();
				final long userId = u.getId();
				item.add(new WebMarkupContainer("status").add(AttributeModifier.append("class", isUserOnline(userId) ? "online" : "offline")));
				item.add(new Label("name", getName(u)));
				item.add(new Label("tz", getBean(TimezoneUtil.class).getTimeZone(u).getID()));
				item.add(new Label("offer", u.getUserOffers()));
				item.add(new Label("search", u.getUserSearchs()));
				item.add(new WebMarkupContainer("view").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						d.open(target, userId);
					}
				}));
				item.add(new WebMarkupContainer("add").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						ContactsHelper.addUserToContactList(userId);
						refresh(target);
					}
				}).setVisible(userId != getUserId() && 0 == contactsDao.checkUserContacts(userId, getUserId())));
				item.add(new WebMarkupContainer("message").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						newMessage.reset(true).open(target, userId);
					}
				}));
				//item.add(new TooltipBehavior(new Options("content", "TODO:: Picture will be displayed"))); //FIXME 
			}
		};

		add(d, container.add(dv, new PagingNavigatorPanel("navigator", dv, itemsPerPage, 100) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				refresh(target);
			}
		}).setOutputMarkupId(true));
	}
	
	private String getName(User u) {
		return "" + u.getFirstname() + " " + u.getLastname() + " [" + u.getLogin() + "]"; //FIXME salutation
	}

}
