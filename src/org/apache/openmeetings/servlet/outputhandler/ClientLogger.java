package org.apache.openmeetings.servlet.outputhandler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.servlet.BaseHttpServlet;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ClientLogger extends BaseHttpServlet {

	private static final long serialVersionUID = 6281903903868569791L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			ClientLogger.class, OpenmeetingsVariables.webAppRootKey);

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/xml");
		resp.setStatus(HttpServletResponse.SC_OK);
		String message = req.getParameter("message");
		if (message != null) {
			log.debug(message);
		}
	}

}
