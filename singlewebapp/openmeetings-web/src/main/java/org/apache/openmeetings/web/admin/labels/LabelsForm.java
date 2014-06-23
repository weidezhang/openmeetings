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
package org.apache.openmeetings.web.admin.labels;

import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.label.FieldValueDao;
import org.apache.openmeetings.db.entity.label.Fieldlanguagesvalues;
import org.apache.openmeetings.db.entity.label.Fieldvalues;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * Add/edit/delete {@link Fieldlanguagesvalues}
 * 
 * @author swagner
 * 
 */
public class LabelsForm extends AdminBaseForm<Fieldlanguagesvalues> {
	private static final long serialVersionUID = 1L;
	private LangPanel panel;
	
	public LabelsForm(String id, LangPanel panel, Fieldlanguagesvalues fieldlanguagesvalues) {
		super(id, new CompoundPropertyModel<Fieldlanguagesvalues>(fieldlanguagesvalues));
		this.panel = panel;
		
		add(new Label("fieldvalues.id"));
		add(new TextField<String>("fieldvalues.name"));
		add(new TextArea<String>("value"));
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
		Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
		flv.setLanguage_id(panel.language.getId());
		this.setModelObject(flv);
		target.add(this);
		target.appendJavaScript("labelsInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Fieldlanguagesvalues flv = getModelObject();
		if (flv.getId() != null) {
			flv = Application.getBean(FieldLanguagesValuesDao.class).get(getModelObject().getId());
		} else {
			flv = new Fieldlanguagesvalues();
		}
		this.setModelObject(flv);
		target.add(this);
		target.appendJavaScript("labelsInit();");
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Fieldlanguagesvalues flv = getModelObject();
		Fieldvalues fv = flv.getFieldvalues();
		Application.getBean(FieldValueDao.class).update(fv, WebSession.getUserId());
		
		flv.setFieldvalues(fv);
		Application.getBean(FieldLanguagesValuesDao.class)
			.update(flv, WebSession.getUserId());
		hideNewRecord();
		target.add(panel.listContainer);
		target.appendJavaScript("labelsInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(FieldLanguagesValuesDao.class)
			.delete(getModelObject(), WebSession.getUserId());
		target.add(panel.listContainer);
		target.appendJavaScript("labelsInit();");
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}
}
