package de.beuth.sp.screbo.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
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
	protected final VerticalLayout verticalLayout = new VerticalLayout();
	protected CreateRetrospectiveWindow createRetrospectiveWindow;
	protected User user = UserRepository.getUserFromSession();
	protected RetrospectiveRepository retrospectiveRepository = ScreboServlet.getRetrospectiveRepository();

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
		setWidth("280px");
		setHeight("380px");
		setStyleName("retrospectiveSelectionWindow");

		setContent(verticalLayout);
		fillVerticalLayout();
		setVisible(true);

		screboUI.getEventBus().addEventListener(this, true);
	}

	protected void fillVerticalLayout() {

		verticalLayout.removeAllComponents();

		if (screboUI.getCurrentlyOpenedRetrospective() != null) {
			Label currentRetrospective = new Label("Current Retrospective");
			verticalLayout.addComponent(currentRetrospective);

			Button addRemoveUsersFromCurrentRetrospectiveButton = new Button("Invite or remove users");
			verticalLayout.addComponent(addRemoveUsersFromCurrentRetrospectiveButton);

			Button closeRetrospective = new Button("Close retrospective");
			closeRetrospective.addClickListener(event -> {
				screboUI.getEventBus().fireEvent(new RequestCloseRetrospectiveEvent(screboUI.getCurrentlyOpenedRetrospective()));
				close();
			});
			verticalLayout.addComponent(closeRetrospective);
		}

		verticalLayout.addComponent(new Label("-"));

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

		verticalLayout.addComponent(createNewRetrospectiveButton);

		Label myRetrospectives = new Label("My Retrospectives");
		verticalLayout.addComponent(myRetrospectives);

		for (Retrospective retrospective : retrospectiveRepository.getVisibleByUser(user.getId())) {
			OpenRetrospectiveButton openRetrospectiveButton = new OpenRetrospectiveButton(retrospective);
			openRetrospectiveButton.setStyleName("openRetrospectiveButton");
			openRetrospectiveButton.addClickListener(event -> {
				close();
			});
			verticalLayout.addComponent(openRetrospectiveButton);
		}
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof DatabaseObjectChangedEvent) {
			if (((DatabaseObjectChangedEvent) screboEvent).getObjectClass().equals(Retrospective.class)) { // a retrospective object was changed, we update the view
				fillVerticalLayout();
			}
		}
	}

}
