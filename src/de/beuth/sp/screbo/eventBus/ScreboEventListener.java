package de.beuth.sp.screbo.eventBus;

import de.beuth.sp.screbo.eventBus.events.ScreboEvent;

public interface ScreboEventListener {
	public void onScreboEvent(ScreboEvent screboEvent);
}