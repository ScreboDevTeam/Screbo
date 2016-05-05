package de.beuth.sp.screbo.database;

public class RetroItem {
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
