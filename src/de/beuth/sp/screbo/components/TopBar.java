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
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveClosedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveOpenedEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

/**
 * Class managing the bar on top of the page.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class TopBar extends HorizontalLayout implements ScreboEventListener {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;
	protected final Button retrospectiveButton = new Button("retrospectives");
	protected final Button userButton = new Button("user");
	protected TopBarRetrospectivePopupWindow retrospectiveSelectionWindow;
	protected TopBarUserPopupWindow userSelectionWindow;
	protected String boardsButtonShowsRetrospectiveId;

	public TopBar(final ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;

		setWidth("100%");
		setHeight("40px");
		setStyleName("topBar", true);

		retrospectiveButton.addClickListener(event -> {
			logger.debug("boardsButton clicked");
			if (userSelectionWindow != null) {
				userSelectionWindow.close();
			}
			if (retrospectiveSelectionWindow == null) {
				retrospectiveSelectionWindow = new TopBarRetrospectivePopupWindow(screboUI);
				retrospectiveSelectionWindow.addCloseListener(event2 -> {
					retrospectiveSelectionWindow = null;
				});
				screboUI.addWindow(retrospectiveSelectionWindow);
			} else {
				retrospectiveSelectionWindow.close();
			}
		});
		addComponent(retrospectiveButton);
		setComponentAlignment(retrospectiveButton, Alignment.MIDDLE_LEFT);

		Image logoImage = new Image();
		logoImage.setStyleName("logo");
		logoImage.setSource(new ThemeResource("images/logo_title.png"));
		logoImage.setHeight("30px");
		addComponent(logoImage);
		setComponentAlignment(logoImage, Alignment.MIDDLE_CENTER);

		userButton.setCaption("user");
		userButton.addClickListener(event -> {
			logger.debug("boardsButton clicked");
			if (retrospectiveSelectionWindow != null) {
				retrospectiveSelectionWindow.close();
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
		updateButtons();
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent || (screboEvent instanceof DatabaseObjectChangedEvent && ((DatabaseObjectChangedEvent) screboEvent).getObjectClass().equals(User.class))) {
			updateButtons();
		} else if (screboEvent instanceof RetrospectiveOpenedEvent) {
			Retrospective retrospective = ((RetrospectiveOpenedEvent) screboEvent).getRetrospective();
			boardsButtonShowsRetrospectiveId = retrospective.getId();
			retrospectiveButton.setCaption("retrospective: " + retrospective.getTitle());
		} else if (screboEvent instanceof RetrospectiveClosedEvent) {
			Retrospective retrospective = ((RetrospectiveClosedEvent) screboEvent).getRetrospective();
			if (Objects.equals(boardsButtonShowsRetrospectiveId, retrospective.getId())) {
				boardsButtonShowsRetrospectiveId = null;
				retrospectiveButton.setCaption("retrospectives");
			}
		}
	}

	protected void updateButtons() {
		User user = UserRepository.getUserFromSession();
		if (user == null) {
			retrospectiveButton.setVisible(false);
			userButton.setVisible(false);
		} else {
			userButton.setCaption(user.getDisplayName());
			retrospectiveButton.setVisible(true);
			userButton.setVisible(true);
		}
	}
}
