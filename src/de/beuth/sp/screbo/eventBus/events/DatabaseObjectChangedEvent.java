package de.beuth.sp.screbo.eventBus.events;

/**
 * Event, which is populated after an object is changed in the database.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class DatabaseObjectChangedEvent extends ScreboEvent {
	protected Class<?> objectClass;
	protected String documentId;
	protected String revision;
	protected boolean deleted;
	public DatabaseObjectChangedEvent(Class<?> objectClass, String documentId, String revision, boolean deleted) {
		super();
		this.objectClass = objectClass;
		this.documentId = documentId;
		this.revision = revision;
		this.deleted = deleted;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getDocumentId() {
		return documentId;
	}
	public String getRevision() {
		return revision;
	}
	public boolean isDeleted() {
		return deleted;
	}
	@Override
	public String toString() {
		return "DatabaseObjectChangedEvent [objectClass=" + objectClass + ", documentId=" + documentId + ", deleted=" + deleted + "]";
	}
}