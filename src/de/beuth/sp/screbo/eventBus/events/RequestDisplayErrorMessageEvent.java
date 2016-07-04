package de.beuth.sp.screbo.eventBus.events;

/**
 * An event which is sent to request a message to be displayed to the user.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RequestDisplayErrorMessageEvent extends ScreboEvent {
	protected String textToShowToUser;
	protected Exception exception;

	/**
	 * Beware, you have to i18n yourself.
	 */
	public RequestDisplayErrorMessageEvent(String textToShowToUser, Exception exception) {
		this(textToShowToUser);
		this.exception = exception;
	}

	/**
	 * Beware, you have to i18n yourself.
	 */
	public RequestDisplayErrorMessageEvent(String textToShowToUser) {
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
