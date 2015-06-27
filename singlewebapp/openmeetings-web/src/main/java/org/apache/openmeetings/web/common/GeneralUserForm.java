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
package org.apache.openmeetings.web.common;

import static org.apache.openmeetings.db.util.UserHelper.getMinPasswdLength;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.AVAILABLE_TIMEZONES;
import static org.apache.wicket.validation.validator.StringValidator.minimumLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.dao.user.SalutationDao;
import org.apache.openmeetings.db.dao.user.StateDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.OrganisationUser;
import org.apache.openmeetings.db.entity.user.Salutation;
import org.apache.openmeetings.db.entity.user.State;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.CalendarHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.IMarkupSourcingStrategy;
import org.apache.wicket.markup.html.panel.PanelMarkupSourcingStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.threeten.bp.LocalDate;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;
import org.wicketstuff.select2.TextChoiceProvider;

import com.googlecode.wicket.kendo.ui.form.datetime.local.AjaxDatePicker;
import com.googlecode.wicket.kendo.ui.resource.KendoGlobalizeResourceReference;

public class GeneralUserForm extends Form<User> {
	private static final long serialVersionUID = 1L;
	private Salutation salutation;
	private LocalDate age;
	private PasswordTextField passwordField;
	private RequiredTextField<String> email;

	public GeneralUserForm(String id, IModel<User> model, boolean isAdminForm) {
		super(id, model);

		//TODO should throw exception if non admin User edit somebody else (or make all fields read-only)
		add(passwordField = new PasswordTextField("password", new Model<String>()));
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		passwordField.setRequired(false).add(minimumLength(getMinPasswdLength(cfgDao)));

		updateModelObject(getModelObject());
		SalutationDao salutDao = getBean(SalutationDao.class);
		add(new DropDownChoice<Salutation>("salutation"
				, new PropertyModel<Salutation>(this, "salutation")
				, salutDao.get()
				, new ChoiceRenderer<Salutation>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(Salutation object) {
						return getString("" + object.getLabelId());
					}

					@Override
					public String getIdValue(Salutation object, int index) {
						return "" + object.getId();
					}
				})
			.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					GeneralUserForm.this.getModelObject().setSalutationId(salutation.getId());
				}
			}));
		add(new TextField<String>("firstname"));
		add(new TextField<String>("lastname"));
		
		add(new DropDownChoice<String>("timeZoneId", AVAILABLE_TIMEZONES));

		add(new LanguageDropDown("languageId"));

		add(email = new RequiredTextField<String>("address.email"));
		email.setLabel(Model.of(Application.getString(137)));
		email.add(RfcCompliantEmailAddressValidator.getInstance());
		add(new TextField<String>("address.phone"));
		add(new CheckBox("sendSMS"));
		add(new AjaxDatePicker("age", new PropertyModel<LocalDate>(this, "age"), WebSession.get().getLocale()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onValueChanged(AjaxRequestTarget target) {
				GeneralUserForm.this.getModelObject().setAge(CalendarHelper.getDate(age));
			}
		});
		add(new TextField<String>("address.street"));
		add(new TextField<String>("address.additionalname"));
		add(new TextField<String>("address.zip"));
		add(new TextField<String>("address.town"));
		add(new DropDownChoice<State>("address.state", getBean(StateDao.class).get(), new ChoiceRenderer<State>("name", "id")));
		add(new TextArea<String>("address.comment"));

		final List<OrganisationUser> orgUsers;
		if (isAdminForm) {
			List<Organisation> orgList = getBean(OrganisationDao.class).get(0, Integer.MAX_VALUE);
			orgUsers = new ArrayList<OrganisationUser>(orgList.size());
			for (Organisation org : orgList) {
				orgUsers.add(new OrganisationUser(org));
			}
		} else {
			orgUsers = getModelObject().getOrganisationUsers();
		}
		add(new Select2MultiChoice<OrganisationUser>("organisationUsers", null, new TextChoiceProvider<OrganisationUser>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getDisplayText(OrganisationUser choice) {
				return choice.getOrganisation().getName();
			}

			@Override
			protected Object getId(OrganisationUser choice) {
				return choice.getOrganisation().getId();
			}

			@Override
			public void query(String term, int page, Response<OrganisationUser> response) {
				for (OrganisationUser ou : orgUsers) {
					if (Strings.isEmpty(term) || (!Strings.isEmpty(term) && ou.getOrganisation().getName().contains(term))) {
						response.add(ou);
					}
				}
			}

			@Override
			public Collection<OrganisationUser> toChoices(Collection<String> _ids) {
				List<Long> ids = new ArrayList<Long>();
				for (String id : _ids) {
					ids.add(Long.parseLong(id));
				}
				List<OrganisationUser> list = new ArrayList<OrganisationUser>();
				for (Organisation o : getBean(OrganisationDao.class).get(ids)) {
					list.add(new OrganisationUser(o));
				}
				return list;
			}
		}).setEnabled(isAdminForm));
	}

	public void updateModelObject(User u) {
		salutation = getBean(SalutationDao.class).get(u.getSalutationId());
		age = CalendarHelper.getDate(u.getAge());
	}
	
	@Override
	protected void onValidate() {
		User u = getModelObject();
		if(!getBean(UserDao.class).checkEmail(email.getConvertedInput(), u.getType(), u.getDomainId(), u.getId())) {
			error(Application.getString(1000));
		}
		super.onValidate();
	}
	
	public PasswordTextField getPasswordField() {
		return passwordField;
	}
	
	@Override
	protected IMarkupSourcingStrategy newMarkupSourcingStrategy() {
		return new PanelMarkupSourcingStrategy(false);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new KendoGlobalizeResourceReference(WebSession.get().getLocale())));
	}
}
