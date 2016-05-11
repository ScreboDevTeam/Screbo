package de.beuth.sp.screbo.eventBus.events;

@SuppressWarnings("serial")
public abstract class EventSupressor extends ScreboEvent {
	protected final long timeout = System.currentTimeMillis() + 10000;

	public long getTimeout() {
		return timeout;
	}

	public abstract boolean suppresses(ScreboEvent screboEvent);

}
