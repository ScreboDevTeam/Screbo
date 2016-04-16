package de.beuth.sp.screbo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.components.TopBar;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.views.BoardView;
import de.beuth.sp.screbo.views.BoardsView;
import de.beuth.sp.screbo.views.LoginView;

@SuppressWarnings("serial")
@Theme("screbo")
public class ScreboUI extends UI {
	protected static final String SESSION_KEY_LOAD_PAGE_AFTER_LOGIN = "requestedPage";
	protected static final Logger logger = LogManager.getLogger();

	protected EventBus eventBus = new EventBus();
	protected Navigator navigator;

	public String getPageToLoadAfterLogin() {
		String result = (String) VaadinSession.getCurrent().getSession().getAttribute(SESSION_KEY_LOAD_PAGE_AFTER_LOGIN);
		if (result == null) {
			return "";
		}
		if (result.startsWith("!")) {
			result = result.substring(1);
		}
		return result;
	}

	public String getSessionId() {
		return VaadinSession.getCurrent().getSession().getId();
	}

	@Override
	protected void init(VaadinRequest request) {
		User user = User.getUserFromSession();
		logger.info("Page (re)loaded for sessionID: {}, user: {}", getSessionId(), user);

		getPage().setTitle("Screbo");

		VerticalLayout mainLayout = new VerticalLayout();
		TopBar topBar = new TopBar(this);
		mainLayout.addComponent(topBar);
		mainLayout.setExpandRatio(topBar, 0);
		mainLayout.setComponentAlignment(topBar, Alignment.TOP_RIGHT);
		VerticalLayout contentLayout = new VerticalLayout();
		contentLayout.setSizeFull();
		mainLayout.addComponent(contentLayout);
		mainLayout.setExpandRatio(contentLayout, 1);

		mainLayout.setSizeFull();
		setContent(mainLayout);

		if (user == null) {
			String fragment = getPage().getUriFragment();
			VaadinSession.getCurrent().getSession().setAttribute(SESSION_KEY_LOAD_PAGE_AFTER_LOGIN, fragment);
			getPage().setUriFragment("!login");
			//navigator.navigateTo("login");
		}

		// Create a navigator to control the views
		navigator = new Navigator(this, contentLayout);

		// Create and register the views
		navigator.addView("login", new LoginView(this));
		navigator.addView("board", new BoardView(this));
		navigator.addView("", new BoardsView(this));
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}

	public void afterLogin() {
		String pageToLoadAfterLogin = getPageToLoadAfterLogin();
		if ("login".equals(pageToLoadAfterLogin)) {
			pageToLoadAfterLogin = "";
		}
		navigator.navigateTo(pageToLoadAfterLogin);
	}

	public EventBus getEventBus() {
		return eventBus;
	}

}