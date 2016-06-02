package de.beuth.sp.screbo.views;


import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class EditAccountView extends ScreboView {

	public EditAccountView(ScreboUI screboUI) {
		super(screboUI);
		
		Panel editProfilePanel = new Panel("edit profile");
		editProfilePanel.setSizeUndefined();
		editProfilePanel.setStyleName("edit_profile_panel");
		
		GridLayout editProfileLayout = new GridLayout(2, 7);
		editProfileLayout.setSpacing(true);
		
		Label userDataLabel = new Label("edit your data");
		userDataLabel.setStyleName("sectionLabel");
		Label changePasswordLabel = new Label("change your password");
		changePasswordLabel.setStyleName("sectionLabel");
		
		TextField firstNameTextfield = new TextField("first name");
		TextField lastNameTextfield = new TextField("last name");
		TextField emailAddressTextfield = new TextField("email address");
		emailAddressTextfield.setRequired(true);
		emailAddressTextfield.setStyleName("emailAddressTextfield");
		EmailValidator emailValidator = new EmailValidator("Please enter a valid email address");
		emailAddressTextfield.addValidator(emailValidator);
		emailAddressTextfield.setImmediate(false);
		PasswordField currentPasswordTextfield = new PasswordField("current password");
		PasswordField newPasswordTextfield = new PasswordField("new password");
		PasswordField newPasswordConfirmTextfield = new PasswordField("confirm new password");
		
		Button saveButton = new Button("save changes");
		
		editProfileLayout.addComponent(userDataLabel, 0, 0, 1, 0);
		editProfileLayout.addComponents(firstNameTextfield, lastNameTextfield);
		editProfileLayout.addComponent(emailAddressTextfield, 0, 2, 1, 2);
		editProfileLayout.addComponent(changePasswordLabel, 0, 3, 1, 3);
		editProfileLayout.addComponent(currentPasswordTextfield, 0, 4, 1, 4);
		editProfileLayout.addComponents(newPasswordTextfield, newPasswordConfirmTextfield);
		editProfileLayout.addComponent(saveButton, 0, 6, 1, 6);
		editProfileLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
				
		
		editProfilePanel.setContent(editProfileLayout);
		
		addComponent(editProfilePanel);
		
	}

}
