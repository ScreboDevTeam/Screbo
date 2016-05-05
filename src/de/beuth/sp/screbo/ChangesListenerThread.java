package de.beuth.sp.screbo;

import org.ektorp.CouchDbConnector;
import org.ektorp.changes.ChangesCommand;
import org.ektorp.changes.ChangesFeed;
import org.ektorp.changes.DocumentChange;

import de.beuth.sp.screbo.eventBus.EventBus;
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;

/**
 * We not only synchronize in our own instance but on the database itself.
 * This way there could be several application servers serving our website (and several CouchDB instances
 * as CouchDB is built for replication).
 * 
 * @author volker.gronau
 *
 */
public class ChangesListenerThread extends Thread {
	protected CouchDbConnector couchDbConnector;
	protected EventBus eventBusToPostChangedTo;
	protected Class<?> changedClass;

	protected ChangesListenerThread(EventBus eventBusToPostChangedTo, Class<?> changedClass, CouchDbConnector couchDbConnector) {
		super("DatabaseChangesListenerThread");
		this.eventBusToPostChangedTo = eventBusToPostChangedTo;
		this.changedClass = changedClass;
		this.couchDbConnector = couchDbConnector;
	}

	@Override
	public void run() {
		ChangesCommand cmd = new ChangesCommand.Builder().build();

		ChangesFeed feed = couchDbConnector.changesFeed(cmd);

		try {
			while (feed.isAlive()) {
				DocumentChange change = feed.next();
				eventBusToPostChangedTo.fireEvent(new DatabaseObjectChangedEvent(changedClass, change.getId(), change.isDeleted()));

			}
		} catch (InterruptedException e) {
			feed.cancel();
		}
	}

}
