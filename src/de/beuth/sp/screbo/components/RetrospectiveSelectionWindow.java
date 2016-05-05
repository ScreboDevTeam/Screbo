package de.beuth.sp.screbo.components;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import org.ektorp.DocumentNotFoundException;

import com.google.common.base.Strings;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.RetrospectiveRepository;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.events.RequestCloseRetrospectiveEvent;
import de.beuth.sp.screbo.eventBus.events.RequestOpenRetrospectiveEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;

@SuppressWarnings({"serial"})
public class RetrospectiveSelectionWindow extends ScreboWindow implements ScreboEventListener {
	protected final VerticalLayout currentRetrospectiveLayout = new VerticalLayout();
	protected final VerticalLayout myRetrospectivesLayout = new VerticalLayout();
	protected CreateRetrospectiveWindow createRetrospectiveWindow;
	protected User user = UserRepository.getUserFromSession();
	protected RetrospectiveRepository retrospectiveRepository = ScreboServlet.getRetrospectiveRepository();
	protected Retrospective currentRetrospective;
	protected TextField currentRetrospectiveTitleTextField = new TextField();
	protected DateField currentRetrospectiveDateField = new DateField();

	protected class OpenRetrospectiveButton extends Button implements Button.ClickListener {
		Retrospective retrospective;

		public OpenRetrospectiveButton(Retrospective retrospective) {
			super(retrospective.getTitle());
			this.retrospective = retrospective;
			addClickListener(this);
		}

		@Override
		public void buttonClick(ClickEvent event) {
			screboUI.getEventBus().fireEvent(new RequestOpenRetrospectiveEvent(retrospective));
		}

	}

	public RetrospectiveSelectionWindow(ScreboUI screboUI) {
		super(screboUI);

		setClosable(false);
		setResizable(false);
		setDraggable(false);
		setPositionY(40);
		myRetrospectivesLayout.setWidth("280px");
		setHeight("380px");
		setStyleName("retrospectiveSelectionWindow");

		currentRetrospectiveTitleTextField.addTextChangeListener((event) -> {
			String text = event.getText();
			if (!Strings.isNullOrEmpty(text) && currentRetrospective != null) {
				Retrospective retrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
				retrospective.setTitle(text);
				ScreboServlet.getRetrospectiveRepository().update(retrospective);
			}
		});

		currentRetrospectiveDateField.addValueChangeListener((event) -> {
			Date date = currentRetrospectiveDateField.getValue();
			if (date != null) {
				ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), user.getTimeZoneId());
				if (currentRetrospective != null) {
					Retrospective retrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
					retrospective.setDateOfRetrospective(zonedDateTime);
					ScreboServlet.getRetrospectiveRepository().update(retrospective);
				}
			}
		});
		currentRetrospectiveDateField.setRangeStart(Date.from(ZonedDateTime.now(user.getTimeZoneId()).toInstant()));
		currentRetrospectiveDateField.setLocale(Locale.forLanguageTag(user.getLocale()));

		setContent(new VerticalLayout(currentRetrospectiveLayout, myRetrospectivesLayout));
		fillCurrentRetrospectiveLayout();
		fillMyRetrospectivesLayout();
		setVisible(true);

		screboUI.getEventBus().addEventListener(this, true);
	}

	protected void fillCurrentRetrospectiveLayout() {
		currentRetrospectiveLayout.removeAllComponents();

		currentRetrospective = screboUI.getCurrentlyOpenedRetrospective();
		if (currentRetrospective != null) {
			Label currentRetrospectiveLabel = new Label("Current Retrospective");
			currentRetrospectiveLabel.setStyleName("sectionLabel");
			currentRetrospectiveLayout.addComponent(currentRetrospectiveLabel);

			currentRetrospectiveTitleTextField.setValue(currentRetrospective.getTitle());

			Label titleLabel = new Label("Title:");
			titleLabel.setStyleName("currentRetrospectiveLabel");
			HorizontalLayout editTitleLine = new HorizontalLayout(titleLabel, currentRetrospectiveTitleTextField);
			editTitleLine.setStyleName("currentRetrospectiveLine");
			editTitleLine.setComponentAlignment(titleLabel, Alignment.MIDDLE_LEFT);
			currentRetrospectiveLayout.addComponent(editTitleLine);

			currentRetrospectiveDateField.setValue(Date.from(currentRetrospective.getDateOfRetrospective().toInstant()));

			Label dateOfRetrospectiveLabel = new Label("Date:");
			dateOfRetrospectiveLabel.setStyleName("currentRetrospectiveLabel");
			HorizontalLayout dateOfRetrospectiveLine = new HorizontalLayout(dateOfRetrospectiveLabel, currentRetrospectiveDateField);
			dateOfRetrospectiveLine.setStyleName("currentRetrospectiveLine");
			dateOfRetrospectiveLine.setComponentAlignment(dateOfRetrospectiveLabel, Alignment.MIDDLE_LEFT);
			currentRetrospectiveLayout.addComponent(dateOfRetrospectiveLine);

			Button addRemoveUsersFromCurrentRetrospectiveButton = new Button("Invite or remove users");
			addRemoveUsersFromCurrentRetrospectiveButton.setStyleName("currentRetrospectiveButton");
			currentRetrospectiveLayout.addComponent(addRemoveUsersFromCurrentRetrospectiveButton);

			Button closeRetrospectiveButton = new Button("Close retrospective");
			closeRetrospectiveButton.setStyleName("currentRetrospectiveButton");
			closeRetrospectiveButton.addClickListener(event -> {
				screboUI.getEventBus().fireEvent(new RequestCloseRetrospectiveEvent(currentRetrospective));
				close();
			});
			currentRetrospectiveLayout.addComponent(closeRetrospectiveButton);
		}
	}

	protected void fillMyRetrospectivesLayout() {
		myRetrospectivesLayout.removeAllComponents();

		Label myRetrospectivesLabel = new Label("My Retrospectives");
		myRetrospectivesLabel.setStyleName("sectionLabel");
		myRetrospectivesLayout.addComponent(myRetrospectivesLabel);

		Button createNewRetrospectiveButton = new Button("Create new retrospective");
		createNewRetrospectiveButton.addClickListener(event -> {
			if (createRetrospectiveWindow == null) {
				createRetrospectiveWindow = new CreateRetrospectiveWindow(screboUI);
				createRetrospectiveWindow.addCloseListener(e -> {
					createRetrospectiveWindow = null;
				});
				createRetrospectiveWindow.setPositionY(40);
				createRetrospectiveWindow.setPositionX(280);
				screboUI.addWindow(createRetrospectiveWindow);
			} else {
				createRetrospectiveWindow.close();
			}
		});
		myRetrospectivesLayout.addComponent(createNewRetrospectiveButton);

		for (Retrospective retrospective : retrospectiveRepository.getVisibleByUser(user.getId())) {
			OpenRetrospectiveButton openRetrospectiveButton = new OpenRetrospectiveButton(retrospective);
			openRetrospectiveButton.setStyleName("openRetrospectiveButton");
			openRetrospectiveButton.addClickListener(event -> {
				close();
			});
			myRetrospectivesLayout.addComponent(openRetrospectiveButton);
		}
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof DatabaseObjectChangedEvent) {
			if (((DatabaseObjectChangedEvent) screboEvent).getObjectClass().equals(Retrospective.class)) { // a retrospective object was changed, we update the view
				if (currentRetrospective != null && currentRetrospective.getId().equals(((DatabaseObjectChangedEvent) screboEvent).getDocumentId())) {
					try {
						currentRetrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
						if (currentRetrospective == null) {
							fillCurrentRetrospectiveLayout();
						} else {
							currentRetrospectiveTitleTextField.setValue(currentRetrospective.getTitle());
							currentRetrospectiveDateField.setValue(Date.from(currentRetrospective.getDateOfRetrospective().toInstant()));
						}
					} catch (DocumentNotFoundException e) {
					}
				}
				fillMyRetrospectivesLayout();
			}
		}
	}

}
