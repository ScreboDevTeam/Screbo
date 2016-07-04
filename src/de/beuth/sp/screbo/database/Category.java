package de.beuth.sp.screbo.database;

import java.io.Serializable;

/**
 * POJO representing a category of a retrospective.
 * This object is serialized and deserialized when writing or reading to/from the database.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class Category implements Serializable, IDInterface {
	protected String name;
	protected String id;
	protected IDList<Cluster> cluster = new IDList<>();

	protected Category(String name) {
		super();
		this.name = name;
	}
	protected Category() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public IDList<Cluster> getCluster() {
		return cluster;
	}
	@Override
	public String toString() {
		return "Category [name=" + name + "]";
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
