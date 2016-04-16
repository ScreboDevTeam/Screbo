package de.beuth.sp.screbo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ScreboContextListener implements ServletContextListener {

	// Screbo app deploying/launching.
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		//ServletContext context = contextEvent.getServletContext();	
	}

	// Screbo app un-deploying/shutting down.
	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		//ServletContext context = contextEvent.getServletContext();
	}

}