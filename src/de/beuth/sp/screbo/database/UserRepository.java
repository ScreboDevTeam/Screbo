package de.beuth.sp.screbo.database;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;

import com.vaadin.server.VaadinSession;

public class UserRepository extends CouchDbRepositorySupport<User> {
	protected final static String SESSION_USER_PROPERTY = "currentUser";

	public UserRepository(CouchDbConnector db) {
		super(User.class, db, true);
		initStandardDesignDocument(); // This has to be called for custom view creation, 2 hours of my life span!
	}

	/**
	 * We allow caching.
	 */
	@Override
	protected ViewQuery createQuery(String viewName) {
		return super.createQuery(viewName).cacheOk(true);
	}

	public static User getUserFromSession() {
		VaadinSession vaadinSession = VaadinSession.getCurrent();
		if (vaadinSession != null) {
			return (User) vaadinSession.getAttribute(SESSION_USER_PROPERTY);
		}
		return null;
	}
}
