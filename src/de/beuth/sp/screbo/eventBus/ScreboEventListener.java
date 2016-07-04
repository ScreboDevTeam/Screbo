package de.beuth.sp.screbo.eventBus;

import de.beuth.sp.screbo.eventBus.events.ScreboEvent;

/**
 * Listener for EventBus.
 * 
 * @author volker.gronau
 *
 */
public interface ScreboEventListener {
	public void onScreboEvent(ScreboEvent screboEvent);
}