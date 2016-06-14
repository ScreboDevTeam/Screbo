package de.beuth.sp.screbo.components;

import com.ejt.vaadin.loginform.LoginForm;
import com.google.common.base.Strings;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ScreboLoginForm extends LoginForm {
	protected TextField userNameField;
	protected PasswordField passwordField;

	@Override
	protected String getUserNameFieldCaption() {
		return "email address";
	}

	@Override
	protected String getLoginButtonCaption() {
		return "login / register";
	}

	@Override
	protected String getPasswordFieldCaption() {
		return "password";
	}

	@Override
	protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
		this.userNameField = userNameField;
		userNameField.addTextChangeListener(new TextChangeListener() {
			EmailValidator emailValidator = new EmailValidator("Please enter a valid email address");
			@Override
			public void textChange(TextChangeEvent event) {
				loginButton.setEnabled(!Strings.isNullOrEmpty(event.getText()) && emailValidator.isValid(event.getText()));
			}
		});
		userNameField.setTextChangeEventMode(TextChangeEventMode.EAGER);

		this.passwordField = passwordField;
		loginButton.setCaption("login / register");

		userNameField.setStyleName("loginMailAddress", true);
		passwordField.setStyleName("loginPassword", true);
		loginButton.setStyleName("loginButton", true);

		VerticalLayout layout = new VerticalLayout();
		layout.setStyleName("ScreboLoginForm");
		layout.setSpacing(true);
		layout.setMargin(true);

		layout.addComponent(userNameField);
		layout.addComponent(passwordField);
		layout.addComponent(loginButton);

		layout.setComponentAlignment(loginButton, Alignment.MIDDLE_RIGHT);

		return layout;
	}

	public void clearPasswordField() {
		passwordField.clear();
	}

	public void focusPasswordField() {
		passwordField.focus();
	}

	public void focusUserNameField() {
		userNameField.focus();
	}

}
