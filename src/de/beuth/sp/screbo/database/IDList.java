package de.beuth.sp.screbo.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Copied from other project, list of IDInterface.
 * 
 * @author volker.gronau
 *
 * @param <E>
 */
@SuppressWarnings("serial")
public class IDList<E extends IDInterface> extends ArrayList<E> {
	public IDList() {
		super();
	}

	public IDList(Collection<? extends E> c) {
		super(c);
	}

	public IDList(int initialCapacity) {
		super(initialCapacity);
	}

	public String getNextID() {
		while (true) {
			String id = UUID.randomUUID().toString();

			if (getFromID(id) == null) {
				return id;
			}
		}
	}

	protected void setIdIfNotSet(E e) {
		if (e.getId() == null) {
			e.setId(getNextID());
		}
	}

	public E getFromID(String id) {
		for (E e : this) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public E set(int index, E element) {
		setIdIfNotSet(element);
		return super.set(index, element);
	}

	@Override
	public boolean add(E e) {
		setIdIfNotSet(e);
		return super.add(e);
	}

	@Override
	public void add(int index, E element) {
		setIdIfNotSet(element);
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			setIdIfNotSet(e);
		}
		return super.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		for (E e : c) {
			setIdIfNotSet(e);
		}
		return super.addAll(index, c);
	}

	public int removeItemWithId(String id) {
		for (int index = size() - 1; index >= 0; index--) {
			if (id.equals(get(index).getId())) {
				remove(index);
				return index;
			}
		}
		return -1;
	}
}
