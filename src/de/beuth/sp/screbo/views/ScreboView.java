package de.beuth.sp.screbo.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public abstract class ScreboView extends VerticalLayout implements View {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;

	public ScreboView(ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;
		setSizeFull();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		logger.info("View {} entered by {}", getClass().getSimpleName(), screboUI.getSessionId());
	}

}
