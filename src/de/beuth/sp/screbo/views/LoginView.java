package de.beuth.sp.screbo.views;

import org.ektorp.DocumentNotFoundException;
import org.vaadin.dialogs.ConfirmDialog;

import com.ejt.vaadin.loginform.LoginForm.LoginEvent;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;

import de.beuth.sp.screbo.SHA256;
import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.components.ScreboLoginForm;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings("serial")
public class LoginView extends ScreboView implements LoginListener {
	protected ScreboLoginForm loginForm;

	public LoginView(ScreboUI screboUI) {
		super(screboUI);
		Panel loginPanel = new Panel("Login");
		loginPanel.setSizeUndefined();
		loginPanel.setStyleName("login_panel");

		loginForm = new ScreboLoginForm();
		loginForm.addLoginListener(this);
		setStyleName("login_bg");
		loginPanel.setContent(loginForm);

		addComponent(loginPanel);
		setExpandRatio(loginPanel, 1);
		setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	/**
	 * Called when Login / Register button is pushed.
	 */
	@Override
	public void onLogin(LoginEvent event) {
		final String mailAddress = event.getUserName();
		try {
			final String password = SHA256.getSHA256(event.getPassword());
			User user = null;
			try {
				user = ScreboServlet.getUserRepository().get(mailAddress);
			} catch (DocumentNotFoundException e) {
				logger.warn("Did not find user with mailAddress:{}", mailAddress, e);
				askToCreateNewUser(mailAddress, password);
				return;
			}

			if (password.equals(user.getPassword())) {
				doLogin(user);
			} else {
				showWrongPassword();
			}

		} catch (Exception e) {
			logger.error("Error while logging in user with mailAddress:{}", mailAddress, e);
		}
	}

	protected void showWrongPassword() {
		Notification.show("Sorry, wrong password.", Notification.Type.WARNING_MESSAGE);
		loginForm.clearPasswordField();
		loginForm.focusPasswordField();
	}

	protected void askToCreateNewUser(final String mailAddress, final String password) {
		ConfirmDialog.show(screboUI, "Not found", "A user with the mail address" + mailAddress + " was not found.\nDo you want to create a new user?", "Sorry, my bad", "Create new user", new ConfirmDialog.Listener() {

			@Override
			public void onClose(ConfirmDialog dialog) {
				if (!dialog.isConfirmed()) { //Create new user clicked, I ordered the buttons that the other one is the default
					User user = new User();
					user.setEmailAddress(mailAddress);
					user.setPassword(password);
					ScreboServlet.getUserRepository().add(user);
					doLogin(user);
				} else {
					loginForm.clear();
					loginForm.focusUserNameField();
				}
			}
		});
	}

	protected void doLogin(User user) {
		Notification.show("Hi", "Welcome " + user.getDisplayName(), Notification.Type.TRAY_NOTIFICATION);
		user.setAsSessionUser();
		screboUI.getEventBus().fireEvent(new UserChangedEvent());
		screboUI.afterLogin();
	}

}
