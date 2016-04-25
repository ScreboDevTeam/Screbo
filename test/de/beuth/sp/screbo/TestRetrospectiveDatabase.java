package de.beuth.sp.screbo;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.RetrospectiveRepository;
import de.beuth.sp.screbo.database.User;

public class TestRetrospectiveDatabase {
	protected static final Logger logger = LogManager.getLogger();

	@Test
	public void testRetrospective() {
		TestDatabase testDatabase = new TestDatabase();
		RetrospectiveRepository retrospectiveRepository = testDatabase.getRetrospectiveRepository();

		User myUser = new User();
		testDatabase.getUserRepository().add(myUser);

		ZonedDateTime dateOfRetrospective = ZonedDateTime.now().plusDays(5);

		// save it
		Retrospective retrospective = new Retrospective(myUser);
		retrospective.setDateOfRetrospective(dateOfRetrospective);

		retrospectiveRepository.add(retrospective);

		String id = retrospective.getId();

		// load
		retrospective = retrospectiveRepository.get(id);

		assertEquals(retrospective.getDateOfRetrospective().toInstant(), dateOfRetrospective.toInstant());

		//testDatabase.tearDown();
	}

}
