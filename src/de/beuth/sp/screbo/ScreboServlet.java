package de.beuth.sp.screbo;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import com.google.gson.Gson;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import de.beuth.sp.screbo.configuration.Configuration;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.RetrospectiveRepository;
import de.beuth.sp.screbo.database.ScreboObjectMapper;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.EventBus;

/**
 * Represents the server side. Singleton.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1)
@VaadinServletConfiguration(productionMode = false, ui = ScreboUI.class, widgetset = "de.beuth.sp.screbo.widgetset.ScreboWidgetset")
public class ScreboServlet extends VaadinServlet {
	protected static Logger logger = null;

	protected Configuration configuration;
	protected UserRepository userRepository;
	protected RetrospectiveRepository retrospectiveRepository;
	protected EventBus globalEventBus = new EventBus(); // for events, which are distributed to all clients

	public Path getWebInfPath() {
		ServletContext servletContext = getServletContext();
		if (servletContext == null) {
			throw new RuntimeException("Servletcontext is null.");
		}
		String path = servletContext.getRealPath("/WEB-INF");
		if (path == null) {
			throw new IllegalArgumentException("path is null");
		}
		return Paths.get(path);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		System.setProperty("org.ektorp.support.AutoUpdateViewOnChange", "true"); // Allows Ektorp to automatically replace views in CouchDB, that differ from our definition
		getServletConfig().getServletContext().setAttribute("unloadDelay", 5000);

		// Init log4j
		Path log4jSettings = getWebInfPath().resolve("log4j2.xml");
		if (Files.isReadable(log4jSettings)) {
			Configurator.initialize("config", null, log4jSettings.toUri());
		} else {
			throw new ServletException("No log4j settings found under: " + log4jSettings);
		}
		logger = LogManager.getLogger();
		logger.info("Started {}", getClass().getSimpleName());

		// Read configuration
		try (Reader reader = Files.newBufferedReader(getWebInfPath().resolve("configuration.json"));) {
			configuration = new Gson().fromJson(reader, Configuration.class);
		} catch (Exception e) {
			logger.error("Could not load configuration file.", e);
			throw new ServletException("Could not load configuration file.", e);
		}

		// Init database
		try {
			initDatabase();
		} catch (Exception e) {
			logger.error("Unable to init database", e);
			throw new ServletException("Unable to init database", e);
		}
	}

	protected void initDatabase() throws Exception {
		HttpClient httpClient = new StdHttpClient.Builder().url(configuration.getCouchDB().getUrl()).caching(true).build();

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient, ScreboObjectMapper.getInstance());

		CouchDbConnector userDatabase = new StdCouchDbConnector("screbo_users", dbInstance, ScreboObjectMapper.getInstance());
		userRepository = new UserRepository(userDatabase);
		ChangesListenerThread changesListenerThread = new ChangesListenerThread(globalEventBus, User.class, userDatabase);
		changesListenerThread.setDaemon(true);
		changesListenerThread.start();

		CouchDbConnector retrospectiveDatabase = new StdCouchDbConnector("retrospectives", dbInstance, ScreboObjectMapper.getInstance());
		retrospectiveRepository = new RetrospectiveRepository(retrospectiveDatabase);
		changesListenerThread = new ChangesListenerThread(globalEventBus, Retrospective.class, retrospectiveDatabase);
		changesListenerThread.setDaemon(true);
		changesListenerThread.start();

	}

	public static UserRepository getUserRepository() {
		return ((ScreboServlet) VaadinServlet.getCurrent()).userRepository;
	}

	public static RetrospectiveRepository getRetrospectiveRepository() {
		return ((ScreboServlet) VaadinServlet.getCurrent()).retrospectiveRepository;
	}

	public static EventBus getGlobalEventBus() {
		return ((ScreboServlet) VaadinServlet.getCurrent()).globalEventBus;

	}

}
