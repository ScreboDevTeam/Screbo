package de.beuth.sp.screbo;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.google.gson.Gson;

import de.beuth.sp.screbo.configuration.Configuration;
import de.beuth.sp.screbo.database.RetrospectiveRepository;
import de.beuth.sp.screbo.database.ScreboObjectMapper;
import de.beuth.sp.screbo.database.UserRepository;

public class DatabaseForTestcases {
	protected static final Logger logger = LogManager.getLogger();

	protected CouchDbInstance dbInstance;
	protected Configuration configuration;
	protected UserRepository userRepository;
	protected RetrospectiveRepository retrospectiveRepository;

	{
		// Read configuration
		try (Reader reader = Files.newBufferedReader(Paths.get("WebContent", "WEB-INF", "configuration.json"));) {
			configuration = new Gson().fromJson(reader, Configuration.class);
		} catch (Exception e) {
			logger.error("Could not load configuration file.", e);
		}

		// Init database
		try {
			initDatabase();
		} catch (Exception e) {
			logger.error("Unable to init database", e);
		}
	}

	protected void initDatabase() throws Exception {
		HttpClient httpClient = new StdHttpClient.Builder().url(configuration.getCouchDB().getUrl()).build();

		dbInstance = new StdCouchDbInstance(httpClient, ScreboObjectMapper.getInstance());

		tearDown();

		CouchDbConnector userDatabase = new StdCouchDbConnector("test_screbo_users", dbInstance, ScreboObjectMapper.getInstance());
		userRepository = new UserRepository(userDatabase);

		CouchDbConnector retrospectiveDatabase = new StdCouchDbConnector("test_retrospectives", dbInstance, ScreboObjectMapper.getInstance());
		retrospectiveRepository = new RetrospectiveRepository(retrospectiveDatabase);
	}

	public void tearDown() {
		if (dbInstance.checkIfDbExists("test_screbo_users")) {
			dbInstance.deleteDatabase("test_screbo_users");
		}
		if (dbInstance.checkIfDbExists("test_retrospectives")) {
			dbInstance.deleteDatabase("test_retrospectives");
		}
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public RetrospectiveRepository getRetrospectiveRepository() {
		return retrospectiveRepository;
	}
}
