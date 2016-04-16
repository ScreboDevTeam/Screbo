package de.beuth.sp.screbo;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

@SuppressWarnings("serial")
public class EventBus implements Serializable {
	protected static final Logger logger = LogManager.getLogger();

	public static interface ScreboEventListener {
		public void onScreboEvent(ScreboEvent screboEvent);
	}

	public static class ScreboEvent {

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	public static class UserChangedEvent extends ScreboEvent {
	}

	protected List<WeakReference<ScreboEventListener>> eventListeners = Lists.newArrayList();

	public void addEventListener(ScreboEventListener screboEventListener) {
		synchronized (eventListeners) {
			eventListeners.add(new WeakReference<EventBus.ScreboEventListener>(screboEventListener));
		}
	}

	public void removeEventListener(ScreboEventListener screboEventListenerToRemove) {
		synchronized (eventListeners) {
			Iterator<WeakReference<ScreboEventListener>> iterator = eventListeners.iterator();
			while (iterator.hasNext()) {
				ScreboEventListener screboEventListener = iterator.next().get();
				if (screboEventListener == null) {
					iterator.remove();
				} else if (screboEventListener == screboEventListenerToRemove) {
					iterator.remove();
					return;
				}
			}
		}
	}

	public void fireEvent(ScreboEvent screboEvent) {
		// TODO: It's not very nice to copy everything for every event but even with multiplereadersinglewritemutex one has to take care of concurrent modification exception
		List<ScreboEventListener> toFire;
		synchronized (eventListeners) {
			toFire = Lists.newArrayListWithCapacity(eventListeners.size());
			Iterator<WeakReference<ScreboEventListener>> iterator = eventListeners.iterator();
			while (iterator.hasNext()) {
				ScreboEventListener screboEventListener = iterator.next().get();
				if (screboEventListener == null) {
					iterator.remove();
				} else {
					toFire.add(screboEventListener);
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
}
