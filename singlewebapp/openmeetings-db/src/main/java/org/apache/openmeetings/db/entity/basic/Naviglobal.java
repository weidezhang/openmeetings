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
package org.apache.openmeetings.db.entity.basic;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@NamedQueries({
		@NamedQuery(name = "getNavigation", query = "SELECT DISTINCT ng from Naviglobal ng JOIN ng.mainnavi nm "
				+ "WHERE nm.deleted = false AND ng.level_id <= :level_id AND nm.level_id <= :level_id "
				+ "AND ng.deleted = false ORDER BY ng.naviorder ASC"),
		@NamedQuery(name = "getNavigationById", query = "SELECT ng from Naviglobal ng WHERE ng.id = :id") })
@Table(name = "naviglobal")
public class Naviglobal implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "icon")
	private String icon;

	@Column(name = "isleaf")
	private Boolean isleaf;

	@Column(name = "isopen")
	private Boolean isopen;

	@Column(name = "action")
	private String action;

	@Column(name = "updatetime")
	private Date updatetime;

	@Column(name = "starttime")
	private Date starttime;

	@Column(name = "comment_field")
	private String comment;

	@Column(name = "naviorder")
	private Integer naviorder;

	@Column(name = "level_id")
	private Long level_id;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "fieldvalues_id")
	private Long fieldvalues_id;

	@Column(name = "tooltip_fieldvalues_id")
	private Long tooltip_fieldvalues_id;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "global_id")
	@ForeignKey(enabled = true)
	@OrderBy("naviorder")
	private List<Navimain> mainnavi;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(Boolean isleaf) {
		this.isleaf = isleaf;
	}

	public Boolean getIsopen() {
		return isopen;
	}

	public void setIsopen(Boolean isopen) {
		this.isopen = isopen;
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

	public boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNaviorder() {
		return naviorder;
	}

	public void setNaviorder(Integer naviorder) {
		this.naviorder = naviorder;
	}

	public Long getLevel_id() {
		return level_id;
	}

	public void setLevel_id(Long level_id) {
		this.level_id = level_id;
	}

	public List<Navimain> getMainnavi() {
		return mainnavi;
	}

	public void setMainnavi(List<Navimain> mainnavi) {
		this.mainnavi = mainnavi;
	}

	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}

	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

	public Long getTooltip_fieldvalues_id() {
		return tooltip_fieldvalues_id;
	}

	public void setTooltip_fieldvalues_id(Long tooltip_fieldvalues_id) {
		this.tooltip_fieldvalues_id = tooltip_fieldvalues_id;
	}

	@Override
	public String toString() {
		return "Naviglobal [id=" + id + ", name=" + name + ", action=" + action + ", naviorder=" + naviorder + ", deleted="
				+ deleted + ", fieldvalues_id=" + fieldvalues_id + ", tooltip_fieldvalues_id=" + tooltip_fieldvalues_id + "]";
	}

}
