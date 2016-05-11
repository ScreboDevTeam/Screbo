package de.beuth.sp.screbo.database;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cluster implements Serializable, IDInterface {

	protected String id;
	protected IDList<RetroItem> retroItems = new IDList<>();

	public IDList<RetroItem> getRetroItems() {
		return retroItems;
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
