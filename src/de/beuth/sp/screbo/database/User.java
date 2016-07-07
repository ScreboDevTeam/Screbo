package de.beuth.sp.screbo.database;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.View;

import com.google.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.base.Joiner;

/**
 * POJO representing a single user.
 * This object is serialized and deserialized when writing or reading to/from the database.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
@View(name = "all", map = "function(doc) {emit( null, doc._id )}")
public class User extends CouchDbDocument {
	protected String password;
	protected String lastName;
	protected String firstName;
	protected String emailAddress;
	protected ZoneId timeZoneId = TimeZone.getDefault().toZoneId();
	protected String locale = Locale.getDefault().toLanguageTag();

	public ZoneId getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(ZoneId timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
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
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "User [" + getDisplayName() + "]";
	}

	public String getDisplayNameAndEmail() {
		List<String> result = Lists.newArrayList();
		if (firstName != null) {
			result.add(firstName);
		}
		if (lastName != null) {
			result.add(lastName);
		}
		if (result.size() == 0) {
			result.add(getEmailAddress());
		} else {
			result.add("(" + getEmailAddress() + ")");
		}
		return Joiner.on(' ').join(result);
	}

	public String getDisplayName() {
		List<String> result = Lists.newArrayList();
		if (firstName != null) {
			result.add(firstName);
		}
		if (lastName != null) {
			result.add(lastName);
		}
		if (result.size() == 0) {
			result.add(getEmailAddress());
		}
		return Joiner.on(' ').join(result);
	}

}
