package de.beuth.sp.screbo.eventBus.events;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ScreboEvent implements Serializable {
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
