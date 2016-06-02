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
public class LandingPageView extends ScreboView {

	public LandingPageView(ScreboUI screboUI) {
		super(screboUI);

		String welcomeString = String.format("Welcome to Screbo! %n %n Our goal is to improve your team's retrospective%nby offering a webbased platform and persistent%ndata while keeping all the benefits of a whiteboard.");
		Label welcomeLbl = new Label(welcomeString);
		welcomeLbl.setWidth("500px");
		welcomeLbl.setStyleName("welcomeLbl");

		Label boardInfo = new Label("choose your retrospective board");
		boardInfo.setSizeUndefined();
		boardInfo.setStyleName("boardInfo");
		Label userInfo = new Label("edit your profile");
		userInfo.setSizeUndefined();
		userInfo.setStyleName("userInfo");
		Label layerPointerInfo = new Label();
		layerPointerInfo.setSizeUndefined();
		layerPointerInfo.setStyleName("layerPointerInfo");
		Label layerPointerBoard = new Label();
		layerPointerBoard.setSizeUndefined();
		layerPointerBoard.setStyleName("layerPointerBoard");

		addComponent(welcomeLbl);
		addComponent(boardInfo);
		addComponent(userInfo);
		addComponent(layerPointerInfo);
		addComponent(layerPointerBoard);
	}

}
