package de.beuth.sp.screbo.database;

import java.time.ZonedDateTime;

@SuppressWarnings("serial")
public class Activity {
	public static enum ActivityPriority {
		IMPORTANT, NORMAL, UNIMPORTANT
	}

	protected String retrospectiveId;
	protected String realizationByUserId;

	protected String description;
	protected boolean realized;
	protected ZonedDateTime dateOfLatestRealization;
	protected ActivityPriority priority;
}
