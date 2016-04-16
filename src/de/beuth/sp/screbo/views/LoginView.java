package de.beuth.sp.screbo.views;

import com.ejt.vaadin.loginform.LoginForm.LoginEvent;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

import de.beuth.sp.screbo.ScreboLoginForm;

@SuppressWarnings("serial")
public class LoginView extends MainView {

	{
		Panel loginPanel = new Panel("Login");
		loginPanel.setSizeUndefined();

		ScreboLoginForm loginForm = new ScreboLoginForm();
		loginForm.addLoginListener(new LoginListener() {
			@Override
			public void onLogin(LoginEvent event) {
				Notification.show("Hi", "Logged in with user name " + event.getUserName() + " and password of length " + event.getPassword().length(), Notification.Type.TRAY_NOTIFICATION);
			}
		});
		loginPanel.setContent(loginForm);

		addComponent(loginPanel);
		setExpandRatio(loginPanel, 1);
		setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
