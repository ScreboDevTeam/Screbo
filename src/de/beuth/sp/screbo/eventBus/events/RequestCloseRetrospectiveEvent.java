package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

@SuppressWarnings("serial")
public class RequestCloseRetrospectiveEvent extends RetrospectiveEvent {

	public RequestCloseRetrospectiveEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
