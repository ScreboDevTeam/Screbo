package de.beuth.sp.screbo;

import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ScreboLoginForm extends LoginForm {

	@Override
	protected Component createContent(TextField userNameField, PasswordField passwordField, Button loginButton) {
		loginButton.setCaption("Login / Register");

		userNameField.setStyleName("loginUserName", true);
		passwordField.setStyleName("loginPassword", true);
		loginButton.setStyleName("loginButton", true);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		layout.addComponent(userNameField);
		layout.addComponent(passwordField);
		layout.addComponent(loginButton);

		layout.setComponentAlignment(loginButton, Alignment.MIDDLE_RIGHT);

		return layout;
	}

}
