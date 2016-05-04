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
import de.beuth.sp.screbo.eventBus.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.ScreboEvent;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;

@SuppressWarnings({"serial"})
public class RetrospectiveSelectionWindow extends ScreboWindow implements ScreboEventListener {
	protected final VerticalLayout verticalLayout = new VerticalLayout();
	protected CreateRetrospectiveWindow createRetrospectiveWindow;
	protected User user = UserRepository.getUserFromSession();
	protected RetrospectiveRepository retrospectiveRepository = ScreboServlet.getRetrospectiveRepository();

	public RetrospectiveSelectionWindow(ScreboUI screboUI) {
		super(screboUI);

		setClosable(false);
		setResizable(false);
		setDraggable(false);
		setPositionY(40);
		setWidth("280px");
		setHeight("380px");
		setStyleName("boardSelectionWindow");

		setContent(verticalLayout);
		fillVerticalLayout();
		setVisible(true);

		screboUI.getEventBus().addEventListener(this, true);
	}

	protected synchronized void fillVerticalLayout() {

		verticalLayout.removeAllComponents();
		Label myBoards = new Label("My Retrospectives");
		verticalLayout.addComponent(myBoards);

		for (Retrospective retrospective : retrospectiveRepository.getVisibleByUser(user.getId())) {
			Button openRetrospectiveButton = new Button(retrospective.getTitle());
			openRetrospectiveButton.setStyleName("openRetrospectiveButton");
			openRetrospectiveButton.addClickListener(event -> {
				close();
			});
			verticalLayout.addComponent(openRetrospectiveButton);
		}

		Button createNewRetrospectiveButton = new Button("Create new");
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
