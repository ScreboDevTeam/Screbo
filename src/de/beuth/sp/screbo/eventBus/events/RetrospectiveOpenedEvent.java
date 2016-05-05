package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

@SuppressWarnings("serial")
public class RetrospectiveOpenedEvent extends RetrospectiveEvent {

	public RetrospectiveOpenedEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
