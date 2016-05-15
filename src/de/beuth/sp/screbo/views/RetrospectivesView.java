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
		setStyleName("login_bg");
		
		String welcomeString = String.format("Welcome to Screbo! %n %n Our Goal is to improve your team's retrospective%nby offering a webbased platform, persistent%ndata and keeping all the benefits of a whiteboard.");
		Label welcomeLbl = new Label(welcomeString);
		welcomeLbl.setWidth("500px");
		welcomeLbl.setStyleName("welcomeLbl");
		
		Label boardInfo = new Label("Choose your Retrospective Board");
		boardInfo.setSizeUndefined();
		boardInfo.setStyleName("boardInfo");
		Label userInfo = new Label("Edit your Profile");
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
