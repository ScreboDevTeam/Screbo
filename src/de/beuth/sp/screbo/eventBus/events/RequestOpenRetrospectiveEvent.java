package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

@SuppressWarnings("serial")
public class RequestOpenRetrospectiveEvent extends RetrospectiveEvent {

	public RequestOpenRetrospectiveEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
