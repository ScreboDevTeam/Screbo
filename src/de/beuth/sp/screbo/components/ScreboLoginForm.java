package de.beuth.sp.screbo.components;

import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Extension of the LoginForm component (addon) to fit our needs.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class ScreboLoginForm extends LoginForm {
	protected TextField mailAddressTextField;
	protected PasswordField passwordField;
	protected Button loginButton;

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
	protected Component createContent(TextField mailAddressTextField, PasswordField passwordField, Button loginButton) {
		this.mailAddressTextField = mailAddressTextField;
		this.passwordField = passwordField;
		this.loginButton = loginButton;

		loginButton.setCaption("login / register");

		mailAddressTextField.setStyleName("loginMailAddress", true);
		passwordField.setStyleName("loginPassword", true);
		loginButton.setStyleName("loginButton", true);

		VerticalLayout layout = new VerticalLayout();
		layout.setStyleName("ScreboLoginForm");
		layout.setSpacing(true);
		layout.setMargin(true);

		layout.addComponent(mailAddressTextField);
		layout.addComponent(passwordField);
		layout.addComponent(loginButton);

		layout.setComponentAlignment(loginButton, Alignment.MIDDLE_RIGHT);

		mailAddressTextField.focus();

		return layout;
	}

	public void clearPasswordField() {
		passwordField.clear();
	}

	public void focusPasswordField() {
		passwordField.focus();
	}

	public void focusUserNameField() {
		mailAddressTextField.focus();
	}

}
