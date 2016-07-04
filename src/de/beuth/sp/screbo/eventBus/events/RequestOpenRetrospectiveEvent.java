package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

/**
 * Sending this event results in the given retrospective loaded.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RequestOpenRetrospectiveEvent extends RetrospectiveEvent {

	public RequestOpenRetrospectiveEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
