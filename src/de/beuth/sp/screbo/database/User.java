package de.beuth.sp.screbo.database;

import java.util.List;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.vaadin.server.VaadinSession;

@SuppressWarnings("serial")
@JsonIgnoreProperties({"displayName", "emailAddress"})
public class User extends CouchDbDocument {
	protected final static String SESSION_USER_PROPERTY = "currentUser";

	public static enum UserRole {
		TeamMember, ScrumMaster, ProductOwner, StakeHolder
	}

	protected String password;
	protected String lastName;
	protected String firstName;
	//protected String emailAddress; saved in ID
	protected UserRole userRole;

	public static User getUserFromSession() {
		return (User) VaadinSession.getCurrent().getAttribute(SESSION_USER_PROPERTY);
	}

	public void setAsSessionUser() {
		VaadinSession.getCurrent().setAttribute(SESSION_USER_PROPERTY, this);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmailAddress() {
		return getId();
	}

	public void setEmailAddress(String emailAddress) {
		this.setId(emailAddress);
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "User [" + getDisplayName() + "]";
	}

	public String getDisplayName() {
		List<String> result = Lists.newArrayList();
		if (lastName != null) {
			result.add(lastName);
		}
		if (firstName != null) {
			result.add(firstName);
		}
		if (result.size() == 0) {
			result.add(getEmailAddress());
		}
		return Joiner.on(' ').join(result);
	}

}
