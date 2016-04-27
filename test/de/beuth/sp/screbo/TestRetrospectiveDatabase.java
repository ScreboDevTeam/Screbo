package de.beuth.sp.screbo;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.common.base.Joiner;

import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.RetrospectiveRepository;
import de.beuth.sp.screbo.database.User;

public class TestRetrospectiveDatabase {
	protected static final Logger logger = LogManager.getLogger();

	@Test
	public void testRetrospective() {
		DatabaseForTestcases database = new DatabaseForTestcases();
		RetrospectiveRepository retrospectiveRepository = database.getRetrospectiveRepository();

		User myUser = new User();
		database.getUserRepository().add(myUser);

		// save it
		Retrospective oldRetrospective = new Retrospective(myUser);
		oldRetrospective.setDateOfRetrospective(ZonedDateTime.now().plusDays(5));

		retrospectiveRepository.add(oldRetrospective);

		String id = oldRetrospective.getId();

		// load
		Retrospective loadedRetrospective = retrospectiveRepository.get(id);

		assertEquals(loadedRetrospective.getDateOfRetrospective().toInstant(), oldRetrospective.getDateOfRetrospective().toInstant());

		assertEquals(Joiner.on(',').join(loadedRetrospective.getCategories()), Joiner.on(',').join(oldRetrospective.getCategories()));

		//database.tearDown();
	}

}
