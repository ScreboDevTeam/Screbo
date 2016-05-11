package de.beuth.sp.screbo.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;

public class MyCouchDbRepositorySupport<T> extends CouchDbRepositorySupport<T> {
	protected static final Logger logger = LogManager.getLogger();
	public static interface TransformationRunnable<T> {
		public void applyChanges(T entity) throws Exception;
	}

	public MyCouchDbRepositorySupport(Class<T> type, CouchDbConnector db, boolean createIfNotExists) {
		super(type, db, createIfNotExists);
	}

	public MyCouchDbRepositorySupport(Class<T> type, CouchDbConnector db, String designDocName) {
		super(type, db, designDocName);
	}

	public MyCouchDbRepositorySupport(Class<T> type, CouchDbConnector db) {
		super(type, db);
	}

	/**
	 * We allow caching.
	 */
	@Override
	protected ViewQuery createQuery(String viewName) {
		return super.createQuery(viewName).cacheOk(true);
	}

	/**
	 * We work in an async environment. It can always be that somebody else modified the object between our read and
	 * this write attempt. Gladly couchDB notifies us about this, this way we can reload the object from the database and
	 * apply the transformation on the new object.
	 * 
	 */
	public void update(T entity, TransformationRunnable<T> changeRunnable) throws Exception {
		update(entity, changeRunnable, System.currentTimeMillis() + 10_000);
	}

	private void update(T entity, TransformationRunnable<T> changeRunnable, long retryUntil) throws Exception {
		changeRunnable.applyChanges(entity);
		try {
			update(entity); // the actual update
		} catch (UpdateConflictException e) { // somebody was faster
			logger.warn("Got UpdateConflictException", e);
			if (retryUntil < System.currentTimeMillis()) {
				update(entity, changeRunnable, retryUntil);
			} else {
				throw e;
			}
		}

	}

}
