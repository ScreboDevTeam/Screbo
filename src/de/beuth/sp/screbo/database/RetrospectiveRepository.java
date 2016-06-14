package de.beuth.sp.screbo.database;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;

public class RetrospectiveRepository extends MyCouchDbRepositorySupport<Retrospective> {
	public RetrospectiveRepository(CouchDbConnector db) {
		super(Retrospective.class, db, true);
		initStandardDesignDocument(); // This has to be called for custom view creation, 2 hours of my life span!
	}

	@View(name = "by_visibleByUserId", map = "function(doc) { for (var userName in doc.rights) { var right=doc.rights[userName]; if(right=='EDIT' || right=='VIEW') emit(userName, doc._id); } }")
	public List<Retrospective> getVisibleByUser(String userId) {
		// TODO modify the view to only return the board title and id -> more performance
		return queryView("by_visibleByUserId", userId);
	}
}
