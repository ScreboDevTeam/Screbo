package de.beuth.sp.screbo.eventBus.events;

/**
 * Superclass for all Suppress* Events.
 * Allows events to be suppressed.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public abstract class EventSupressor extends ScreboEvent {
	protected final long timeout = System.currentTimeMillis() + 10000;

	public long getTimeout() {
		return timeout;
	}

	public abstract boolean suppresses(ScreboEvent screboEvent);

}
