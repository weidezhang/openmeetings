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
package org.apache.openmeetings.db.entity.label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@NamedQueries({
	@NamedQuery(name = "getNextLanguageId", query = "SELECT MAX(fl.id) FROM FieldLanguage fl")
	, @NamedQuery(name = "getFullLanguageById", query = "SELECT fl FROM FieldLanguage fl LEFT JOIN FETCH fl.languageValues WHERE fl.id = :id")
	, @NamedQuery(name = "deleteAllLanguages", query = "DELETE FROM FieldLanguage")
	, @NamedQuery(name = "getLanguageById", query = "SELECT c FROM FieldLanguage c WHERE c.deleted = false AND c.id = :id")
	, @NamedQuery(name = "getLanguages", query = "SELECT c FROM FieldLanguage c WHERE c.deleted = false")
})
@Table(name = "fieldlanguage")
public class FieldLanguage implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private Long id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="starttime")
	private Date starttime;
	
	@Column(name="updatetime")
	private Date updatetime;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Column(name="rtl")
	private boolean rtl;
	
	@Column(name="code")
	private String code;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "language_id", insertable = true, updatable = true)
	private Collection<Fieldlanguagesvalues> languageValues;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isRtl() {
		return rtl;
	}
	public void setRtl(boolean rtl) {
		this.rtl = rtl;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public Collection<Fieldlanguagesvalues> getLanguageValues() {
		return languageValues == null ? new ArrayList<Fieldlanguagesvalues>() : languageValues;
	}
	public void setLanguageValues(Collection<Fieldlanguagesvalues> languageValues) {
		if (languageValues != null) {
			this.languageValues = languageValues;
		}
	}
	public Map<Long, Fieldlanguagesvalues> getLanguageValuesMap() {
		Collection<Fieldlanguagesvalues> langVals = getLanguageValues();
		Map<Long, Fieldlanguagesvalues> result = new Hashtable<Long, Fieldlanguagesvalues>(langVals.size());
		for (Fieldlanguagesvalues flv : langVals) {
			result.put(flv.getFieldvalues().getId(), flv);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "FieldLanguage [id=" + id + ", name=" + name + ", deleted=" + deleted + ", rtl=" + rtl + ", code=" + code
				+ "]";
	}
}
