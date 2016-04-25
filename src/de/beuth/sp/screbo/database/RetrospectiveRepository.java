package de.beuth.sp.screbo.database;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class RetrospectiveRepository extends CouchDbRepositorySupport<Retrospective> {
	public RetrospectiveRepository(CouchDbConnector db) {
		super(Retrospective.class, db, true);
	}
}
