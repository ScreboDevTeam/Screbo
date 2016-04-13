package de.beuth.sp.screbo;

import javax.servlet.annotation.WebServlet;

import com.ejt.vaadin.loginform.LoginForm.LoginEvent;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("screbo")
public class ScreboUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = ScreboUI.class, widgetset = "de.beuth.sp.screbo.widgetset.ScreboWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
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

		Label screboLabel = new Label("Welcome to Screbo, please select your board:");

		ComboBox boardComboBox = new ComboBox();
		boardComboBox.setEnabled(false);

		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setStyleName("topBar", true);
		topBar.addComponent(screboLabel);
		topBar.addComponent(boardComboBox);
		topBar.setComponentAlignment(screboLabel, Alignment.MIDDLE_RIGHT);
		topBar.setComponentAlignment(boardComboBox, Alignment.MIDDLE_RIGHT);

		final VerticalLayout layout = new VerticalLayout();

		layout.addComponent(topBar);
		layout.addComponent(loginPanel);
		layout.setExpandRatio(topBar, 0);

		layout.setSizeFull();
		layout.setComponentAlignment(topBar, Alignment.TOP_RIGHT);
		layout.setExpandRatio(loginPanel, 1);
		layout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		setContent(layout);
	}

}