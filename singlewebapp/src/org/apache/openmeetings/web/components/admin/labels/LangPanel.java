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
package org.apache.openmeetings.web.components.admin.labels;

import java.util.Iterator;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.data.basic.FieldValueDaoImpl;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.OmDataView;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 * Language Editor, add/insert/update {@link Fieldlanguagesvalues} and
 * add/delete {@link FieldLanguage} contains several Forms and one list
 * 
 * @author solomax, swagner
 * 
 */
public class LangPanel extends AdminPanel {
	private static final long serialVersionUID = 5904180813198016592L;

	FieldLanguage language;
	private LangForm langForm;

	public LangPanel(String id) {
		super(id);
		FieldLanguageDaoImpl langDao = Application
				.getBean(FieldLanguageDaoImpl.class);
		language = langDao.getFieldLanguageById(1L);

		Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
		flv.setLanguage_id(language.getLanguage_id());
		final LabelsForm form = new LabelsForm("form", this, flv);
		form.showNewRecord();
		add(form);

		final OmDataView<Fieldvalues> dataView = new OmDataView<Fieldvalues>(
				"langList", new OmDataProvider<Fieldvalues>(
						FieldValueDaoImpl.class) {
					private static final long serialVersionUID = -6822789354860988626L;

					public Iterator<? extends Fieldvalues> iterator(
							long first, long count) {
						return Application
								.getBean(FieldValueDaoImpl.class)
								.get(language.getLanguage_id(), (int) first,
										(int) count).iterator();
					}
				}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Fieldvalues> item) {
				final Fieldvalues fv = item.getModelObject();
				item.add(new Label("lblId", "" + fv.getFieldvalues_id()));
				item.add(new Label("name", fv.getName()));
				item.add(new Label("value", fv.getFieldlanguagesvalue() != null ? fv.getFieldlanguagesvalue().getValue() : null));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(fv.getFieldlanguagesvalue());
						form.hideNewRecord();
						target.add(form);
					}
				});
				item.add(AttributeModifier.append("class", "clickable "
						+ ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};

		final WebMarkupContainer listContainer = new WebMarkupContainer(
				"listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dataView.modelChanging();
				target.add(listContainer);
			}
		});
		langForm = new LangForm("langForm", listContainer, this);
		add(langForm);
		add(new AddLanguageForm("addLangForm", this));
	}

	public LangForm getLangForm() {
		return langForm;
	}
}
