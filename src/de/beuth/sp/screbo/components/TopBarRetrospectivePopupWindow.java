package de.beuth.sp.screbo.components;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import org.ektorp.DocumentNotFoundException;

import com.google.common.base.Strings;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
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
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings({"serial"})
public class TopBarRetrospectivePopupWindow extends ScreboWindow implements ScreboEventListener {
	protected final VerticalLayout currentRetrospectiveLayout = new VerticalLayout();
	protected final VerticalLayout myRetrospectivesLayout = new VerticalLayout();
	protected final User user = UserRepository.getUserFromSession();
	protected final RetrospectiveRepository retrospectiveRepository = ScreboServlet.getRetrospectiveRepository();
	protected final TextField currentRetrospectiveTitleTextField = new TextField();
	protected final DateField currentRetrospectiveDateField = new DateField();
	protected final ValueChangeListener currentRetrospectiveDateValueChangedListener = new ValueChangeListener() {

		@Override
		public void valueChange(ValueChangeEvent event) {
			Date date = currentRetrospectiveDateField.getValue();
			if (date != null) {
				ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
				if (currentRetrospective != null) {
					Retrospective retrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
					retrospective.setDateOfRetrospective(zonedDateTime);
					ScreboServlet.getRetrospectiveRepository().update(retrospective);
				}
			}
		}
	};
	protected final TextChangeListener currentRetrospectiveTitleTextChangedListener = new TextChangeListener() {

		@Override
		public void textChange(TextChangeEvent event) {
			String text = event.getText();
			if (!Strings.isNullOrEmpty(text) && currentRetrospective != null) {
				Retrospective retrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
				retrospective.setTitle(text);
				ScreboServlet.getRetrospectiveRepository().update(retrospective);
			}

		}
	};

	protected CreateRetrospectiveWindow createRetrospectiveWindow;
	protected SharingWindow sharingWindow;
	protected Retrospective currentRetrospective;

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

	public TopBarRetrospectivePopupWindow(ScreboUI screboUI) {
		super(screboUI);

		setClosable(false);
		setResizable(false);
		setDraggable(false);
		setPositionY(40);
		setWidth("312px");
		myRetrospectivesLayout.setSizeFull();

		currentRetrospectiveLayout.setStyleName("currentRetrospectiveLayout");
		myRetrospectivesLayout.setStyleName("myRetrospectivesLayout");

		currentRetrospectiveDateField.setRangeStart(Date.from(ZonedDateTime.now(user.getTimeZoneId()).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant()));
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

			setTitle();
			setDate();

			Label titleLabel = new Label("Title:");
			titleLabel.setStyleName("currentRetrospectiveLabel");
			HorizontalLayout editTitleLine = new HorizontalLayout(titleLabel, currentRetrospectiveTitleTextField);
			editTitleLine.setStyleName("currentRetrospectiveLine");
			editTitleLine.setComponentAlignment(titleLabel, Alignment.MIDDLE_LEFT);
			currentRetrospectiveLayout.addComponent(editTitleLine);

			Label dateOfRetrospectiveLabel = new Label("Date:");
			dateOfRetrospectiveLabel.setStyleName("currentRetrospectiveLabel");
			HorizontalLayout dateOfRetrospectiveLine = new HorizontalLayout(dateOfRetrospectiveLabel, currentRetrospectiveDateField);
			dateOfRetrospectiveLine.setStyleName("currentRetrospectiveLine");
			dateOfRetrospectiveLine.setComponentAlignment(dateOfRetrospectiveLabel, Alignment.MIDDLE_LEFT);
			currentRetrospectiveLayout.addComponent(dateOfRetrospectiveLine);

			Button addRemoveUsersFromCurrentRetrospectiveButton = new Button("Share this retrospective");
			addRemoveUsersFromCurrentRetrospectiveButton.setEnabled(currentRetrospective.isEditableByUser(user));
			currentRetrospectiveLayout.addComponent(addRemoveUsersFromCurrentRetrospectiveButton);
			addRemoveUsersFromCurrentRetrospectiveButton.addClickListener(event -> {
				if (sharingWindow == null) {
					sharingWindow = new SharingWindow(screboUI, currentRetrospective);
					sharingWindow.addCloseListener(event2 -> {
						sharingWindow = null;
					});
					screboUI.addWindow(sharingWindow);
				} else {
					sharingWindow.close();
				}
			});

			Button startTeamtRetrospectiveButton = new Button("Start team retrospective");
			startTeamtRetrospectiveButton.addClickListener(event -> {
				currentRetrospective.setTeamRetroStarted(true);
				ScreboServlet.getRetrospectiveRepository().update(currentRetrospective);
				close();
			});
			if (!currentRetrospective.isTeamRetroStarted()) {
				currentRetrospectiveLayout.addComponent(startTeamtRetrospectiveButton);
			}

			Button closeRetrospectiveButton = new Button("Close this retrospective");
			closeRetrospectiveButton.addClickListener(event -> {
				screboUI.getEventBus().fireEvent(new RequestCloseRetrospectiveEvent(currentRetrospective));
				close();
			});
			currentRetrospectiveLayout.addComponent(closeRetrospectiveButton);
		}
	}

	private void setTitle() {
		currentRetrospectiveTitleTextField.removeTextChangeListener(currentRetrospectiveTitleTextChangedListener);
		currentRetrospectiveTitleTextField.setValue(currentRetrospective.getTitle());
		currentRetrospectiveTitleTextField.addTextChangeListener(currentRetrospectiveTitleTextChangedListener);
	}

	private void setDate() {
		currentRetrospectiveDateField.removeValueChangeListener(currentRetrospectiveDateValueChangedListener);
		currentRetrospectiveDateField.setValue(Date.from(currentRetrospective.getDateOfRetrospective().toInstant()));
		currentRetrospectiveDateField.addValueChangeListener(currentRetrospectiveDateValueChangedListener);
	}

	protected void fillMyRetrospectivesLayout() {
		myRetrospectivesLayout.removeAllComponents();

		Label myRetrospectivesLabel = new Label("My Retrospectives");
		myRetrospectivesLabel.setStyleName("sectionLabel");
		myRetrospectivesLayout.addComponent(myRetrospectivesLabel);

		for (Retrospective retrospective : retrospectiveRepository.getVisibleByUser(user.getId())) {
			OpenRetrospectiveButton openRetrospectiveButton = new OpenRetrospectiveButton(retrospective);
			openRetrospectiveButton.setStyleName("openRetrospectiveButton");
			openRetrospectiveButton.addClickListener(event -> {
				close();
			});
			myRetrospectivesLayout.addComponent(openRetrospectiveButton);
		}

		Button createNewRetrospectiveButton = new Button("Create new retrospective");
		createNewRetrospectiveButton.addClickListener(event -> {
			if (createRetrospectiveWindow == null) {
				createRetrospectiveWindow = new CreateRetrospectiveWindow(screboUI);
				createRetrospectiveWindow.addCloseListener(event2 -> {
					createRetrospectiveWindow = null;
				});
				createRetrospectiveWindow.setPositionY(40);
				createRetrospectiveWindow.setPositionX(312);
				createRetrospectiveWindow.setWidth("230px");
				screboUI.addWindow(createRetrospectiveWindow);
			} else {
				createRetrospectiveWindow.close();
			}
		});
		myRetrospectivesLayout.addComponent(createNewRetrospectiveButton);
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			close();
		} else if (screboEvent instanceof DatabaseObjectChangedEvent) {
			if (((DatabaseObjectChangedEvent) screboEvent).getObjectClass().equals(Retrospective.class)) { // a retrospective object was changed, we update the view
				if (currentRetrospective != null && currentRetrospective.getId().equals(((DatabaseObjectChangedEvent) screboEvent).getDocumentId())) {
					try {
						currentRetrospective = ScreboServlet.getRetrospectiveRepository().get(currentRetrospective.getId());
						if (currentRetrospective == null) {
							fillCurrentRetrospectiveLayout();
						} else {
							setTitle();
							setDate();
						}
					} catch (DocumentNotFoundException e) {
					}
				}
				fillMyRetrospectivesLayout();
			}
		}
	}

}
