package de.beuth.sp.screbo.eventBus;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class EventBus implements Serializable, ScreboEventListener {
	protected static final Logger logger = LogManager.getLogger();

	public static class UserChangedEvent extends ScreboEvent {
	}

	protected List<Object> eventListeners = Lists.newArrayList();

	public EventBus() {
		super();
	}

	/**
	 * @param parentEventBus
	 *            Every message from parentBus is received by this bus too.
	 */
	public EventBus(EventBus parentEventBus) {
		super();
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
	 * Called by parent EventBus.
	 */
	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		fireEvent(screboEvent);
	}
}
