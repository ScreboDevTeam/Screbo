package de.beuth.sp.screbo;

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

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import de.beuth.sp.screbo.database.UserRepository;

@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = ScreboUI.class, widgetset = "de.beuth.sp.screbo.widgetset.ScreboWidgetset")
public class ScreboServlet extends VaadinServlet {
	protected static Logger logger = null;

	protected UserRepository userRepository;

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

		getServletConfig().getServletContext().setAttribute("unloadDelay", 5000);

		Path log4jSettings = getWebInfPath().resolve("log4j2.xml");

		if (Files.isReadable(log4jSettings)) {
			Configurator.initialize("config", null, log4jSettings.toUri());
		} else {
			throw new ServletException("No log4j settings found under: " + log4jSettings);
		}
		logger = LogManager.getLogger();
		logger.info("Started {}", getClass().getSimpleName());

		try {
			initDatabaseConnection();
		} catch (Exception e) {
			logger.error("Unable to init database connection", e);
			throw new ServletException("Unable to init database connection", e);
		}
	}

	protected void initDatabaseConnection() throws Exception {
		HttpClient httpClient = new StdHttpClient.Builder().url("http://homeserver:5984").build();

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);

		CouchDbConnector userDatabase = new StdCouchDbConnector("screbo_users", dbInstance);
		userDatabase.createDatabaseIfNotExists();
		userRepository = new UserRepository(userDatabase);

	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

}
