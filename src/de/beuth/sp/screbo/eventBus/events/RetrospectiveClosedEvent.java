package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

@SuppressWarnings("serial")
public class RetrospectiveClosedEvent extends RetrospectiveEvent {

	public RetrospectiveClosedEvent(Retrospective retrospective) {
		super(retrospective);
	}

}
