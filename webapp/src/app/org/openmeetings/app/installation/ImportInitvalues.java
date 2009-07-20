package org.openmeetings.app.installation;

import java.util.List;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.dom4j.io.SAXReader;
import org.dom4j.Element;
import org.dom4j.Document;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.ErrorManagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.data.calendar.management.AppointmentCategoryLogic;
import org.openmeetings.app.data.calendar.management.AppointmentLogic;
import org.openmeetings.app.data.calendar.management.AppointmentRemindertypeLogic;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;

public class ImportInitvalues {

	private static final Logger log = Red5LoggerFactory.getLogger(ImportInitvalues.class, "openmeetings");

	public static final String languageFolderName = "languages/";
 
	private static final String nameOfLanguageFile = "languages.xml";

	private static final String nameOfCountriesFile = "countries.xml";

	private static final String nameOfErrorFile = "errorvalues.xml";

	private static ImportInitvalues instance;

	private ImportInitvalues() {
	}

	public static synchronized ImportInitvalues getInstance() {
		if (instance == null) {
			instance = new ImportInitvalues();
		}
		return instance;
	}

	public void loadMainMenu() {

		Usermanagement.getInstance().addUserLevel("User", 1);
		Usermanagement.getInstance().addUserLevel("Moderator", 2);
		Usermanagement.getInstance().addUserLevel("Admin", 3);

		/* ########################
		 * Dashboard Menu Points
		 */
		Navimanagement.getInstance().addGlobalStructure("home", 1, 124, false,
				true, 1, "home", "false", 582L);
		Navimanagement.getInstance().addMainStructure("dashboardModuleStartScreen", 1, 290,
				true, false, 1, "Dashboard Module", 1, "false");
		Navimanagement.getInstance().addMainStructure("dashboardModuleCalendar", 2,
				291, true, false, 1, "Module Calendar", 1, "false");

		/* ########################
		 * Recording Menu Points - disabled by default
		 */
		
		Navimanagement.getInstance().addGlobalStructure("record", 2, 395,
				false, true, 1, "record", "true", 583L);
		Navimanagement.getInstance().addMainStructure("recordModule", 1, 395,
				true, false, 1, "Module Recording Player", 2, "false");
		
		/*
		String action, int naviorder,
		long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
		String name, long global_id
		*/
		
		/* ########################
		 * Conference Menu Points
		 */
		
		Navimanagement.getInstance().addGlobalStructure("meetings", 3, 2,
				false, true, 1, "List of Conference Rooms Global Navi", "false", 584L);
		
		Navimanagement.getInstance().addMainStructure("conferenceModuleRoomList", 1,
				2, true, false, 1, "List of Conference Rooms Main Navi", 3, "false");
		
		
		/* ########################
		 * Event Menu Points
		 */

		Navimanagement.getInstance().addGlobalStructure("events", 4, 3, false,
				true, 1, "List of Event Rooms Global Navi", "false", 585L);
		
		Navimanagement.getInstance().addMainStructure("eventModuleRoomList", 1, 3,
				true, false, 1, "List of Event Rooms Main Navi", 4, "false");
		
		
		
		/* ########################
		 * Moderation Menu Points - disabled by default
		 */
		Navimanagement.getInstance().addGlobalStructure("moderate", 5, 649L, false,
				true, 2, "Moderation Menu", "true", 660L);
		
		Navimanagement.getInstance().addMainStructure("moderatorModuleUser", 1, 650,
				true, false, 1, "Moderate Users", 5, "false");
		
		Navimanagement.getInstance().addMainStructure("moderatorModuleRoom", 1, 651,
				true, false, 1, "Moderate Rooms", 5, "false");
		
		
		/* ########################
		 * Administration Menu Points
		 */

		Navimanagement.getInstance().addGlobalStructure("admin", 6, 6, false,
				true, 2, "Administration Menu", "false", 586L);
		
		Navimanagement.getInstance().addMainStructure("adminModuleUser", 14, 125,
				true, false, 2, "Administration of Users", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleConnections", 15, 597,
				true, false, 3, "Aministration of Connections", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleOrg", 16, 127,
				true, false, 3, "Administration of Organizations", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleRoom", 17, 186,
				true, false, 3, "Administration of Rooms", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleConfiguration", 18, 263,
				true, false, 3, "Administration of Configuration", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleLanguages", 19,
				348, true, false, 3, "Administration of Languages", 6, "false");
		
		Navimanagement.getInstance().addMainStructure("adminModuleBackup", 20, 367,
				true, false, 3, "Administration of Backups", 6, "false");

		ErrorManagement.getInstance().addErrorType(new Long(1), new Long(322));
		ErrorManagement.getInstance().addErrorType(new Long(2), new Long(323));

	}

	public void loadErrorMappingsFromXML(String filePath) throws Exception {

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath
				+ ImportInitvalues.nameOfErrorFile);

		Element root = document.getRootElement();

		for (Iterator it = root.elementIterator("row"); it.hasNext();) {

			Element row = (Element) it.next();

			Long errorvalues_id = null;
			Long fieldvalues_id = null;
			Long errortype_id = null;

			for (Iterator itSub = row.elementIterator("field"); itSub.hasNext();) {

				Element field = (Element) itSub.next();

				String name = field.attributeValue("name");
				String text = field.getText();
				//System.out.println("NAME | TEXT "+name+" | "+text);
				if (name.equals("errorvalues_id"))
					errorvalues_id = Long.valueOf(text).longValue();
				if (name.equals("fieldvalues_id"))
					fieldvalues_id = Long.valueOf(text).longValue();
				if (name.equals("errortype_id"))
					errortype_id = Long.valueOf(text).longValue();
			}

			ErrorManagement.getInstance().addErrorValues(errorvalues_id,
					errortype_id, fieldvalues_id);
		}
		log.error("ErrorMappings ADDED");
	}

	public void loadSalutations() {

		Salutationmanagement.getInstance().addUserSalutation("Mister", 261);
		Salutationmanagement.getInstance().addUserSalutation("Miss", 262);

	}

	public void loadConfiguration(String crypt_ClassName,
			String allowfrontendRegister, String smtpServer, String smtpPort,
			String referer, String mailauthname, String mailauthpass,
			String default_lang_id, String swf_path, String im_path,
			String url_feed, String url_feed2,
			String sendEmailAtRegister, String sendEmailWithVerficationCode,
			String default_export_font, String ldap_auth_path, String screen_viewer) {

		Configurationmanagement
				.getInstance()
				.addConfByKey(
						3,
						"crypt_ClassName",
						crypt_ClassName,
						null,
						"This Class is used for Authentification-Crypting. Be carefull what you do here! If you change it while running previous Pass of users will not be workign anymore! for more Information see http://code.google.com/p/openmeetings/wiki/CustomCryptMechanism");
		//"1"
		Configurationmanagement.getInstance().addConfByKey(3,
				"ldap_config_path", ldap_auth_path, null, "Absolute Path to a Ldap Configration File(see example config)");
		
		Configurationmanagement.getInstance().addConfByKey(3,
				"screen_viewer", screen_viewer, null, "ScreenViewer Type(0==standard, 1== jrdesktop)");
		
		Configurationmanagement.getInstance().addConfByKey(3,
				"allow_frontend_register", allowfrontendRegister, null, "");

		Configurationmanagement.getInstance().addConfByKey(3,
				"default_group_id", "1", null, "");

		//this domain_id is the Organisation of users who register through the frontend
		Configurationmanagement.getInstance().addConfByKey(3,
				"default_domain_id", "1", null, "");

		//"smtp.xmlcrm.org"
		Configurationmanagement.getInstance().addConfByKey(3, "smtp_server",
				smtpServer, null, "this is the smtp server to send messages");
		//25
		Configurationmanagement.getInstance().addConfByKey(3, "smtp_port",
				smtpPort, null, "this is the smtp server port normally 25");
		//"openmeetings@xmlcrm.org"
		Configurationmanagement.getInstance().addConfByKey(3,
				"system_email_addr", referer, null,
				"all send EMails by the system will have this address");
		//"openmeetings@xmlcrm.org"
		Configurationmanagement.getInstance().addConfByKey(3, "email_username",
				mailauthname, null, "System auth email username");
		//
		Configurationmanagement.getInstance().addConfByKey(3, "email_userpass",
				mailauthpass, null, "System auth email password");
		//"EN"
		Configurationmanagement.getInstance().addConfByKey(3, "default_lang_id",
				default_lang_id, null, "Default System Language ID see language.xml");

		Configurationmanagement.getInstance().addConfByKey(3, "swftools_path",
				swf_path, null, "Path To SWF-Tools");

		Configurationmanagement.getInstance().addConfByKey(3,
				"imagemagick_path", im_path, null, "Path to ImageMagick tools");

		Configurationmanagement.getInstance().addConfByKey(3, "rss_feed1",
				url_feed, null, "Feed URL");

		Configurationmanagement.getInstance().addConfByKey(3, "rss_feed2",
				url_feed2, null, "Feed URL 2");
		
		Configurationmanagement.getInstance().addConfByKey(3, "sendEmailAtRegister",
				sendEmailAtRegister, null, "User get a EMail with their Account data. Values: 0(No) or 1(Yes)");
		
		Configurationmanagement.getInstance().addConfByKey(3, "sendEmailWithVerficationCode",
				sendEmailWithVerficationCode, null, "User must activate their account by clicking on the " +
						"activation-link in the registering Email. Values: 0(No) or 1(Yes) " +
						"It makes no sense to make this(sendEmailWithVerficationCode) 1(Yes) while " +
						"sendEmailAtRegister is 0(No) cause you need" +
						"to send a EMail.");
		Configurationmanagement.getInstance().addConfByKey(3, "default_export_font",
				default_export_font, null, "The Name of the Font used for exporting/render Images from Whiteboard" +
						"The Font has to exist on the Server which runs Red5");
		
	}

	public void loadDefaultRooms() {

		long conference_Id = Roommanagement.getInstance().addRoomType(
				"conference");
		log.debug("conference_Id: " + conference_Id);
		long audience_Id = Roommanagement.getInstance().addRoomType("audience");
		log.debug("audience_Id: " + audience_Id);

		Roommanagement.getInstance().addRoom(3, "public Conference Room", 1,
				"", new Long(8), true, null, false, false, null, false);

		Roommanagement.getInstance().addRoom(3, "public Video Only Room", 1,
				"", new Long(16), true, null, false, false, null, false);

		Roommanagement.getInstance().addRoom(3,
				"public Video And Whiteboard Room", 1, "", new Long(16), true,
				null, false, false, null, false);

		long room2 = Roommanagement.getInstance().addRoom(3,
				"private Conference Room", 1, "", new Long(16), false, null,
				false, false, null, false);
		Roommanagement.getInstance().addRoomToOrganisation(3, room2, 1);

		Roommanagement.getInstance().addRoom(3, 
				"public Audience Room", 2, "", new Long(32), true, null, 
				false, false, null, false);

		long room4 = Roommanagement.getInstance().addRoom(3,
				"private Audience Room", 2, "", new Long(32), false, null, 
				false, false, null, false);
		
		Roommanagement.getInstance().addRoomToOrganisation(3, room4, 1);

	}

	public void loadInitUserAndOrganisation(String username, String userpass,
			String email, String defaultOrganisationName) {
		//Add user
		try {
			Long user_id = Usermanagement.getInstance().registerUserInit(
					new Long(3), 3, 1, 1, username, userpass, "lastname",
					"firstname", email, new java.util.Date(), "street", "no",
					"fax", "zip", 1, "town", 0, false, null, "phone");

			//Add default group
			Long organisation_id = Organisationmanagement.getInstance()
					.addOrganisation(defaultOrganisationName, user_id);

			//Add user to default group
			Organisationmanagement.getInstance().addUserToOrganisation(
					new Long(1), organisation_id, null, "");
		} catch (Exception e) {
			log.error("[loadInitUserAndOrganisation] ", e);
		}
	}

	/**
	 * import all language Names from the xml file
	 * @param filePath
	 * @throws Exception
	 */
	private void loadCountriesFiles(String filePath) throws Exception {

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath
				+ ImportInitvalues.nameOfCountriesFile);

		Element root = document.getRootElement();

		for (Iterator it = root.elementIterator("country"); it.hasNext();) {

			Element item = (Element) it.next();
			String country = item.attributeValue("name");

			Statemanagement.getInstance().addState(country);

		}
	}

	/**
	 * load all availible languages File names and language name's from the config file
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<Integer,LinkedHashMap<String,Object>> getLanguageFiles(String filePath) throws Exception {

		LinkedHashMap<Integer,LinkedHashMap<String,Object>> languages = new LinkedHashMap<Integer,LinkedHashMap<String,Object>>();

		SAXReader reader = new SAXReader();
		Document document = reader.read(filePath
				+ ImportInitvalues.nameOfLanguageFile);

		Element root = document.getRootElement();

		for (Iterator<Element> it = root.elementIterator("lang"); it.hasNext();) {

			Element item = it.next();
			String country = item.getText();
			Integer id = Integer.valueOf(item.attribute("id").getValue()).intValue();
			Boolean rtl = Boolean.valueOf(item.attribute("rightToLeft").getValue()).booleanValue();

			LinkedHashMap<String,Object> lang = new LinkedHashMap<String,Object>();
			lang.put("id", id);
			lang.put("name", country);
			lang.put("rtl", rtl);
			//log.error("getLanguageFiles "+country);
			languages.put(id,lang);

		}
		log.debug("Countries ADDED ");
		return languages;

	}
	
	
	/**
	 * @author o.becherer
	 * initial fillment of Appointmentcategories
	 */
	//------------------------------------------------------------------------------
	public void loadInitAppointmentCategories(){
		log.debug("ImportInitValues.loadInitAppointmentCategories");
		
		try{
			AppointmentCategoryLogic.getInstance().createAppointmentCategory("default", "default", new Long(-1));
		}catch(Exception e){
			log.error("Could not create AppointMentcategories");
			return;
		}
		
		
	}
	//------------------------------------------------------------------------------
	
	
	/**
	 * @author o.becherer
	 * initial fillment of AppointMentReminderTypes
	 */
	//------------------------------------------------------------------------------
	public void loadInitAppointmentReminderTypes(){
		
		log.debug("ImportInitValues.loadInitAppointmentReminderTypes");
		
		try{
			AppointmentRemindertypeLogic.getInstance().createAppointMentReminderType(-1L, "none", "no reminder");
			AppointmentRemindertypeLogic.getInstance().createAppointMentReminderType(-1L, "simple email", "simple email notification");
			AppointmentRemindertypeLogic.getInstance().createAppointMentReminderType(-1L, "iCal email", "Ical notification");
			
		}catch(Exception e){
			log.error("Could not create ReminderType");
			return;
		}
	}
	//------------------------------------------------------------------------------
	
	public void loadInitLanguages(String filePath) throws Exception {

		this.loadCountriesFiles(filePath);

		//String listLanguages[] = {"deutsch", "english", "french", "spanish"};

		LinkedHashMap<Integer,LinkedHashMap<String,Object>> listlanguages = this.getLanguageFiles(filePath);

		// TODO empty tables before launch
		//Languagemanagement.getInstance().emptyFieldLanguage();

		boolean langFieldIdIsInited = false;

		/** Read all languages files */
		for (Iterator<Integer> itLang = listlanguages.keySet().iterator(); itLang
				.hasNext();) {
			Integer langId = itLang.next();
			LinkedHashMap<String,Object> lang = listlanguages.get(langId);
			log.debug("loadInitLanguages lang: " + lang);

			String langName = (String) lang.get("name");
			Boolean langRtl = (Boolean) lang.get("rtl");
			Long languages_id = FieldLanguageDaoImpl.getInstance().addLanguage(langName,langRtl);

			SAXReader reader = new SAXReader();
			Document document = reader.read(filePath + langName + ".xml");

			Element root = document.getRootElement();

			for (Iterator it = root.elementIterator("string"); it.hasNext();) {
				Element item = (Element) it.next();
				//log.error(item.getName());

				Long id = Long.valueOf(item.attributeValue("id")).longValue();
				String name = item.attributeValue("name");
				String value = "";

				for (Iterator t2 = item.elementIterator("value"); t2.hasNext();) {
					Element val = (Element) t2.next();
					value = val.getText();
				}

				//log.error("result: "+langFieldIdIsInited+" "+id+" "+name+" "+value);

				//Only do that for the first field-set
				if (!langFieldIdIsInited) {
					Fieldmanagment.getInstance().addField(name);
				}
				
				Fieldmanagment.getInstance().addFieldValueByFieldAndLanguage(
						id, languages_id, value);

			}
			log.debug("Lang ADDED: " + lang);
			if (!langFieldIdIsInited)
				langFieldIdIsInited = true;
		}

	}

}
