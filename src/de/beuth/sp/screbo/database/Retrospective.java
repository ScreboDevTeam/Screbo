package de.beuth.sp.screbo.database;

import java.time.ZonedDateTime;
import java.util.Map;

import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.View;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
@View(name = "all", map = "function(doc) {emit( null, doc._id )}")
public class Retrospective extends CouchDbDocument {
	public static enum Right {
		VIEW, EDIT, NONE
	}

	protected String title;
	protected String createdByUserId;

	protected boolean teamRetroStarted;

	protected Map<String, Right> rights = Maps.newHashMap();

	protected ZonedDateTime dateOfRetrospective;
	protected IDList<Category> categories = new IDList<>();
	protected IDList<Activity> activities = new IDList<>();

	/**
	 * Constructor if retrospective is created by User.
	 * We init the board as we see fit.
	 * 
	 * @param createdByUser
	 */
	public Retrospective(String title, User createdByUser, ZonedDateTime dateOfRetrospective) {
		this.title = title;
		this.createdByUserId = createdByUser.getId();
		teamRetroStarted = false;
		rights.put(createdByUserId, Right.EDIT);

		// add default categories
		categories.add(new Category("Liked"));
		categories.add(new Category("Learned"));
		categories.add(new Category("Lacked"));
		categories.add(new Category("Longed for"));

		this.dateOfRetrospective = dateOfRetrospective;
	}

	public Retrospective() {
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
	public boolean isTeamRetroStarted() {
		return teamRetroStarted;
	}
	public void setTeamRetroStarted(boolean teamRetroStarted) {
		this.teamRetroStarted = teamRetroStarted;
	}
	public ZonedDateTime getDateOfRetrospective() {
		return dateOfRetrospective;
	}
	public void setDateOfRetrospective(ZonedDateTime dateOfRetrospective) {
		this.dateOfRetrospective = dateOfRetrospective;
	}

	public Map<String, Right> getRights() {
		return rights;
	}

	public void setRights(Map<String, Right> rights) {
		this.rights = rights;
	}

	public IDList<Category> getCategories() {
		return categories;
	}
	public IDList<Activity> getActivities() {
		return activities;
	}
	public boolean isVisibleByUser(User user) {
		String userId = user.getId();
		Right right = rights.get(userId);
		return Right.EDIT.equals(right) || Right.VIEW.equals(right);
	}
	public boolean isEditableByUser(User user) {
		String userId = user.getId();
		Right right = rights.get(userId);
		return Right.EDIT.equals(right);
	}
	public Cluster getClusterFromId(String id) {
		for (Category category : categories) {
			for (Cluster cluster : category.getCluster()) {
				if (id.equals(cluster.getId())) {
					return cluster;
				}
			}
		}
		return null;
	}
}
