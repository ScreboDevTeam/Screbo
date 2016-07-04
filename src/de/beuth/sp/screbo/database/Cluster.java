package de.beuth.sp.screbo.database;

import java.io.Serializable;

/**
 * POJO containing one or more postings which are grouped together.
 * This object is serialized and deserialized when writing or reading to/from the database.
 * 
 * @author volker.gronau
 *
 */
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
