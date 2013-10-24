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

import static java.text.DateFormat.SHORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_MYROOMS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAUT_LANG_KEY;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getDashboardContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.dao.user.ILdapLoginManagement;
import org.apache.openmeetings.db.dao.user.IUserManager;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.TimezoneUtil;
import org.apache.openmeetings.util.AuthLevelUtil;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.user.dashboard.PrivateRoomsWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.RssWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.StartWidgetDescriptor;
import org.apache.openmeetings.web.user.dashboard.WelcomeWidgetDescriptor;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;

import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.DefaultDashboard;
import ro.fortsoft.wicket.dashboard.WidgetFactory;
import ro.fortsoft.wicket.dashboard.web.DashboardContext;

public class WebSession extends AbstractAuthenticatedWebSession {
	private static final long serialVersionUID = 1123393236459095315L;
	public static int MILLIS_IN_MINUTE = 60000;
	//private static final Map<String, Locale> LNG_TO_LOCALE_MAP = new HashMap<String, Locale> ();
	private long userId = -1;
	private long userLevel = -1; //TODO renew somehow on user edit !!!!
	private long languageId = -1; //TODO renew somehow on user edit !!!!
	private String SID = null;
	private OmUrlFragment area = null;
	private TimeZone tz;
	private SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private DateFormat sdf;
	private Dashboard dashboard;
	private String baseUrl = null;
	private Locale browserLocale = null;
	private int browserTZOffset = Integer.MIN_VALUE;
	
	public WebSession(Request request) {
		super(request);
		browserLocale = getLocale();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		userId = -1;
		userLevel = -1;
		SID = null;
		sdf = null;
	}
	
	@Override
	public Roles getRoles() {
		Roles r = null;
		if (isSignedIn()) {
			r = new Roles(Roles.USER);
			if (AuthLevelUtil.checkAdminLevel(userLevel)) {
				r.add(Roles.ADMIN);
			}
		} else {
			try {
				IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
				StringValue secureHash = params.getParameterValue("secureHash");
				if (!secureHash.isEmpty()) {
					PageParameters pp = new PageParameters();
					for (String p : params.getParameterNames()) {
						pp.add(p, params.getParameterValues(p));
					}
					throw new RestartResponseAtInterceptPageException(SwfPage.class, pp);
				}
			} catch (RestartResponseAtInterceptPageException e) {
				throw e;
			} catch (Exception e) {
				//no-op, will continue to sign-in page
			}
		}
		return r;
	}

	@Override
	public boolean isSignedIn() {
		if (userId < 1) {
			IAuthenticationStrategy strategy = getAuthenticationStrategy();
			// get username and password from persistence store
			String[] data = strategy.load();
			if ((data != null) && (data.length > 2)) {
				// try to sign in the user
				if (!signIn(data[0], data[1], data[2])) {
					// the loaded credentials are wrong. erase them.
					strategy.remove();
				}
			}
		}
		return userId > -1;
	}

	public boolean signIn(String login, String password, String ldapConfigFileName) {
		Sessiondata sessData = getBean(SessiondataDao.class).startsession();
		SID = sessData.getSession_id();
		Object u = Strings.isEmpty(ldapConfigFileName)
				? getBean(IUserManager.class).loginUser(SID, login, password, null, null, false)
				: getBean(ILdapLoginManagement.class).doLdapLogin(login, password, null, null, SID, ldapConfigFileName);
		
		if (u instanceof User) {
			User user = (User)u;
			userId = user.getUser_id();
			userLevel = user.getLevel_id();
			languageId = user.getLanguage_id();
			tz = getBean(TimezoneUtil.class).getTimeZone(user);
			ISO8601FORMAT.setTimeZone(tz);
			//FIXMW locale need to be set by User language first
			sdf = DateFormat.getDateTimeInstance(SHORT, SHORT, getLocale());
			if (null == getId()) {
				bind();
			}
			return true;
		}
		return false;
	}
	
	public static WebSession get() {
		return (WebSession)AbstractAuthenticatedWebSession.get();
	}
	
	public static String getString(long id) {
		String s = getBean(FieldLanguagesValuesDao.class).getString(id, getLanguage());
		return s == null ? "[Missing]" : s;
	}
	
	void setLanguage(long languageId) {
		this.languageId = languageId;
	}
	
	public static long getLanguage() {
		WebSession session = get();
		if (session.languageId < 0) {
			if (session.isSignedIn()) {
				session.languageId = getBean(UserDao.class).get(session.userId).getLanguage_id();
			} else {
				session.languageId = getBean(ConfigurationDao.class).getConfValue(CONFIG_DEFAUT_LANG_KEY, Long.class, "1");
			}
		}
		return session.languageId;
	}
	
	public static FieldLanguage getLanguageObj() {
		return getBean(FieldLanguageDao.class).getFieldLanguageById(getLanguage());
	}
	
	public static String getSid() {
		return get().SID;
	}

	public static long getUserId() {
		return get().userId;
	}
	
	public static TimeZone getUserTimeZone() {
		return get().tz;
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance(get().tz);
	}

	public static DateFormat getIsoDateFormat() {
		return get().ISO8601FORMAT;
	}
	
	public static DateFormat getDateFormat() {
		return get().sdf;
	}
	
	public static long getUserLevel() {
		return get().userLevel;
	}

	public OmUrlFragment getArea() {
		return area;
	}

	public void setArea(OmUrlFragment area) {
		this.area = area;
	}

	
	public static Dashboard getDashboard() {
		Dashboard d = get().dashboard;
		if (d == null) {
			get().initDashboard();
			d = get().dashboard;
		}
		return d;
	}
	
	public static String getBaseUrl() {
		return get().baseUrl;
	}
	
	public void setBaseUrl(String baseUrl){
		this.baseUrl = baseUrl;
	}
	
	public Locale getBrowserLocale(){
		return browserLocale;
	}

	public void setBrowserTimeZoneOffset(int browserTZOffset){
		this.browserTZOffset = browserTZOffset;
	}
	
	public int getBrowserTimeZoneOffset(){
		return browserTZOffset;
	}
	
	public FieldLanguage getLanguageByBrowserLocale() {
		List<FieldLanguage> languages = getBean(FieldLanguageDao.class).getLanguages();
		for (FieldLanguage l : languages) {
			if (getBrowserLocale().getLanguage().equals(new Locale(l.getCode()).getLanguage())){
				return l;
			}
		}
		return languages.get(0);
	}

	public State getCountryByBrowserLocale() {
		List<State> states = getBean(StateDao.class).getStates();
		String code = getBrowserLocale().getISO3Country().toUpperCase();
		for (State s : states) {
			if (s.getShortName().toUpperCase().equals(code)){
				return s;
			}
		}
		return states.get(0);
	}

	public static List<String> getAvailableTimezones() {
		return Arrays.asList(TimeZone.getAvailableIDs());
	}
	
	private boolean checkTimezone(TimeZone tz, int offsetByMinutes) {
		return offsetByMinutes == (tz.getRawOffset() / MILLIS_IN_MINUTE);
	}
	
	public String getTimeZoneByBrowserLocale(int offsetByMinutes){
		TimeZone tz = Calendar.getInstance(getBrowserLocale()).getTimeZone();
		if (!checkTimezone(tz, offsetByMinutes)){
			String[] tzIds = TimeZone.getAvailableIDs(MILLIS_IN_MINUTE * offsetByMinutes);
			if (tzIds != null && tzIds.length > 0) {
				return tzIds[0];
			}
		}
		return tz.getID();
	}
	
	private void initDashboard() {
		DashboardContext dashboardContext = getDashboardContext();
		//FIXME check title etc.
		dashboard = dashboardContext.getDashboardPersiter().load();
		if (dashboard == null) {
			dashboard = new DefaultDashboard("default", "Default");
			
			WidgetFactory widgetFactory = dashboardContext.getWidgetFactory();
			dashboard.addWidget(widgetFactory.createWidget(new WelcomeWidgetDescriptor()));
			dashboard.addWidget(widgetFactory.createWidget(new StartWidgetDescriptor()));
			ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
			if ("1".equals(cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_MYROOMS_KEY, Integer.class, "0"))) {
				dashboard.addWidget(widgetFactory.createWidget(new PrivateRoomsWidgetDescriptor()));
			}
			if ("1".equals(cfgDao.getConfValue(CONFIG_DASHBOARD_SHOW_RSS_KEY, Integer.class, "0"))) {
				dashboard.addWidget(widgetFactory.createWidget(new RssWidgetDescriptor()));
			}
			dashboardContext.getDashboardPersiter().save(dashboard);
		}
	}
}