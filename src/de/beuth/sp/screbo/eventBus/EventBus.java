package de.beuth.sp.screbo.eventBus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import de.beuth.sp.screbo.eventBus.events.EventSupressor;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;

/**
 * Used to populate messages between the components.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class EventBus implements Serializable, ScreboEventListener {
	protected static final Logger logger = LogManager.getLogger();

	/**
	 * Used to synchronize events between parent EventBus and this EventBus.
	 * 
	 * @author volker.gronau
	 *
	 */
	public static interface Synchronizer {
		public void synchronize(Runnable runnable);
	}

	protected Synchronizer synchronizer;
	protected List<Object> eventListeners = Lists.newArrayList();
	protected List<EventSupressor> eventSupressors = Lists.newArrayList();

	public EventBus() {
		super();
	}

	/**
	 * @param parentEventBus
	 *            Every message from parentBus is received by this bus too.
	 */
	public EventBus(EventBus parentEventBus, Synchronizer synchronizer) {
		super();
		this.synchronizer = synchronizer;
		parentEventBus.addEventListener(this, true);
	}

	public void addEventListener(ScreboEventListener screboEventListener, boolean weakReference) {
		synchronized (eventListeners) {
			if (weakReference) {
				eventListeners.add(new WeakReference<ScreboEventListener>(screboEventListener));
			} else {
				eventListeners.add(screboEventListener);
			}
		}
	}

	public void removeEventListener(ScreboEventListener screboEventListenerToRemove) {
		synchronized (eventListeners) {
			Iterator<Object> iterator = eventListeners.iterator();
			while (iterator.hasNext()) {
				Object eventListener = iterator.next();

				if (eventListener instanceof WeakReference) {
					@SuppressWarnings("unchecked")
					ScreboEventListener screboEventListener = ((WeakReference<ScreboEventListener>) eventListener).get();
					if (screboEventListener == null) {
						iterator.remove();
					} else if (screboEventListener == screboEventListenerToRemove) {
						iterator.remove();
						return;
					}
				} else if (eventListener == screboEventListenerToRemove) {
					iterator.remove();
					return;
				}

			}
		}
	}

	public void fireEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof EventSupressor) {
			addEventSuppressor((EventSupressor) screboEvent);
			return;
		}

		// TODO: It's not very nice to copy everything for every event but even with multiplereadersinglewritemutex one has to take care of concurrent modification exception
		// TODO: let a worker thread pool call the event handlers
		List<ScreboEventListener> toFire;
		synchronized (eventListeners) {
			toFire = Lists.newArrayListWithCapacity(eventListeners.size());
			Iterator<Object> iterator = eventListeners.iterator();
			while (iterator.hasNext()) {
				Object eventListener = iterator.next();

				if (eventListener instanceof WeakReference) {
					@SuppressWarnings("unchecked")
					ScreboEventListener screboEventListener = ((WeakReference<ScreboEventListener>) eventListener).get();
					if (screboEventListener == null) {
						iterator.remove();
					} else {
						toFire.add(screboEventListener);
					}
				} else {
					toFire.add((ScreboEventListener) eventListener);
				}
			}
		}
		logger.debug("Firing event {}, informing {} listeners.", screboEvent, toFire.size());
		for (ScreboEventListener screboEventListener : toFire) {
			try {
				screboEventListener.onScreboEvent(screboEvent);
			} catch (Exception e) {
				logger.error("Error calling event listener", e);
			}
		}
	}

	/**
	 * Called by parent EventBus. We do the proper synchronization between server and client here.
	 * It is not the perfect place as the event bus forwards every message so a lot of synchronization is done.
	 * As the server event bus is only used for database changed events so far, it is okay I guess.
	 * Better would be to reimplement the bus on a subscriber basis. I have that finished in another project but it is closed source.
	 */
	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (!isSupressed(screboEvent)) {
			if (synchronizer != null) {
				synchronizer.synchronize(new Runnable() {

					@Override
					public void run() {
						fireEvent(screboEvent);
					}
				});
			} else {
				fireEvent(screboEvent);
			}
		}
	}

	private void addEventSuppressor(EventSupressor eventSupressor) {
		synchronized (eventSupressors) {
			long currentTimeMillis = System.currentTimeMillis();
			for (int index = eventSupressors.size() - 1; index >= 0; index--) {
				if (eventSupressors.get(index).getTimeout() < currentTimeMillis) {
					eventSupressors.remove(index);
				}
			}
			eventSupressors.add(eventSupressor);
		}
	}

	private boolean isSupressed(ScreboEvent screboEvent) {
		synchronized (eventSupressors) {
			for (int index = eventSupressors.size() - 1; index >= 0; index--) {
				EventSupressor eventSupressor = eventSupressors.get(index);
				if (eventSupressor.suppresses(screboEvent)) {
					eventSupressors.remove(index);
					return true;
				}
			}
		}
		return false;
	}
}
