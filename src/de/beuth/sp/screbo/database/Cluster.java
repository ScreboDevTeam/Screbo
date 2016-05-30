package de.beuth.sp.screbo.database;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cluster implements Serializable, IDInterface {

	protected String id;
	protected IDList<Posting> postings = new IDList<>();

	public IDList<Posting> getPostings() {
		return postings;
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
