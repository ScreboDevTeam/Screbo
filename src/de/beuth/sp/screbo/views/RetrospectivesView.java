package de.beuth.sp.screbo.views;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Label;

import de.beuth.sp.screbo.ScreboUI;

/**
 * Allows creation of new boards and selection of a board.
 * 
 * @author geoffrey.teuber
 *
 */
@SuppressWarnings("serial")
@Theme("screbo")
public class RetrospectivesView extends ScreboView {

	public RetrospectivesView(ScreboUI screboUI) {
		super(screboUI);
		addComponent(new Label("Currently it is only possible to select retrospectives through the top bar."));
	}

}
