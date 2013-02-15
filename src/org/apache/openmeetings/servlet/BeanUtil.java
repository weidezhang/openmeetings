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
package org.apache.openmeetings.servlet;

import javax.servlet.ServletContext;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BeanUtil {
	
	private static final Logger log = Red5LoggerFactory.getLogger(
			BeanUtil.class, OpenmeetingsVariables.webAppRootKey);
	
	public <T> T getBean(Class<T> beanClass, ServletContext ctx) {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				return WebApplicationContextUtils.getWebApplicationContext(
						ctx).getBean(beanClass);
			}
		} catch (Exception err) {
			log.error("[getBean]", err);
		}
		return null;
	}

}