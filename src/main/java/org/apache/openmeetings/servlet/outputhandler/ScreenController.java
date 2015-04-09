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
package org.apache.openmeetings.servlet.outputhandler;

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_ALLOW_REMOTE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS_SHOW;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.openmeetings.data.basic.FieldManager;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ScreenController {
	private static final Logger log = Red5LoggerFactory.getLogger(ScreenController.class, webAppRootKey);

	@Autowired
	private ISessionManager sessionManager;
	@Autowired
	public SessiondataDao sessiondataDao;
	@Autowired
	public ConfigurationDao cfgDao;
	@Autowired
	public FieldManager fieldManager;
	@Autowired
	public RoomDao roomDao;

	private enum ConnectionType {
		rtmp
		, rtmps
		, rtmpt
	}
	
	private String getLabels(Long language_id, int ... ids) {
		StringBuilder result = new StringBuilder();
		boolean delim = false;
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(fieldManager.getString((long)id, language_id));
			delim = true;
		}
		return result.toString();
	}
	
    @RequestMapping(value = "/screen.upload")
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
		try {
			String sid = request.getParameter("sid");
			Long users_id = sessiondataDao.checkSession(sid);
			String publicSID = request.getParameter("publicSID");
			if (publicSID == null) {
				throw new Exception("publicSID is empty: " + publicSID);
			}
			if (users_id == 0) {
				//checkSession will return 0 in case of invalid session
				throw new Exception("Request from invalid user " + users_id);
			}

			String domain = request.getParameter("domain");
			if (domain == null) {
				throw new Exception("domain is empty: " + domain);
			}

			String languageAsString = request.getParameter("languageAsString");
			if (languageAsString == null) {
				throw new Exception("languageAsString is empty: " + languageAsString);
			}
			Long language_id = Long.parseLong(languageAsString);

			String rtmphostlocal = request.getParameter("rtmphostlocal");
			if (rtmphostlocal == null) {
				throw new Exception("rtmphostlocal is empty: " + rtmphostlocal);
			}
			
			String baseURL = request.getParameter("baseurl");
			if (baseURL == null) {
				throw new Exception("baseurl is empty: " + baseURL);
			}
			log.debug("language_id :: " + language_id);
			String label_sharer = "Sharer";

			try {
				label_sharer = getLabels(language_id
					,  730,  731,  732,  733,  734
					,  735,  737,  738,  739,  740
					,  741,  742,  844,  869,  870
					,  871,  872,  878, 1089, 1090
					, 1091, 1092, 1093, 1465, 1466
					, 1467, 1468, 1469, 1470, 1471
					, 1472, 1473, 1474, 1475, 1476
					, 1477, 1589, 1598, 1078);
			} catch (Exception e) {
				log.error("Error resolving Language labels : ", e);
			}
			log.debug("Creating JNLP Template for TCP solution");

			log.debug("RTMP Sharer labels :: " + label_sharer);

			ConnectionType conType = ConnectionType.valueOf(request.getParameter("connectionType"));

			String port = request.getParameter("port");
			if (port == null) {
				throw new Exception("port is empty: ");
			}
			Client rc = sessionManager.getClientByPublicSID(publicSID, false, null);
			if (rc == null) {
				throw new Exception("Port is empty");
			}
			Long roomId = rc.getRoom_id();
			if (roomId == null) {
				throw new Exception("Client has no room " + rc);
			}
			Room room = roomDao.get(roomId);
			boolean allowRecording = room.isAllowRecording() && rc.isAllowRecording() && (0 == sessionManager.getRecordingCount(roomId));
			boolean allowPublishing = (0 == sessionManager.getPublishingCount(roomId));
			
			Context ctx = new VelocityContext();
			ctx.put("codebase", baseURL + OmFileHelper.SCREENSHARING_DIR);
			ctx.put("APP_NAME", cfgDao.getAppName());
			ctx.put("protocol", conType.name());
			ctx.put("host", rtmphostlocal);
			ctx.put("port", port);
			ctx.put("app", OpenmeetingsVariables.webAppRootKey + "/" + roomId);
			ctx.put("userId", users_id);
			ctx.put("publicSid", publicSID);
			ctx.put("labels", label_sharer);
			ctx.put("defaultQuality", cfgDao.getConfValue(CONFIG_SCREENSHARING_QUALITY, String.class, "1"));
			ctx.put("defaultFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS, String.class, "10"));
			ctx.put("showFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS_SHOW, Boolean.class, "true"));
			ctx.put("allowRemote", cfgDao.getConfValue(CONFIG_SCREENSHARING_ALLOW_REMOTE, Boolean.class, "true"));
			ctx.put("allowRecording", allowRecording);
			ctx.put("allowPublishing", allowPublishing);
			addKeystore(ctx);

			String requestedFile = StringUtils.deleteWhitespace(domain + "_" + roomId) + ".jnlp";
			response.setContentType("application/x-java-jnlp-file");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "Inline; filename=\"" + requestedFile + "\"");

			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, Log4JLogChute.class.getName()); 
			
			ve.mergeTemplate(
				"screenshare.vm"
				, "UTF-8"
				, ctx
				, response.getWriter());
		} catch (Exception er) {
			log.error("[ScreenController]", er);
		}
	}

	private void addKeystore(Context ctx) {
		log.debug("RTMP Sharer Keystore :: start");
		String keystore = "--dummy--", password = "--dummy--";
		FileInputStream fis = null, ris = null;
		try {
			File conf = new File(OmFileHelper.getRootDir(), "conf");

			File keyStore = new File(conf, "keystore.screen");
			if (keyStore.exists()) {
				Properties red5Props = new Properties();
				ris = new FileInputStream(new File(conf, "red5.properties"));
				red5Props.load(ris);
				
				byte keyBytes[] = new byte[(int)keyStore.length()];
				fis = new FileInputStream(keyStore);
				fis.read(keyBytes);
				
				keystore = Hex.encodeHexString(keyBytes);
				password = red5Props.getProperty("rtmps.screen.keystorepass");
				
				/*
				KeyStore ksIn = KeyStore.getInstance(KeyStore.getDefaultType());
				ksIn.load(new FileInputStream(keyStore), red5Props.getProperty("rtmps.keystorepass").toCharArray());
				ByteArrayInputStream bin = new ByteArrayInputStream()
				
				byte fileContent[] = new byte[(int)file.length()];
				sb = addArgument(sb, Object arg)
				ctx.put("$KEYSTORE", users_id);
				/*
				KeyStore ksOut = KeyStore.getInstance(KeyStore.getDefaultType());
				for (Certificate cert : ksIn.getCertificateChain("red5")) {
					PublicKey pub = cert.getPublicKey();
					TrustedCertificateEntry tce = new TrustedCertificateEntry(cert);
					tce.
				}
				*/
			}
		} catch (Exception e) {
			//no op
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// no op
				}
			}
			if (ris != null) {
				try {
					ris.close();
				} catch (IOException e) {
					// no op
				}
			}
		}
		ctx.put("keystore", keystore);
		ctx.put("password", password);
	}
}
