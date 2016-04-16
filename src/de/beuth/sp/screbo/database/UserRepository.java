package de.beuth.sp.screbo.database;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class UserRepository extends CouchDbRepositorySupport<User> {

	public UserRepository(CouchDbConnector db) {
		super(User.class, db);
	}

}
