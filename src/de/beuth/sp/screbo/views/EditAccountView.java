package de.beuth.sp.screbo.views;

import java.util.Objects;

import com.google.common.base.Strings;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import de.beuth.sp.screbo.SHA256;
import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.MyCouchDbRepositorySupport.TransformationRunnable;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.RequestNavigateToRetrospectivesViewEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.beuth.sp.screbo.eventBus.events.SetEditAccountFormData;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings("serial")
public class EditAccountView extends ScreboView implements ScreboEventListener {

	protected User user;
	protected TextField firstNameTextfield = new TextField("first name");
	protected TextField lastNameTextfield = new TextField("last name");
	protected TextField emailAddressTextfield = new TextField("email address");
	protected Button saveButton = new Button();
	protected PasswordField passwordTextfield = new PasswordField();
	protected PasswordField passwordConfirmTextfield = new PasswordField();

	protected EmailValidator emailValidator = new EmailValidator("Please enter a valid email address");
	protected StringLengthValidator userNameValidator = new StringLengthValidator("Please enter your name.", 1, null, false);
	protected StringLengthValidator passwordValidator = new StringLengthValidator("Your password must be at least 8 characters long.", 8, null, false);

	protected String firstName = "";
	protected String lastName = "";
	protected String password1 = "";
	protected String password2 = "";
	protected String emailAddress;

	public EditAccountView(ScreboUI screboUI) {
		super(screboUI);
		screboUI.getEventBus().addEventListener(this, true);

		Panel editProfilePanel = new Panel("edit profile");
		editProfilePanel.setSizeUndefined();
		editProfilePanel.setStyleName("edit_profile_panel");

		GridLayout editProfileLayout = new GridLayout(2, 7);
		editProfileLayout.setSpacing(true);

		Label userDataLabel = new Label("edit your data");
		userDataLabel.setStyleName("sectionLabel");
		Label changePasswordLabel = new Label("change your password");
		changePasswordLabel.setStyleName("sectionLabel");

		firstNameTextfield.addValidator(userNameValidator);
		firstNameTextfield.setValidationVisible(false);
		firstNameTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		firstNameTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				firstName = event.getText();
				onFieldsChanged();
			}
		});

		lastNameTextfield.addValidator(userNameValidator);
		lastNameTextfield.setValidationVisible(false);
		lastNameTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		lastNameTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				lastName = event.getText();
				onFieldsChanged();
			}
		});

		emailAddressTextfield.addValidator(emailValidator);
		emailAddressTextfield.setValidationVisible(false);
		emailAddressTextfield.setStyleName("emailAddressTextfield");
		emailAddressTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		emailAddressTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				emailAddress = event.getText();
				onFieldsChanged();
			}
		});

		passwordTextfield.addValidator(passwordValidator);
		passwordTextfield.setValidationVisible(false);
		passwordTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		passwordTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				password1 = event.getText();
				onFieldsChanged();
			}
		});
		passwordConfirmTextfield.addValidator(passwordValidator);
		passwordConfirmTextfield.setValidationVisible(false);
		passwordConfirmTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		passwordConfirmTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				password2 = event.getText();
				onFieldsChanged();
			}
		});

		saveButton.addClickListener(event -> {
			save();
		});

		editProfileLayout.addComponent(userDataLabel, 0, 0, 1, 0);
		editProfileLayout.addComponents(firstNameTextfield, lastNameTextfield);
		editProfileLayout.addComponent(emailAddressTextfield, 0, 2, 1, 2);
		editProfileLayout.addComponent(changePasswordLabel, 0, 3, 1, 3);
		//		editProfileLayout.addComponent(currentPasswordTextfield, 0, 4, 1, 4);
		editProfileLayout.addComponents(passwordTextfield, passwordConfirmTextfield);
		editProfileLayout.addComponent(saveButton, 0, 6, 1, 6);
		editProfileLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

		editProfilePanel.setContent(editProfileLayout);

		addComponent(editProfilePanel);
		firstNameTextfield.focus();
	}

	protected void onFieldsChanged() {
		boolean formIsValid = true;

		if ((!Strings.isNullOrEmpty(password1) || user == null) && !passwordValidator.isValid(password1)) {
			passwordTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			passwordTextfield.setValidationVisible(false);
		}
		if ((!Strings.isNullOrEmpty(password2) || user == null) && !passwordValidator.isValid(password2)) {
			passwordConfirmTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			passwordConfirmTextfield.setValidationVisible(false);
		}
		if (!Strings.isNullOrEmpty(password1) && !Objects.equals(password1, password2)) {
			UserError userError = new UserError("The passwords you have entered do not match.");
			passwordTextfield.setComponentError(userError);
			passwordConfirmTextfield.setComponentError(userError);
			formIsValid = false;
		} else {
			passwordTextfield.setComponentError(null);
			passwordConfirmTextfield.setComponentError(null);
		}
		if (!emailValidator.isValid(emailAddress)) {
			emailAddressTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			emailAddressTextfield.setValidationVisible(false);
		}

		if (!userNameValidator.isValid(firstName)) {
			firstNameTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			firstNameTextfield.setValidationVisible(false);
		}

		if (!userNameValidator.isValid(lastName)) {
			lastNameTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			lastNameTextfield.setValidationVisible(false);
		}

		if (Strings.isNullOrEmpty(emailAddress) || !emailValidator.isValid(emailAddress)) {
			emailAddressTextfield.setComponentError(new UserError(emailValidator.getErrorMessage()));
			formIsValid = false;
		} else {
			emailAddressTextfield.setComponentError(null);
		}

		saveButton.setEnabled(formIsValid);
	}

	protected void save() {
		try {
			if (user == null) {
				User userToWrite = new User();

				userToWrite.setEmailAddress(emailAddress);
				userToWrite.setFirstName(firstNameTextfield.getValue());
				userToWrite.setLastName(lastNameTextfield.getValue());
				userToWrite.setPassword(SHA256.getSHA256(password1));

				ScreboServlet.getUserRepository().add(userToWrite);
				screboUI.doLogin(userToWrite);
			} else {
				ScreboServlet.getUserRepository().update(user, new TransformationRunnable<User>() {

					@Override
					public void applyChanges(User userToWrite) throws Exception {
						userToWrite.setFirstName(firstNameTextfield.getValue());
						userToWrite.setLastName(lastNameTextfield.getValue());
						userToWrite.setEmailAddress(emailAddress);

						if (Objects.equals(password1, password2) && !Strings.isNullOrEmpty(password1)) {
							userToWrite.setPassword(SHA256.getSHA256(password1));
						}
					}
				});
			}
			screboUI.getEventBus().fireEvent(new RequestNavigateToRetrospectivesViewEvent());
			screboUI.getEventBus().fireEvent(new UserChangedEvent());
		} catch (Exception e) {
			logger.error("Could not write to database.", e);
			screboUI.fireCouldNotWriteToDatabaseEvent(e);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		load();
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			load();
		} else if (screboEvent instanceof SetEditAccountFormData) {
			firstName = "";
			lastName = "";
			emailAddress = ((SetEditAccountFormData) screboEvent).getMailAddress();
			password1 = ((SetEditAccountFormData) screboEvent).getPassword();
			password2 = password1;

			emailAddressTextfield.setValue(emailAddress);
			passwordTextfield.setValue(password1);
			passwordConfirmTextfield.setValue(password2);

			onFieldsChanged();
		}
	}

	protected void load() {
		user = UserRepository.getUserFromSession();
		saveButton.setCaption(user == null ? "create account" : "save changes");
		passwordTextfield.setCaption(user == null ? "password" : "new password");
		passwordConfirmTextfield.setCaption(user == null ? "confirm password" : "confirm new password");

		if (user != null) {
			firstName = Strings.nullToEmpty(user.getFirstName());
			lastName = Strings.nullToEmpty(user.getLastName());
			emailAddress = Strings.nullToEmpty(user.getEmailAddress());

			firstNameTextfield.setValue(firstName);
			lastNameTextfield.setValue(lastName);
			emailAddressTextfield.setValue(emailAddress);

			onFieldsChanged();
		}
	}

}
