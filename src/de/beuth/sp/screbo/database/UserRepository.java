package de.beuth.sp.screbo.database;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.support.View;

import com.vaadin.server.VaadinSession;

/**
 * Class to access the user storage of the database.
 * 
 * @author volker.gronau
 *
 */
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

	public static void setSessionUser(User user) {
		VaadinSession.getCurrent().setAttribute(UserRepository.SESSION_USER_PROPERTY, user);
	}

	@View(name = "by_emailAddress", map = "function(doc) { emit(doc.emailAddress, doc._id); }")
	public User getByEmailAddress(String emailAddress) {
		try {
			return queryView("by_emailAddress", emailAddress).get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new DocumentNotFoundException(emailAddress);
		}
	}
}