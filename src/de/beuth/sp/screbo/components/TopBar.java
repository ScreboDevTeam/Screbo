package de.beuth.sp.screbo.components;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveClosedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveOpenedEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings("serial")
public class TopBar extends HorizontalLayout implements ScreboEventListener {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;
	protected final Button boardsButton = new Button("Retrospectives");
	protected final Button userButton = new Button("User");
	protected TopBarRetrospectivePopupWindow boardSelectionWindow;
	protected TopBarUserPopupWindow userSelectionWindow;
	protected String boardsButtonShowsRetrospectiveId;

	public TopBar(final ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;

		setWidth("100%");
		setHeight("40px");
		setStyleName("topBar", true);

		boardsButton.addClickListener(event -> {
			logger.debug("boardsButton clicked");
			if (userSelectionWindow != null) {
				userSelectionWindow.close();
			}
			if (boardSelectionWindow == null) {
				boardSelectionWindow = new TopBarRetrospectivePopupWindow(screboUI);
				boardSelectionWindow.addCloseListener(event2 -> {
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
		userButton.addClickListener(event -> {
			logger.debug("boardsButton clicked");
			if (boardSelectionWindow != null) {
				boardSelectionWindow.close();
			}
			if (userSelectionWindow == null) {
				userSelectionWindow = new TopBarUserPopupWindow(screboUI);
				userSelectionWindow.addCloseListener(event2 -> {
					userSelectionWindow = null;
				});
				screboUI.addWindow(userSelectionWindow);
			} else {
				userSelectionWindow.close();
			}
		});
		addComponent(userButton);
		setComponentAlignment(userButton, Alignment.MIDDLE_RIGHT);

		screboUI.getEventBus().addEventListener(this, true);
		setUserButtonText();
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			setUserButtonText();
		} else if (screboEvent instanceof RetrospectiveOpenedEvent) {
			Retrospective retrospective = ((RetrospectiveOpenedEvent) screboEvent).getRetrospective();
			boardsButtonShowsRetrospectiveId = retrospective.getId();
			boardsButton.setCaption("Retrospective: " + retrospective.getTitle());
		} else if (screboEvent instanceof RetrospectiveClosedEvent) {
			Retrospective retrospective = ((RetrospectiveClosedEvent) screboEvent).getRetrospective();
			if (Objects.equals(boardsButtonShowsRetrospectiveId, retrospective.getId())) {
				boardsButtonShowsRetrospectiveId = null;
				boardsButton.setCaption("Retrospectives");
			}
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
