package de.beuth.sp.screbo.database;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RetroItem implements Serializable, IDInterface {
	protected String id;
	protected String title;

	public RetroItem(String title) {
		super();
		this.title = title;
	}
	public RetroItem() {
		super();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
