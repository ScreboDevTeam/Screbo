package de.beuth.sp.screbo.eventBus.events;

import java.util.Objects;

import org.ektorp.support.CouchDbDocument;

/**
 * If we write to the database ourself we could update the UI ourself too.
 * This suppresses exactly one revision
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class EatDatabaseObjectChangedEvent extends EventSupressor {
	protected final Class<?> objectClass;
	protected final String documentId;
	protected final String revision;

	public EatDatabaseObjectChangedEvent(CouchDbDocument couchDbDocument) {
		super();
		objectClass = couchDbDocument.getClass();
		documentId = couchDbDocument.getId();
		revision = couchDbDocument.getRevision();
	}

	@Override
	public boolean suppresses(ScreboEvent screboEvent) {
		if (screboEvent instanceof DatabaseObjectChangedEvent) {
			DatabaseObjectChangedEvent databaseObjectChangedEvent = (DatabaseObjectChangedEvent) screboEvent;
			return Objects.equals(objectClass, databaseObjectChangedEvent.getObjectClass()) && Objects.equals(documentId, databaseObjectChangedEvent.getDocumentId()) && Objects.equals(revision, databaseObjectChangedEvent.getRevision());
		}
		return false;
	}

}
