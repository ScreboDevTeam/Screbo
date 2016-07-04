package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

/**
 * Event, which is populated after a retrospective was opened.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RetrospectiveOpenedEvent extends RetrospectiveEvent {

	public RetrospectiveOpenedEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
