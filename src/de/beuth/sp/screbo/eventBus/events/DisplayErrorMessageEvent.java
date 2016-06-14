package de.beuth.sp.screbo.eventBus.events;

@SuppressWarnings("serial")
public class DisplayErrorMessageEvent extends ScreboEvent {
	protected String textToShowToUser;
	protected Exception exception;

	/**
	 * Beware, you have to i18n yourself.
	 */
	public DisplayErrorMessageEvent(String textToShowToUser, Exception exception) {
		this(textToShowToUser);
		this.exception = exception;
	}

	/**
	 * Beware, you have to i18n yourself.
	 */
	public DisplayErrorMessageEvent(String textToShowToUser) {
		super();
		this.textToShowToUser = textToShowToUser;
	}

	public Exception getException() {
		return exception;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [textToShowToUser=" + textToShowToUser + (exception == null ? "" : (", exception=" + exception)) + "]";
	}

	public String getTextToShowToUser() {
		return textToShowToUser;
	}

}
