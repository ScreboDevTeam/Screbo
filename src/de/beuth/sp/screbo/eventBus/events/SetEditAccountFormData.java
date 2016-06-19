package de.beuth.sp.screbo.eventBus.events;

@SuppressWarnings("serial")
public class SetEditAccountFormData extends ScreboEvent {
	protected String mailAddress;
	protected String password;

	public SetEditAccountFormData(String mailAddress, String password) {
		super();
		this.mailAddress = mailAddress;
		this.password = password;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public String getPassword() {
		return password;
	}

}
