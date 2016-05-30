package de.beuth.sp.screbo.components;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import com.google.common.base.Strings;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;

@SuppressWarnings("serial")
public class CreateRetrospectiveWindow extends ScreboWindow { // TODO design, members section
	protected User user = UserRepository.getUserFromSession();
	protected final TextField titleTextField = new TextField();
	protected final Button createButton = new Button("Create retrospective");
	protected DateField retrospectiveDateField = new DateField();
	protected ZonedDateTime dateOfRetrospective;
	protected String title;

	protected CreateRetrospectiveWindow(ScreboUI screboUI) {
		super(screboUI);
		setCaption("Create Retrospective");
		setResizable(false);
		setModal(true);

		titleTextField.setImmediate(true);
		titleTextField.addTextChangeListener(e -> {
			title = e.getText();
			setCreateButtonStatus();
		});

		dateOfRetrospective = ZonedDateTime.now(user.getTimeZoneId()).plusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
		retrospectiveDateField.setImmediate(true);
		retrospectiveDateField.setRangeStart(Date.from(ZonedDateTime.now(user.getTimeZoneId()).toInstant()));
		retrospectiveDateField.setLocale(Locale.forLanguageTag(user.getLocale()));
		retrospectiveDateField.setValue(Date.from(dateOfRetrospective.toInstant())); // we have to convert to date, because the component uses the old date api
		retrospectiveDateField.addValueChangeListener(e -> {
			dateOfRetrospective = ZonedDateTime.ofInstant(retrospectiveDateField.getValue().toInstant(), user.getTimeZoneId()).withHour(0).withMinute(0).withSecond(0).withNano(0);
		});

		createButton.addClickListener(e -> {
			createRetrospective();
		});

		setCreateButtonStatus();
		VerticalLayout verticalLayout = new VerticalLayout(new Label("Title:"), titleTextField, new Label("Date:"), retrospectiveDateField, new Label("Members"), createButton);
		setContent(verticalLayout);
		titleTextField.focus();
	}

	protected void setCreateButtonStatus() {
		createButton.setEnabled(!Strings.isNullOrEmpty(title));
	}

	protected void createRetrospective() {
		if (!Strings.isNullOrEmpty(title)) {
			Retrospective retrospective = new Retrospective(title, user, dateOfRetrospective);
			ScreboServlet.getRetrospectiveRepository().add(retrospective);
			close();
		}
	}

}
