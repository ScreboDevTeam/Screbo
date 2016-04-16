package de.beuth.sp.screbo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import de.beuth.sp.screbo.views.BoardView;
import de.beuth.sp.screbo.views.LoginView;

@SuppressWarnings("serial")
@Theme("screbo")
public class ScreboUI extends UI {
	protected static final Logger logger = LogManager.getLogger();

	protected Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {
		logger.info("Page (re)loaded for {}", VaadinSession.getCurrent().getSession().getId());

		getPage().setTitle("Screbo");

		// Create a navigator to control the views
		navigator = new Navigator(this, this);

		// Create and register the views
		navigator.addView("", new LoginView());
		navigator.addView("board", new BoardView());
	}

}