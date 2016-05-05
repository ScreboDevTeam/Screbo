package de.beuth.sp.screbo.eventBus.events;

@SuppressWarnings("serial")
public class DisplayErrorMessageEvent extends ScreboEvent {
	protected String textToShowToUser;
	protected Exception exception;

	/**
	 * Beware, you have to i18n yourself.
	 */
	public DisplayErrorMessageEvent(String textToShowToUser, Exception exception) {
		super();
		this.textToShowToUser = textToShowToUser;
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [textToShowToUser=" + textToShowToUser + ", exception=" + exception + "]";
	}

}
