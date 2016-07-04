package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

/**
 * Sending this event closes the retrospective view for the given retrospective.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RequestCloseRetrospectiveEvent extends RetrospectiveEvent {

	public RequestCloseRetrospectiveEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
