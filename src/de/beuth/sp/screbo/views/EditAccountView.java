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
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings("serial")
public class EditAccountView extends ScreboView implements ScreboEventListener {

	protected User user;
	protected TextField firstNameTextfield = new TextField("first name");
	protected TextField lastNameTextfield = new TextField("last name");
	protected TextField emailAddressTextfield = new TextField("email address");
	protected Button saveButton = new Button("save changes");
	protected EmailValidator emailValidator = new EmailValidator("Please enter a valid email address");
	protected PasswordField newPasswordTextfield = new PasswordField("new password");
	protected PasswordField newPasswordConfirmTextfield = new PasswordField("confirm new password");
	protected StringLengthValidator passwordValidator = new StringLengthValidator("Your password must be at least 8 characters long", 8, null, true);


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

		emailAddressTextfield.setRequired(true);
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
		
		newPasswordTextfield.addValidator(passwordValidator);
		newPasswordTextfield.setValidationVisible(false);
		newPasswordTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		newPasswordTextfield.addTextChangeListener(new TextChangeListener() {

			@Override
			public void textChange(TextChangeEvent event) {
				password1 = event.getText();
				onFieldsChanged();
			}
		});
		newPasswordConfirmTextfield.addValidator(passwordValidator);
		newPasswordConfirmTextfield.setValidationVisible(false);
		newPasswordConfirmTextfield.setTextChangeEventMode(TextChangeEventMode.EAGER);
		newPasswordConfirmTextfield.addTextChangeListener(new TextChangeListener() {

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
		editProfileLayout.addComponents(newPasswordTextfield, newPasswordConfirmTextfield);
		editProfileLayout.addComponent(saveButton, 0, 6, 1, 6);
		editProfileLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

		editProfilePanel.setContent(editProfileLayout);

		addComponent(editProfilePanel);
	}

	protected void onFieldsChanged() {
		boolean formIsValid = true;
		if (!Strings.isNullOrEmpty(password1) && !passwordValidator.isValid(password1)) {
			newPasswordTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			newPasswordTextfield.setValidationVisible(false);
		}
		if (!Strings.isNullOrEmpty(password2) && !passwordValidator.isValid(password2)) {
			newPasswordConfirmTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			newPasswordConfirmTextfield.setValidationVisible(false);
		}
		if ((!Strings.isNullOrEmpty(password1) || !Strings.isNullOrEmpty(password2)) && !Objects.equals(password1, password2)) {
			newPasswordTextfield.setComponentError(new UserError("The passwords you entered do not match."));
			newPasswordConfirmTextfield.setComponentError(new UserError("The passwords you entered do not match."));
			formIsValid = false;
		} else {
			newPasswordTextfield.setComponentError(null);
			newPasswordConfirmTextfield.setComponentError(null);
		}
		if(!Strings.isNullOrEmpty(emailAddress) && !emailValidator.isValid(emailAddress)) {
			emailAddressTextfield.setValidationVisible(true);
			formIsValid = false;
		} else {
			emailAddressTextfield.setValidationVisible(false);
		}
		saveButton.setEnabled(formIsValid);
	}

	protected void save() {
		try {
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
		}
	}

	protected void load() {
		user = UserRepository.getUserFromSession();
		if (user != null) {
			firstNameTextfield.setValue(Strings.nullToEmpty(user.getFirstName()));
			lastNameTextfield.setValue(Strings.nullToEmpty(user.getLastName()));
			emailAddress = Strings.nullToEmpty(user.getEmailAddress());
			emailAddressTextfield.setValue(emailAddress);
		}
	}

}
