package de.beuth.sp.screbo.database;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vaadin.server.VaadinSession;

@SuppressWarnings("serial")
@JsonIgnoreProperties({"userName"})
public class User extends CouchDbDocument {
	protected final static String SESSION_USER_PROPERTY = "currentUser";

	protected String password;

	public static User getUserFromSession() {
		return (User) VaadinSession.getCurrent().getAttribute(SESSION_USER_PROPERTY);
	}

	public void setAsSessionUser() {
		VaadinSession.getCurrent().setAttribute(SESSION_USER_PROPERTY, this);
	}

	public String getUserName() {
		return getId();
	}

	public void setUserName(String userName) {
		setId(userName);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [" + getUserName() + "]";
	}

}
