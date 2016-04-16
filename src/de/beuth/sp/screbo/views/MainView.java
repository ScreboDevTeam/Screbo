package de.beuth.sp.screbo.views;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class MainView extends VerticalLayout implements View {
	protected final Logger logger = LogManager.getLogger();

	{
		Label screboLabel = new Label("Welcome to Screbo, please select your board:");

		ComboBox boardComboBox = new ComboBox();
		boardComboBox.setEnabled(false);

		HorizontalLayout topBar = new HorizontalLayout();
		topBar.setStyleName("topBar", true);
		topBar.addComponent(screboLabel);
		topBar.addComponent(boardComboBox);
		topBar.setComponentAlignment(screboLabel, Alignment.MIDDLE_RIGHT);
		topBar.setComponentAlignment(boardComboBox, Alignment.MIDDLE_RIGHT);

		addComponent(topBar);
		setExpandRatio(topBar, 0);
		setComponentAlignment(topBar, Alignment.TOP_RIGHT);
		setSizeFull();
	}

	protected String getSessionId() {
		return VaadinSession.getCurrent().getSession().getId();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		logger.info("View {} entered by {}", getClass().getSimpleName(), getSessionId());
	}

}
