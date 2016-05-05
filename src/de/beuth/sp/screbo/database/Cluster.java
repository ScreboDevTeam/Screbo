package de.beuth.sp.screbo.database;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class Cluster implements Serializable {
	protected List<RetroItem> retroItems = Lists.newArrayList();

	public List<RetroItem> getRetroItems() {
		return retroItems;
	}
}
