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
package org.apache.openmeetings.db.dao.user;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.util.DaoHelper;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrganisationDao implements IDataProviderDao<Organisation> {
	public final static String[] searchFields = {"name"};
	@PersistenceContext
	private EntityManager em;

	public Organisation get(long id) {
		TypedQuery<Organisation> query = em.createNamedQuery("getOrganisationById", Organisation.class);
		query.setParameter("organisationId", id);
		Organisation o = null;
		try {
			o = query.getSingleResult();
		} catch (NoResultException e) {
			// o = null;
		}
		return o;
	}

	public List<Organisation> get(int start, int count) {
		TypedQuery<Organisation> q = em.createNamedQuery("getNondeletedOrganisations", Organisation.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public List<Organisation> get(String search, int start, int count, String sort) {
		TypedQuery<Organisation> q = em.createQuery(DaoHelper.getSearchQuery("Organisation", "o", search, true, false, sort, searchFields), Organisation.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public long count() {
		TypedQuery<Long> q = em.createNamedQuery("countOrganisations", Long.class);
		return q.getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Organisation", "o", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public List<Organisation> get(Collection<Long> ids) {
		return em.createNamedQuery("getOrganisationsByIds", Organisation.class).setParameter("ids", ids).getResultList();
	}

	public Organisation update(Organisation entity, Long userId) {
		if (entity.getId() == null) {
			if (userId != null) {
				entity.setInsertedby(userId);
			}
			entity.setInserted(new Date());
			em.persist(entity);
		} else {
			if (userId != null) {
				entity.setUpdatedby(userId);
			}
			entity.setUpdated(new Date());
			em.merge(entity);
		}
		return entity;
	}

	public void delete(Organisation entity, Long userId) {
		em.createNamedQuery("deleteUsersFromOrganisation")
			.setParameter("id", entity.getId())
			.executeUpdate();

		entity.setDeleted(true);
		if (userId != null) {
			entity.setUpdatedby(userId);
		}

		em.merge(entity);
	}
}
