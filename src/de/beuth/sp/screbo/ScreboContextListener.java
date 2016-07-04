package de.beuth.sp.screbo;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.common.collect.Lists;

/**
 * Static class to distribute notifications of web application shutdown.
 * 
 * @author volker.gronau
 *
 */
@WebListener
public class ScreboContextListener implements ServletContextListener {

	protected static final List<Runnable> shutdownListeners = Lists.newArrayList();

	public static void addShutdownListener(Runnable runnable) {
		shutdownListeners.add(runnable);
	}

	public static void removeShutdownListener(Runnable runnable) {
		shutdownListeners.remove(runnable);
	}

	@Override
	// Screbo app un-deploying/shutting down.
	public void contextDestroyed(ServletContextEvent arg0) {
		List<Runnable> shutdownListeners = Lists.newArrayList();
		shutdownListeners.addAll(ScreboContextListener.shutdownListeners);
		for (Runnable runnable : shutdownListeners) {
			runnable.run();
		}
	}

	@Override

	// Screbo app deploying/launching.
	public void contextInitialized(ServletContextEvent arg0) {

	}

}