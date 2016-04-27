package de.beuth.sp.screbo.views;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
		
		HorizontalLayout boardMainLayout = new HorizontalLayout();
		boardMainLayout.setSizeFull();
		addComponent(boardMainLayout);
		
		VerticalLayout boardArea = new VerticalLayout();
		VerticalLayout cat1Area = new VerticalLayout();
		VerticalLayout cat2Area = new VerticalLayout();
		VerticalLayout cat3Area = new VerticalLayout();
		VerticalLayout cat4Area = new VerticalLayout();
		VerticalLayout activityArea = new VerticalLayout();
		
		boardArea.setStyleName("boardArea", true);
		cat1Area.setStyleName("cat1Area", true);
		cat2Area.setStyleName("cat2Area", true);
		cat3Area.setStyleName("cat3Area", true);
		cat4Area.setStyleName("cat4Area", true);
		activityArea.setStyleName("activityArea", true);
		
		boardMainLayout.addComponent(boardArea);
		boardMainLayout.addComponent(cat1Area);
		boardMainLayout.addComponent(cat2Area);
		boardMainLayout.addComponent(cat3Area);
		boardMainLayout.addComponent(cat4Area);
		boardMainLayout.addComponent(activityArea);
		
		// Board 
		Label boardLblBez = new Label("Bezeichnung");
		Label boardLblDate = new Label("Datum (TT.MM.JJJJ)");
		Label boardLblProject = new Label("durchgeführte Projekte");
		
		TextField boardTxtBez = new TextField();
		boardTxtBez.setStyleName("boardInput");
		TextField boardTxtDate = new TextField();
		boardTxtDate.setStyleName("boardInput");
		TextArea boardTxtProject = new TextArea();
		boardTxtProject.setStyleName("boardInput");
		
		Button boardBtnNew = new Button("add / save");
		boardBtnNew.setStyleName("addSaveBtn");
		
		Button boardBtnExisting6 = new Button("Retro Sprint 6");
		boardBtnExisting6.setStyleName("BoardBtn");
		Button boardBtnExisting5 = new Button("Retro Sprint 5");
		boardBtnExisting5.setStyleName("BoardBtn");
		Button boardBtnExisting4 = new Button("Retro Sprint 4");
		boardBtnExisting4.setStyleName("BoardBtn");
		Button boardBtnExisting3 = new Button("Retro Sprint 3");
		boardBtnExisting3.setStyleName("BoardBtn");
		Button boardBtnExisting2 = new Button("Retro Sprint 2");
		boardBtnExisting2.setStyleName("BoardBtn");
		
		boardArea.addComponent(boardLblBez);
		boardArea.addComponent(boardTxtBez);
		
		boardArea.addComponent(boardLblDate);
		boardArea.addComponent(boardTxtDate);

		boardArea.addComponent(boardLblProject);
		boardArea.addComponent(boardTxtProject);
		
		boardArea.addComponent(boardBtnNew);
		boardArea.addComponent(boardBtnExisting6);
		boardArea.addComponent(boardBtnExisting5);
		boardArea.addComponent(boardBtnExisting4);
		boardArea.addComponent(boardBtnExisting3);
		boardArea.addComponent(boardBtnExisting2);
		
		// Category 1
		Label cat1Lbl = new Label("Liked");
		cat1Lbl.setStyleName("catLabel");
		cat1Lbl.setSizeUndefined();
		Button cat1Btn = new Button("+");
		cat1Btn.addStyleName("addRetroItemBtn");
		cat1Area.addComponent(cat1Lbl);
		cat1Area.addComponent(cat1Btn);
		cat1Area.setComponentAlignment(cat1Lbl, Alignment.MIDDLE_CENTER);
		cat1Area.setComponentAlignment(cat1Btn, Alignment.MIDDLE_CENTER);
		
		// Category 2
		Label cat2Lbl = new Label("Learned");
		cat2Lbl.setStyleName("catLabel");
		cat2Lbl.setSizeUndefined();
		Button cat2Btn = new Button("+");
		cat2Btn.addStyleName("addRetroItemBtn");
		cat2Area.addComponent(cat2Lbl);
		cat2Area.addComponent(cat2Btn);
		cat2Area.setComponentAlignment(cat2Lbl, Alignment.MIDDLE_CENTER);
		cat2Area.setComponentAlignment(cat2Btn, Alignment.MIDDLE_CENTER);
				
		// Category 3
		Label cat3Lbl = new Label("Lacked");
		cat3Lbl.setStyleName("catLabel");
		cat3Lbl.setSizeUndefined();
		Button cat3Btn = new Button("+");
		cat3Btn.addStyleName("addRetroItemBtn");
		cat3Area.addComponent(cat3Lbl);
		cat3Area.addComponent(cat3Btn);
		cat3Area.setComponentAlignment(cat3Lbl, Alignment.MIDDLE_CENTER);
		cat3Area.setComponentAlignment(cat3Btn, Alignment.MIDDLE_CENTER);	
		
		// Category 4
		Label cat4Lbl = new Label("Longed for");
		cat4Lbl.setStyleName("catLabel");
		cat4Lbl.setSizeUndefined();
		Button cat4Btn = new Button("+");
		cat4Btn.addStyleName("addRetroItemBtn");
		cat4Area.addComponent(cat4Lbl);
		cat4Area.addComponent(cat4Btn);
		cat4Area.setComponentAlignment(cat4Lbl, Alignment.MIDDLE_CENTER);
		cat4Area.setComponentAlignment(cat4Btn, Alignment.MIDDLE_CENTER);
		
		// Activity 
		Label actLblAct = new Label("Maßnahme");
		Label actLblDate = new Label("Termin (TT.MM.JJJJ)");
		Label actLblPrio = new Label("Priorität");
		
		TextField actTxtAct = new TextField();
		actTxtAct.setStyleName("boardInput");
		TextField actTxtDate = new TextField();
		actTxtDate.setStyleName("boardInput");
		ComboBox actDropPrio = new ComboBox();
		actDropPrio.setStyleName("boardInput");
		actDropPrio.addItem("Wichtig");
		actDropPrio.addItem("Normal");
		actDropPrio.addItem("Unwichtig");
		
		Button actBtnNew = new Button("add / save");
		actBtnNew.setStyleName("addSaveBtn");
		
		Button actBtnExisting6 = new Button("kürzere Meetings");
		actBtnExisting6.setStyleName("BoardBtn");
		Button actBtnExisting5 = new Button("neue Rechner");
		actBtnExisting5.setStyleName("BoardBtn");
		Button actBtnExisting4 = new Button("ergonmische Stühle");
		actBtnExisting4.setStyleName("BoardBtn");
		
		activityArea.addComponent(actLblAct);
		activityArea.addComponent(actTxtAct);
		
		activityArea.addComponent(actLblDate);
		activityArea.addComponent(actTxtDate);

		activityArea.addComponent(actLblPrio);
		activityArea.addComponent(actDropPrio);
		
		activityArea.addComponent(actBtnNew);
		activityArea.addComponent(actBtnExisting6);
		activityArea.addComponent(actBtnExisting5);
		activityArea.addComponent(actBtnExisting4);
	}

}
