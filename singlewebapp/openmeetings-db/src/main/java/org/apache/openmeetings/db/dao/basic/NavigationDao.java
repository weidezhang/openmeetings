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
package org.apache.openmeetings.db.dao.basic;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.basic.Navimain;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NavigationDao {
	private static final Logger log = Red5LoggerFactory.getLogger(NavigationDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Naviglobal getGlobalMenuEntry(long globalId) {
		try {
			TypedQuery<Naviglobal> query = em.createNamedQuery("getNavigationById", Naviglobal.class);
			query.setParameter("id", globalId);
			return query.getSingleResult();
		} catch (Exception ex2) {
			log.error("getGlobalMenuEntry", ex2);
		}
		return null;

	}

	public List<Naviglobal> getMainMenu(boolean admin, long USER_ID) {
		return em.createNamedQuery("getNavigation", Naviglobal.class)
				.setParameter("level_id", admin ? 3L : 1L)
				.getResultList();
	}

	public void addGlobalStructure(String action, int naviorder, long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, boolean deleted, Long tooltip_fieldvalues_id) {
		Naviglobal ng = new Naviglobal();
		ng.setAction(action);
		ng.setComment("");
		ng.setIcon("");
		ng.setNaviorder(naviorder);
		ng.setFieldvalues_id(fieldvalues_id);
		ng.setIsleaf(isleaf);
		ng.setIsopen(isopen);
		ng.setDeleted(deleted);
		ng.setLevel_id(level_id);
		ng.setName(name);
		ng.setStarttime(new Date());
		ng.setTooltip_fieldvalues_id(tooltip_fieldvalues_id);
		// CriteriaBuilder crit = em.getCriteriaBuilder();

		em.persist(ng);
	}

	public void addMainStructure(String action, String params, int naviorder, long fieldvalues_id, boolean isleaf, boolean isopen,
			long level_id, String name, long global_id, boolean deleted, Long tooltip_fieldvalues_id) {
		Naviglobal ng = getGlobalMenuEntry(global_id);
		List<Navimain> mainEntries = ng.getMainnavi();
		mainEntries = (mainEntries == null) ? new ArrayList<Navimain>() : mainEntries;

		Navimain nm = new Navimain();
		nm.setAction(action);
		nm.setParams(params);
		nm.setComment("");
		nm.setIcon("");
		nm.setFieldvalues_id(fieldvalues_id);
		nm.setIsleaf(isleaf);
		nm.setNaviorder(naviorder);
		nm.setIsopen(isopen);
		nm.setLevel_id(level_id);
		nm.setName(name);
		nm.setDeleted(deleted);
		nm.setGlobal_id(global_id);
		nm.setStarttime(new Date());
		nm.setTooltip_fieldvalues_id(tooltip_fieldvalues_id);

		mainEntries.add(nm);
		ng.setMainnavi(mainEntries);

		em.persist(ng);
	}

}
