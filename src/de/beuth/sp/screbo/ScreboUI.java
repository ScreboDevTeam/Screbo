package de.beuth.sp.screbo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.components.TopBar;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.EventBus;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.DisplayErrorMessageEvent;
import de.beuth.sp.screbo.eventBus.events.RequestNavigateToRetrospectivesViewEvent;
import de.beuth.sp.screbo.eventBus.events.RequestOpenRetrospectiveEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveClosedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveOpenedEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.beuth.sp.screbo.views.EditAccountView;
import de.beuth.sp.screbo.views.LandingPageView;
import de.beuth.sp.screbo.views.LoginView;
import de.beuth.sp.screbo.views.RetrospectiveView;

@SuppressWarnings("serial")
@Theme("screbo")
@Push
public class ScreboUI extends UI implements ScreboEventListener {
	protected static final String SESSION_KEY_LOAD_PAGE_AFTER_LOGIN = "requestedPage";
	protected static final Logger logger = LogManager.getLogger();

	protected EventBus eventBus = new EventBus(ScreboServlet.getGlobalEventBus(), new EventBus.Synchronizer() {

		@Override
		public void synchronize(Runnable runnable) {
			access(runnable);
		}
	}); // Events in the scope of a client

	protected Navigator navigator;
	protected Retrospective currentlyOpenedRetrospective;

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
		eventBus.addEventListener(this, true);

		User user = UserRepository.getUserFromSession();
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
		navigator.addView("retrospective", new RetrospectiveView(this));
		navigator.addView("editAccount", new EditAccountView(this));
		navigator.addView("", new LandingPageView(this));
	}

	public void openLoginView() {
		navigator.navigateTo("login");
	}

	public void openEditAccountView() {
		navigator.navigateTo("editAccount");
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
		try {
			navigator.navigateTo(pageToLoadAfterLogin);
		} catch (IllegalArgumentException e) {
			logger.error("Tried to navigate to {}", pageToLoadAfterLogin);
			navigator.navigateTo("/");
		}
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof RequestOpenRetrospectiveEvent) {
			navigator.navigateTo("retrospective/" + ((RequestOpenRetrospectiveEvent) screboEvent).getRetrospective().getId());
		} else if (screboEvent instanceof RequestNavigateToRetrospectivesViewEvent) {
			navigator.navigateTo("/");
		} else if (screboEvent instanceof RetrospectiveOpenedEvent) {
			currentlyOpenedRetrospective = ((RetrospectiveOpenedEvent) screboEvent).getRetrospective();
		} else if (screboEvent instanceof RetrospectiveClosedEvent) {
			currentlyOpenedRetrospective = null;
		}
	}

	public Retrospective getCurrentlyOpenedRetrospective() {
		return currentlyOpenedRetrospective;
	}

	public void fireCouldNotWriteToDatabaseEvent(Exception e) {
		eventBus.fireEvent(new DisplayErrorMessageEvent("Could not write to database.", e));
	}

}