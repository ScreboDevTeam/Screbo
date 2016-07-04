package de.beuth.sp.screbo.views;

import org.ektorp.DocumentNotFoundException;

import com.ejt.vaadin.loginform.LoginForm.LoginEvent;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.google.common.base.Strings;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Panel;

import de.beuth.sp.screbo.SHA256;
import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.components.ScreboLoginForm;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.eventBus.events.RequestDisplayErrorMessageEvent;
import de.beuth.sp.screbo.eventBus.events.SetEditAccountFormData;
import de.steinwedel.messagebox.MessageBox;

/**
 * Page which contains the LoginDialog.
 * 
 * @author volker.gronau
 *
 */
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
		final String password = event.getPassword();

		if (Strings.isNullOrEmpty(mailAddress)) {
			screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The mail address field must not be empty."));
			return;
		}
		if (Strings.isNullOrEmpty(password)) {
			screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The password field must not be empty."));
			return;
		}

		try {

			User user;
			try {
				user = ScreboServlet.getUserRepository().get(mailAddress);
			} catch (DocumentNotFoundException e) {
				logger.warn("Did not find user with mailAddress:{}", mailAddress, e);
				askToCreateNewUser(mailAddress, password);
				return;
			}

			final String hashedPassword = SHA256.getSHA256(password);
			if (hashedPassword.equals(user.getPassword())) {
				screboUI.doLogin(user);
			} else {
				showWrongPassword();
			}

		} catch (Exception e) {
			logger.error("Error while logging in user with mailAddress:{}", mailAddress, e);
		}
	}

	protected void showWrongPassword() {
		screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("Sorry, wrong password."));
		loginForm.clearPasswordField();
		loginForm.focusPasswordField();
	}

	protected void askToCreateNewUser(final String mailAddress, final String password) {

		MessageBox.createQuestion().withCaption("Not found").withMessage("An account with the mail address " + mailAddress + " was not found.\nDo you want to create the account?").withYesButton(new Runnable() {

			@Override
			public void run() {
				screboUI.getNavigator().navigateTo("editAccount");
				screboUI.getEventBus().fireEvent(new SetEditAccountFormData(mailAddress, password));
			}
		}).withNoButton(new Runnable() {

			@Override
			public void run() {
				loginForm.clear();
				loginForm.focusUserNameField();
			}
		}).open();
	}

}
