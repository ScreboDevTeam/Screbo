package de.beuth.sp.screbo.database;

import java.time.ZonedDateTime;
import java.util.List;

import org.ektorp.support.CouchDbDocument;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class Retrospective extends CouchDbDocument {
	protected String title;
	protected String createdByUserId;
	protected List<String> visibleByUserIds = Lists.newArrayList();
	protected List<String> editableByUserIds = Lists.newArrayList();

	protected ZonedDateTime dateOfRetrospective;
	protected List<Category> categories = Lists.newArrayList();
	protected List<Activity> activities = Lists.newArrayList();

	/**
	 * Constructor if retrospective is created by User.
	 * We init the board as we see fit.
	 * 
	 * @param createdByUser
	 */
	public Retrospective(String title, User createdByUser) {
		super();
		this.title = title;
		this.createdByUserId = createdByUser.getId();
		visibleByUserIds.add(createdByUserId);
		editableByUserIds.add(createdByUserId);

		// add default categories
		categories.add(new Category("Liked"));
		categories.add(new Category("Learned"));
		categories.add(new Category("Lacked"));
		categories.add(new Category("Longed for"));
	}
	protected Retrospective() {
		super();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreatedByUserId() {
		return createdByUserId;
	}
	public void setCreatedByUserId(String createdByUserId) {
		this.createdByUserId = createdByUserId;
	}
	public ZonedDateTime getDateOfRetrospective() {
		return dateOfRetrospective;
	}
	public void setDateOfRetrospective(ZonedDateTime dateOfRetrospective) {
		this.dateOfRetrospective = dateOfRetrospective;
	}
	public List<String> getVisibleByUserIds() {
		return visibleByUserIds;
	}
	public List<String> getEditableByUserIds() {
		return editableByUserIds;
	}
	public List<Category> getCategories() {
		return categories;
	}
	public List<Activity> getActivities() {
		return activities;
	}
	public boolean isVisibleByUser(User user) {
		String userId = user.getId();
		return visibleByUserIds.contains(userId) || editableByUserIds.contains(userId);
	}
}
