package de.beuth.sp.screbo.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;

import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class BoardView extends ScreboView {

	public BoardView(ScreboUI screboUI) {
		super(screboUI);
		addComponent(new Label("Hi"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		addComponent(new Label(event.getParameters()));
	}

}
