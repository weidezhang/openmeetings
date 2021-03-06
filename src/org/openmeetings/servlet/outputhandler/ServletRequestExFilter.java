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
package org.openmeetings.servlet.outputhandler;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.openmeetings.servlet.outputhandler.HttpServletRequestEx;

public class ServletRequestExFilter implements Filter {

	private FilterConfig config = null;  

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
		config = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		//We need our filter only to handle download requests, therefore we only
		//need it with GET requests. In addition, it can only work with GET requests
		//in its current condition
		if  ( req.getMethod() == "GET" )
		{
			HttpServletRequestEx requestEx = new HttpServletRequestEx(req, config.getInitParameter("uriEncoding"));
			chain.doFilter(requestEx, response);
		}
		else
		{
			chain.doFilter(req, response);
		}
	}
}

