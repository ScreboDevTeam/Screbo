package de.beuth.sp.screbo.database;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

import com.vaadin.server.VaadinSession;

public class UserRepository extends MyCouchDbRepositorySupport<User> {
	protected final static String SESSION_USER_PROPERTY = "currentUser";

	public UserRepository(CouchDbConnector db) {
		super(User.class, db, true);
		initStandardDesignDocument(); // This has to be called for custom view creation, 2 hours of my life span!
	}

	public static User getUserFromSession() {
		VaadinSession vaadinSession = VaadinSession.getCurrent();
		if (vaadinSession != null) {
			return (User) vaadinSession.getAttribute(SESSION_USER_PROPERTY);
		}
		return null;
	}

	@Override
	@View(name = "myAll", map = "function(doc) {emit( null, doc._id );}")
	public List<User> getAll() {
		return queryView("myAll");
	}
}