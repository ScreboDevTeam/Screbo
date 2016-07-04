package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

/**
 * Event, which is populated after a retrospective was closed.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RetrospectiveClosedEvent extends RetrospectiveEvent {

	public RetrospectiveClosedEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
