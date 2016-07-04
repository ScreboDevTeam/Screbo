package de.beuth.sp.screbo.eventBus.events;

import de.beuth.sp.screbo.database.Retrospective;

/**
 * Superclass for all retrospective based events.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RetrospectiveEvent extends ScreboEvent {
	protected Retrospective retrospective;

	public RetrospectiveEvent(Retrospective retrospective) {
		super();
		this.retrospective = retrospective;
	}

	public Retrospective getRetrospective() {
		return retrospective;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [retrospective=" + retrospective + "]";
	}
}
