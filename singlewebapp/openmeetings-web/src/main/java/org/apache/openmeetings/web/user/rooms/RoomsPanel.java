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
package org.apache.openmeetings.web.user.rooms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.util.io.IOUtils;

import com.googlecode.wicket.jquery.ui.form.button.Button;

public class RoomsPanel extends UserPanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer clientsContainer = new WebMarkupContainer("clientsContainer");
	private final WebMarkupContainer details = new WebMarkupContainer("details");
	private final ListView<Client> clients;
	private IModel<Long> roomID = Model.of((Long)null);
	private IModel<String> roomName = Model.of((String)null);
	private IModel<String> roomComment = Model.of((String)null);
	private List<Client> clientsInRoom = null;
	private long roomId = 0;

	public RoomsPanel(String id, List<Room> rooms) {
		super(id);
		add(new ListView<Room>("list", rooms) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Room> item) {
				final Room r = item.getModelObject();
				WebMarkupContainer roomContainer;
				item.add((roomContainer = new WebMarkupContainer("roomContainer")).add(new AjaxEventBehavior("onclick"){
					private static final long serialVersionUID = 1L;
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						roomId = r.getId();
						updateRoomDetails(target);
					}
				}));
				roomContainer.add(new Label("roomName", r.getName()));
				final Label curUsers = new Label("curUsers", new Model<Integer>(Application.getBean(ISessionManager.class).getClientListByRoom(r.getId()).size()));
				roomContainer.add(curUsers.setOutputMarkupId(true));
				roomContainer.add(new Label("totalUsers", r.getNumberOfPartizipants()));
				item.add(new Button("enter").add(new RoomEnterBehavior(r.getId())));
				roomContainer.add(new AjaxLink<Void>("refresh") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						roomId = r.getId();
						target.add(curUsers.setDefaultModelObject(Application.getBean(ISessionManager.class).getClientListByRoom(r.getId()).size()));						updateRoomDetails(target);
					}
				});
			}
		});
		
		// Users in this Room
		add(details.setOutputMarkupId(true).setVisible(rooms.size() > 0));
		details.add(new Label("roomId", roomID));
		details.add(new Label("roomName", roomName));
		details.add(new Label("roomComment", roomComment));
		clients = new ListView<Client>("clients", clientsInRoom){
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Client> item) {
				Client client = item.getModelObject();
				final Long userId = client.getUserId();
				item.add(new Image("clientImage", new ByteArrayResource("image/jpeg") {
					private static final long serialVersionUID = 1L;

					@Override
					protected ResourceResponse newResourceResponse(Attributes attributes) {
						ResourceResponse rr = super.newResourceResponse(attributes);
						rr.disableCaching();
						return rr;
					}
					
					@Override
					protected byte[] getData(Attributes attributes) {
						String uri = null;
						if (userId != null) {
							uri = Application.getBean(UserDao.class).get(userId > 0 ? userId : -userId).getPictureuri();
						}
						File img = OmFileHelper.getUserProfilePicture(userId, uri);
						InputStream is = null;
						try {
							is = new FileInputStream(img);
							return IOUtils.toByteArray(is);
						} catch (Exception e) {
						} finally {
							if (is != null) {
								try {
									is.close();
								} catch (IOException e) {}
							}
						}
						return null;
					}
				}));
				item.add(new Label("clientLogin", client.getUsername()));
				item.add(new Label("from", client.getConnectedSince()));
			}
		};
		details.add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));
	}

	void updateRoomDetails(AjaxRequestTarget target) {
		final List<Client> clientsInRoom = Application.getBean(ISessionManager.class).getClientListByRoom(roomId);
		clients.setDefaultModelObject(clientsInRoom);
		Room room = Application.getBean(RoomDao.class).get(roomId);
		roomID.setObject(room.getId());
		roomName.setObject(room.getName());
		roomComment.setObject(room.getComment());
		target.add(clientsContainer, details);
	}
	
	@Override
	protected void onDetach() {
		roomID.detach();
		roomName.detach();
		roomComment.detach();
		super.onDetach();
	}
}
