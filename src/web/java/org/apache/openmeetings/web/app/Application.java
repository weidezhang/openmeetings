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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.red5.logging.Red5LoggerFactory.getLogger;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.util.InitializationContainer;
import org.apache.openmeetings.web.pages.ActivatePage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.NotInitedPage;
import org.apache.openmeetings.web.pages.RecordingPage;
import org.apache.openmeetings.web.pages.ResetPage;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.user.dashboard.MyRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.util.AviRecordingResourceReference;
import org.apache.openmeetings.web.util.FlvRecordingResourceReference;
import org.apache.openmeetings.web.util.JpgRecordingResourceReference;
import org.apache.openmeetings.web.util.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.OggRecordingResourceReference;
import org.apache.openmeetings.web.util.UserDashboardPersister;
import org.apache.wicket.Localizer;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.BookmarkableListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.handler.ListenerInterfaceRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.info.PageComponentInfo;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.apache.wicket.util.tester.WicketTester;
import org.slf4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import ro.fortsoft.wicket.dashboard.WidgetRegistry;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;
import ro.fortsoft.wicket.dashboard.web.DashboardContextInjector;
import ro.fortsoft.wicket.dashboard.web.DashboardSettings;

public class Application extends AuthenticatedWebApplication {
	private static final Logger log = getLogger(Application.class, webAppRootKey);
	private static boolean isInstalled;
	private static Map<Long, Set<String>> ONLINE_USERS = new ConcurrentHashMap<Long, Set<String>>();
	private DashboardContext dashboardContext;
	private static Set<Long> STRINGS_WITH_APP = new HashSet<Long>(); //FIXME need to be removed
	private static String appName;
	static {
		STRINGS_WITH_APP.addAll(Arrays.asList(499L, 500L, 506L, 511L, 512L, 513L, 517L, 532L, 622L, 804L
				, 909L, 952L, 978L, 981L, 984L, 989L, 990L, 999L, 1151L, 1155L, 1157L, 1158L, 1194L));
	}
	
	@Override
	protected void init() {
		appName = super.getName();
		getSecuritySettings().setAuthenticationStrategy(new OmAuthenticationStrategy());
		
		//Add custom resource loader at the beginning, so it will be checked first in the 
		//chain of Resource Loaders, if not found it will search in Wicket's internal 
		//Resource Loader for a the property key
		getResourceSettings().getStringResourceLoaders().add(0, new LabelResourceLoader());
		
		super.init();
		
		// register some widgets
		dashboardContext = new DashboardContext();
		dashboardContext.setDashboardPersister(new UserDashboardPersister());
		WidgetRegistry widgetRegistry = dashboardContext.getWidgetRegistry();
		widgetRegistry.registerWidget(new MyRoomsWidgetDescriptor());
		widgetRegistry.registerWidget(new WelcomeWidgetDescriptor());
		widgetRegistry.registerWidget(new StartWidgetDescriptor());
		widgetRegistry.registerWidget(new RssWidgetDescriptor());
		// add dashboard context injector
		getComponentInstantiationListeners().add(new DashboardContextInjector(dashboardContext));
		DashboardSettings dashboardSettings = DashboardSettings.get();
		dashboardSettings.setIncludeJQuery(false);
		dashboardSettings.setIncludeJQueryUI(false);
		
		getRootRequestMapperAsCompound().add(new NoVersionMapper(getHomePage()));
		getRootRequestMapperAsCompound().add(new NoVersionMapper("notinited", NotInitedPage.class));
		mountPage("swf", SwfPage.class);
		mountPage("install", InstallWizardPage.class);
		mountPage("signin", getSignInPageClass());
		mountPage("activate", ActivatePage.class);
		mountPage("reset", ResetPage.class);
		mountPage("/recording/${hash}", RecordingPage.class);
		mountResource("/recordings/avi/${id}", new AviRecordingResourceReference());
		mountResource("/recordings/flv/${id}", new FlvRecordingResourceReference());
		mountResource("/recordings/mp4/${id}", new Mp4RecordingResourceReference());
		mountResource("/recordings/ogg/${id}", new OggRecordingResourceReference());
		mountResource("/recordings/jpg/${id}", new JpgRecordingResourceReference()); //should be in sync with VideoPlayer
	}

	private static class NoVersionMapper extends MountedMapper {
		public NoVersionMapper(final Class<? extends IRequestablePage> pageClass) {
			this("/", pageClass);
		}
		
		public NoVersionMapper(String mountPath, final Class<? extends IRequestablePage> pageClass) {
			super(mountPath, pageClass, new PageParametersEncoder());
		}

		@Override
		protected void encodePageComponentInfo(Url url, PageComponentInfo info) {
			//Does nothing
		}
		
		@Override
		public Url mapHandler(IRequestHandler requestHandler) {
			if (requestHandler instanceof ListenerInterfaceRequestHandler || requestHandler instanceof BookmarkableListenerInterfaceRequestHandler) {
				return null;
			} else {
				return super.mapHandler(requestHandler);
			}
		}
	}

	public static OmAuthenticationStrategy getAuthenticationStrategy() {
		return (OmAuthenticationStrategy)get().getSecuritySettings().getAuthenticationStrategy();
	}
	
	@Override
	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return WebSession.class;
	}

	@Override
	public Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
	
	public static Application get() {
		return (Application) WebApplication.get();
	}
	
	public static DashboardContext getDashboardContext() {
		return get().dashboardContext;
	}
	
	public static void addOnlineUser(long userId, String sessionId) {
		if (!ONLINE_USERS.containsKey(userId)) {
			ONLINE_USERS.put(userId, new ConcurrentHashSet<String>());
		}
		ONLINE_USERS.get(userId).add(sessionId);
	}
	
	public static void removeOnlineUser(long userId, String sessionId) {
		if (ONLINE_USERS.containsKey(userId)) {
			Set<String> sessions = ONLINE_USERS.get(userId);
			if (sessions.isEmpty()) {
				ONLINE_USERS.remove(userId);
			} else if (sessions.contains(sessionId)) {
				if (sessions.size() > 1) {
					sessions.remove(sessionId);
				} else {
					ONLINE_USERS.remove(userId);
				}
			}
		}
	}
	
	public static boolean isUserOnline(long userId) {
		return ONLINE_USERS.containsKey(userId);
	}
	
	//TODO need more safe way FIXME
	public <T> T _getBean(Class<T> clazz) {
		WebApplicationContext wac = getWebApplicationContext(getServletContext());
		return wac == null ? null : wac.getBean(clazz);
	}

	public static String getString(long id) {
		return getString(id, WebSession.getLanguage());
	}

	public static String getString(long id, final long languageId) {
		Locale loc = LabelDao.languages.get(languageId);
		if (loc == null) {
			loc = WebSession.exists() ? WebSession.get().getLocale() : Locale.ENGLISH;
		}
		return getString(id, loc);
	}
	
	public static String getString(long id, final Locale loc) {
		return getString(id, loc, false);
	}
	
	public static String getString(long id, final Locale loc, boolean noReplace) {
		if (!exists()) {
			ThreadContext.setApplication(Application.get(appName));
		}
		Localizer l = get().getResourceSettings().getLocalizer();
		String value = l.getStringIgnoreSettings("" + id, null, null, loc, null, "[Missing]");
		if (!noReplace && STRINGS_WITH_APP.contains(id)) {
			final MessageFormat format = new MessageFormat(value, loc);
			value = format.format(new Object[]{getBean(ConfigurationDao.class).getAppName()});
		}
		if (!noReplace && RuntimeConfigurationType.DEVELOPMENT == get().getConfigurationType()) {
			value += String.format(" [%s]", id);
		}
		return value;
	}

	public static boolean isInstalled() {
		boolean result = isInstalled;
		if (!isInstalled) {
			if (InitializationContainer.initComplete) {
				//TODO can also check crypt class here
				isInstalled = result = get()._getBean(UserDao.class).count() > 0;
			}
		}
		return result;
	}
	
	public static <T> T getBean(Class<T> clazz) {
		if (InitializationContainer.initComplete) {
			if (!isInstalled()) {
				throw new RestartResponseException(InstallWizardPage.class);
			}
			return get()._getBean(clazz);
		} else {
			throw new RestartResponseException(NotInitedPage.class);
		}
	}
	
	public static WicketTester getWicketTester() {
		return getWicketTester(-1);
	}
	
	public static WicketTester getWicketTester(long langId) {
		Application app = new Application();
        
		WicketTester tester = new WicketTester(app);
		ServletContext sc = app.getServletContext();
        XmlWebApplicationContext xmlContext = new XmlWebApplicationContext();
        xmlContext.setConfigLocation("classpath:openmeetings-applicationContext.xml");
        xmlContext.setServletContext(sc);
        xmlContext.refresh();
        sc.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, xmlContext);
        if (langId > 0) {
        	WebSession.get().setLanguage(langId);
        }
        InitializationContainer.initComplete = true;
        return tester;
	}
	
	public static void destroy(WicketTester tester) {
		if (tester != null) {
			ServletContext sc = tester.getServletContext();
			try {
				((XmlWebApplicationContext)sc.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).close();
			} catch (Exception e) {
				log.error("Unexpected error while destroying XmlWebApplicationContext", e);
			}
			tester.destroy();
		}
	}
	
	public static String getAppName() {
		return appName;
	}
}
