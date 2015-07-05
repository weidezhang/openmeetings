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
package org.apache.openmeetings.web.pages;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addOnlineUser;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.removeOnlineUser;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.apache.openmeetings.web.util.OmUrlFragment.getPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.NavigationDao;
import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.basic.Navimain;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.openmeetings.web.common.menu.MainMenuItem;
import org.apache.openmeetings.web.common.menu.MenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.room.message.RoomMessage;
import org.apache.openmeetings.web.user.AboutDialog;
import org.apache.openmeetings.web.user.ChatPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.urlfragment.UrlFragment;

@AuthorizeInstantiation({"Admin", "Dashboard", "Room"})
public class MainPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPage.class, webAppRootKey);
	private final MenuPanel menu;
	private final WebMarkupContainer topLinks = new WebMarkupContainer("topLinks");
	private final MarkupContainer contents;
	private final AbstractAjaxTimerBehavior areaBehavior;
	private final Component dev;
	private final ChatPanel chat;
	
	public MainPage(PageParameters pp) {
		super();
		getHeader().setVisible(false);
		menu = new MenuPanel("menu", getMainMenu());
		contents = new WebMarkupContainer("contents");
		add(contents.add(new WebMarkupContainer(CHILD_ID)).setOutputMarkupId(true).setMarkupId("contents"));
		add(menu.setVisible(false), topLinks.setVisible(false).setOutputMarkupPlaceholderTag(true).setMarkupId("topLinks"));
		topLinks.add(new AjaxLink<Void>("messages") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_MESSAGES, target);
			}
		});
		topLinks.add(new ConfirmableAjaxLink("logout", 634L) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		topLinks.add(new AjaxLink<Void>("profile") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_EDIT, target);
			}
		});
		final AboutDialog about = new AboutDialog("aboutDialog");
		topLinks.add(new AjaxLink<Void>("about") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				about.open(target);
			}
		});
		add(about);
		if (getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled()) {
		    add((dev = new DebugBar("dev")).setOutputMarkupId(true));
		} else {
		    add((dev = new EmptyPanel("dev")).setOutputMarkupPlaceholderTag(true).setVisible(false));
		}		
		topLinks.add(new ExternalLink("bug", "https://issues.apache.org/jira/browse/OPENMEETINGS"));//FIXME hardcoded
		
		add(chat = new ChatPanel("chatPanel"));
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				addOnlineUser(new Client(message.getSessionId(), message.getKey(), getUserId()));
				log.debug(String.format("WebSocketBehavior::onConnect [session: %s, key: %s]", message.getSessionId(), message.getKey()));
			}
			
			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				Client _c = new Client(message.getSessionId(), message.getKey(), getUserId());
				removeOnlineUser(_c);
				log.debug(String.format("WebSocketBehavior::onClose [session: %s, key: %s]", message.getSessionId(), message.getKey()));
				long roomId = Application.getRoom(_c);
				if (roomId > 0) {
					Application.removeUserFromRoom(roomId, _c);
					RoomPanel.broadcast(new RoomMessage(roomId, _c.getUserId(), RoomMessage.Type.roomExit));
				}
			}
		});
		//load preselected content
		add(areaBehavior = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				OmUrlFragment area = WebSession.get().getArea();
				updateContents(area == null ? OmUrlFragment.get() : area, target);
				stop(target);
				WebSession.get().setArea(null);
			}
		});
	}
	
	private List<MenuItem> getMainMenu() {
		List<MenuItem> menu = new ArrayList<MenuItem>();
		for (Naviglobal gl : getBean(NavigationDao.class).getMainMenu(AuthLevelUtil.hasAdminLevel(WebSession.getRights()))) {
			MenuItem g = new MenuItem(Application.getString(gl.getLabelId())) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(MainPage page, AjaxRequestTarget terget) {}
			};
			List<MenuItem> l = new ArrayList<MenuItem>();
			for (Navimain nm : gl.getMainnavi()) {
				l.add(new MainMenuItem(nm)); 
			}
			if (!l.isEmpty()) {
				g.setChildren(l);
			}
			menu.add(g);
		}
		return menu;
	}
	
	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler) {
		updateContents(f, handler, true);
	}
	
	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler, boolean updateFragment) {
		BasePanel panel = getPanel(f.getArea(), f.getType());
		if (panel != null) {
			Component prev = contents.get(CHILD_ID);
			if (prev != null && prev instanceof BasePanel) {
				((BasePanel)prev).cleanup(handler);
			}
			handler.add(contents.replace(panel));
			if (updateFragment) {
				UrlFragment uf = new UrlFragment(handler);
				uf.set(f.getArea().name(), f.getType());
			}
			panel.onMenuPanelLoad(handler);
		}
		if (dev != null){
			handler.add(dev);
		}
	}
	
	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget target) {
		OmUrlFragment uf = getUrlFragment(params);
		if (uf != null) {
			areaBehavior.stop(target);
			updateContents(uf, target, false);
		}
	}
	
	public MenuPanel getMenu() {
		return menu;
	}

	public WebMarkupContainer getTopLinks() {
		return topLinks;
	}

	public ChatPanel getChat() {
		return chat;
	}
}
