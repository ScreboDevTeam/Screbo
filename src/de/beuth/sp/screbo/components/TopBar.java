package de.beuth.sp.screbo.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.EventBus.UserChangedEvent;
import de.beuth.sp.screbo.eventBus.ScreboEvent;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;

@SuppressWarnings("serial")
public class TopBar extends HorizontalLayout implements ScreboEventListener {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;
	protected final Button boardsButton = new Button("Boards");
	protected final Button userButton = new Button("User");
	protected RetrospectiveSelectionWindow boardSelectionWindow;

	public TopBar(final ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;

		setWidth("100%");
		setHeight("40px");
		setStyleName("topBar", true);

		boardsButton.addClickListener(e -> {
			logger.debug("boardsButton clicked");
			if (boardSelectionWindow == null) {
				boardSelectionWindow = new RetrospectiveSelectionWindow(screboUI);
				boardSelectionWindow.addCloseListener(event -> {
					boardSelectionWindow = null;
				});
				screboUI.addWindow(boardSelectionWindow);
			} else {
				boardSelectionWindow.close();
			}
		});
		addComponent(boardsButton);
		setComponentAlignment(boardsButton, Alignment.MIDDLE_LEFT);

		Image logoImage = new Image();
		logoImage.setStyleName("logo");
		logoImage.setSource(new ThemeResource("images/logo_title.png"));
		logoImage.setHeight("30px");
		addComponent(logoImage);
		setComponentAlignment(logoImage, Alignment.MIDDLE_CENTER);

		userButton.setCaption("User");
		addComponent(userButton);
		setComponentAlignment(userButton, Alignment.MIDDLE_RIGHT);

		screboUI.getEventBus().addEventListener(this, true);
		setUserButtonText();
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			setUserButtonText();
		}
	}

	protected void setUserButtonText() {
		User user = UserRepository.getUserFromSession();
		if (user == null) {
			boardsButton.setVisible(false);
			userButton.setVisible(false);
		} else {
			userButton.setCaption(user.getDisplayName());
			boardsButton.setVisible(true);
			userButton.setVisible(true);
		}
	}
}
