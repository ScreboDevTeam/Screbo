package de.beuth.sp.screbo.eventBus;

public class DatabaseObjectChangedEvent extends ScreboEvent {
	protected Class<?> objectClass;
	protected String documentId;
	protected boolean deleted;
	public DatabaseObjectChangedEvent(Class<?> objectClass, String documentId, boolean deleted) {
		super();
		this.objectClass = objectClass;
		this.documentId = documentId;
		this.deleted = deleted;
	}
	public Class<?> getObjectClass() {
		return objectClass;
	}
	public String getDocumentId() {
		return documentId;
	}
	public boolean isDeleted() {
		return deleted;
	}
	@Override
	public String toString() {
		return "DatabaseObjectChangedEvent [objectClass=" + objectClass + ", documentId=" + documentId + ", deleted=" + deleted + "]";
	}
}