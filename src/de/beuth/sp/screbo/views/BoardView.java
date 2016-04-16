package de.beuth.sp.screbo.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class BoardView extends MainView {

	{
		addComponent(new Label("Hi"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		addComponent(new Label(event.getParameters()));
	}

}
