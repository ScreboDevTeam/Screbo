package de.beuth.sp.screbo.views;

import org.ektorp.DocumentNotFoundException;

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
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;
import de.steinwedel.messagebox.MessageBox;

@SuppressWarnings("serial")
public class LoginView extends ScreboView implements LoginListener {
	protected ScreboLoginForm loginForm;
	protected Panel loginPanel = new Panel("login");

	public LoginView(ScreboUI screboUI) {
		super(screboUI);

		loginPanel.setSizeUndefined();
		loginPanel.setStyleName("login_panel");

		addComponent(loginPanel);
		setExpandRatio(loginPanel, 1);
		setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		loginForm = new ScreboLoginForm();
		loginForm.addLoginListener(this);
		loginPanel.setContent(loginForm);
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

		MessageBox.createQuestion().withCaption("Not found").withMessage("A user with the mail address " + mailAddress + " was not found.\nDo you want to create the user?").withYesButton(new Runnable() {

			@Override
			public void run() {
				User user = new User();
				user.setEmailAddress(mailAddress);
				user.setPassword(password);
				ScreboServlet.getUserRepository().add(user);
				doLogin(user);
			}
		}).withNoButton(new Runnable() {

			@Override
			public void run() {
				loginForm.clear();
				loginForm.focusUserNameField();
			}
		}).open();
	}

	protected void doLogin(User user) {
		Notification.show("Hi", "Welcome " + user.getDisplayName(), Notification.Type.TRAY_NOTIFICATION);
		UserRepository.setSessionUser(user);
		screboUI.getEventBus().fireEvent(new UserChangedEvent());
		screboUI.afterLogin();
	}

}
