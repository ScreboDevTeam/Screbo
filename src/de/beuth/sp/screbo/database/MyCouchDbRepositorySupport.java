package de.beuth.sp.screbo.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.CouchDbRepositorySupport;

/**
 * Superclass of RetrospectiveRepository and UserRepsitory.
 * 
 * @author volker.gronau
 *
 * @param <T>
 */
public class MyCouchDbRepositorySupport<T extends CouchDbDocument> extends CouchDbRepositorySupport<T> {
	protected static final int WRITE_TIMEOUT = 10_000;
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
	 * Returns true if the object was updated without reloading it or in other words if the passed entity still matches
	 * the database copy.
	 */
	public boolean update(T entity, TransformationRunnable<T> changeRunnable) throws Exception {
		return update(entity, changeRunnable, System.currentTimeMillis() + WRITE_TIMEOUT);
	}

	private boolean update(T entity, TransformationRunnable<T> changeRunnable, long retryUntil) throws Exception {
		changeRunnable.applyChanges(entity); // modify the entity
		try {
			update(entity); // write to database
			return true;
		} catch (UpdateConflictException e) { // somebody was faster
			logger.warn("Got UpdateConflictException", e);
			if (retryUntil < System.currentTimeMillis()) {
				entity = get(entity.getId()); // reload from database
				update(entity, changeRunnable, retryUntil); // and try again
				return false;
			} else {
				throw e;
			}
		}

	}

}
