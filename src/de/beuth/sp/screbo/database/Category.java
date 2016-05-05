package de.beuth.sp.screbo.database;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class Category implements Serializable {
	protected String name;
	protected List<Cluster> cluster = Lists.newArrayList();

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
	public List<Cluster> getCluster() {
		return cluster;
	}
	@Override
	public String toString() {
		return "Category [name=" + name + "]";
	}
}
