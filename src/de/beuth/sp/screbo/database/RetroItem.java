package de.beuth.sp.screbo.database;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RetroItem implements Serializable {
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

}
