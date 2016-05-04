package de.beuth.sp.screbo.database;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

public class RetrospectiveRepository extends CouchDbRepositorySupport<Retrospective> {
	public RetrospectiveRepository(CouchDbConnector db) {
		super(Retrospective.class, db, true);
		initStandardDesignDocument(); // This has to be called for custom view creation, 2 hours of my life span!
	}

	@View(name = "by_visibleByUserId", map = "function(doc) {for (index = 0; index < doc.visibleByUserIds.length; ++index) { emit(doc.visibleByUserIds[index], doc._id); } }")
	public List<Retrospective> getVisibleByUser(String userId) {
		// TODO modify the view to only return the board title and id -> more performance
		return queryView("by_visibleByUserId", userId);
	}

}
