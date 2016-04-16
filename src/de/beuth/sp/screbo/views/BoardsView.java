package de.beuth.sp.screbo.views;

import com.vaadin.ui.Label;

import de.beuth.sp.screbo.ScreboUI;

/**
 * Allows creation of new boards and selection of a board.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class BoardsView extends ScreboView {

	public BoardsView(ScreboUI screboUI) {
		super(screboUI);
		addComponent(new Label("Overview and creation of boards."));
	}

}
