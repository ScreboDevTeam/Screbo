package de.beuth.sp.screbo.database;

import java.io.Serializable;
import java.time.ZonedDateTime;

@SuppressWarnings("serial")
public class Activity implements Serializable, IDInterface {
	public static enum ActivityPriority {
		HIGH, NORMAL, LOW
	}

	protected String id;
	protected String realizationByUserId;
	protected String description;
	protected boolean realized;
	protected ZonedDateTime dateOfLatestRealization;
	protected ActivityPriority priority;

	public String getRealizationByUserId() {
		return realizationByUserId;
	}
	public void setRealizationByUserId(String realizationByUserId) {
		this.realizationByUserId = realizationByUserId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRealized() {
		return realized;
	}
	public void setRealized(boolean realized) {
		this.realized = realized;
	}
	public ZonedDateTime getDateOfLatestRealization() {
		return dateOfLatestRealization;
	}
	public void setDateOfLatestRealization(ZonedDateTime dateOfLatestRealization) {
		this.dateOfLatestRealization = dateOfLatestRealization;
	}
	public ActivityPriority getPriority() {
		return priority;
	}
	public void setPriority(ActivityPriority priority) {
		this.priority = priority;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}

}
