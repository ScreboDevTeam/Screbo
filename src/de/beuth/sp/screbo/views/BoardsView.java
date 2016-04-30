package de.beuth.sp.screbo.views;

import com.sun.javafx.css.SizeUnits;
import com.vaadin.annotations.Theme;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboUI;
import java_cup.version;

/**
 * Allows creation of new boards and selection of a board.
 * 
 * @author geoffrey.teuber
 *
 */
@SuppressWarnings("serial")
@Theme("screbo")
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
		Label boardLblBez = new Label("title");
		Label boardLblDate = new Label("date (DD.MM.YYYY)");
		Label boardLblProject = new Label("related projects");

		TextField boardTxtBez = new TextField();
		boardTxtBez.setStyleName("boardInput");
		TextField boardTxtDate = new TextField();
		boardTxtDate.setStyleName("boardInput");
		TextArea boardTxtProject = new TextArea();
		boardTxtProject.setStyleName("boardInput");

		Button boardBtnNew = new Button("");
		boardBtnNew.setDescription("add / save your retro");
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
		Label cat1Lbl = new Label("");
		cat1Lbl.setStyleName("catLabel cat1Label");
		cat1Lbl.setSizeUndefined();
		Button cat1Btn = new Button("");
		cat1Btn.setDescription("add a posting to Liked");
		cat1Btn.addStyleName("addRetroItemBtn");
		cat1Area.addComponent(cat1Lbl);
		cat1Area.addComponent(cat1Btn);
		cat1Area.setComponentAlignment(cat1Lbl, Alignment.MIDDLE_CENTER);
		cat1Area.setComponentAlignment(cat1Btn, Alignment.MIDDLE_CENTER);
		
		// Drag&Drop wrapper
		VerticalLayout cat1PostsArea = new VerticalLayout();
		cat1PostsArea.setStyleName("PostArea");
		DragAndDropWrapper ddWrappercat1 = new DragAndDropWrapper(cat1PostsArea);
		cat1Area.addComponent(ddWrappercat1);
		cat1Area.setComponentAlignment(ddWrappercat1, Alignment.MIDDLE_CENTER);
		
		ddWrappercat1.setDropHandler(new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
            	cat1PostsArea.addComponent(event.getTransferable().getSourceComponent());

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });
		
		//Posts
		Label cat1Post = new Label("Good Teamwork");
		cat1Post.setStyleName("posting");
		DragAndDropWrapper ddWrapperCat1PostAlone = new DragAndDropWrapper(cat1Post);
		ddWrapperCat1PostAlone.setSizeUndefined();
		ddWrapperCat1PostAlone.setStyleName("ddWrapperCat1PostAlone");
		ddWrapperCat1PostAlone.setDragStartMode(DragStartMode.COMPONENT);
		VerticalLayout clustercat1Post1 = new VerticalLayout();
		clustercat1Post1.addComponent(ddWrapperCat1PostAlone);
		DragAndDropWrapper ddWrapperCat1Post = new DragAndDropWrapper(clustercat1Post1);
		ddWrapperCat1Post.setDragStartMode(DragStartMode.COMPONENT);

		// Set the wrapper to wrap tightly around the component
		ddWrapperCat1Post.setSizeUndefined();
		cat1PostsArea.addComponent(ddWrapperCat1Post);
		ddWrapperCat1Post.setStyleName("ddWrapperCat1Post");
		
		ddWrapperCat1Post.setDropHandler(new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
            	clustercat1Post1.addComponent(event.getTransferable().getSourceComponent());

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });
		
		

		// Category 2
		Label cat2Lbl = new Label("");
		cat2Lbl.setStyleName("catLabel cat2Label");
		cat2Lbl.setSizeUndefined();
		Button cat2Btn = new Button("");
		cat2Btn.addStyleName("addRetroItemBtn");
		cat2Btn.setDescription("add a posting to Learned");
		cat2Area.addComponent(cat2Lbl);
		cat2Area.addComponent(cat2Btn);
		cat2Area.setComponentAlignment(cat2Lbl, Alignment.MIDDLE_CENTER);
		cat2Area.setComponentAlignment(cat2Btn, Alignment.MIDDLE_CENTER);
		
		// Drag&Drop wrapper
		VerticalLayout cat2PostsArea = new VerticalLayout();
		cat2PostsArea.setStyleName("PostArea");
		DragAndDropWrapper ddWrappercat2 = new DragAndDropWrapper(cat2PostsArea);
		cat2Area.addComponent(ddWrappercat2);
		
		ddWrappercat2.setDropHandler(new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
            	cat2PostsArea.addComponent(event.getTransferable().getSourceComponent());

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });
		
		//Posts
		
		// Item to be dragged and dropped
		Label cat2Post = new Label("Funny Meetings");
		cat2Post.setStyleName("posting");
		DragAndDropWrapper ddWrapperCat2PostAlone = new DragAndDropWrapper(cat2Post);
		ddWrapperCat2PostAlone.setSizeUndefined();
		ddWrapperCat2PostAlone.setStyleName("ddWrapperCat2PostAlone");
		ddWrapperCat2PostAlone.setDragStartMode(DragStartMode.COMPONENT);
		VerticalLayout clustercat2Post2 = new VerticalLayout();
		clustercat2Post2.addComponent(ddWrapperCat2PostAlone);
		DragAndDropWrapper ddWrapperCat2Post = new DragAndDropWrapper(clustercat2Post2);
		ddWrapperCat2Post.setDragStartMode(DragStartMode.COMPONENT);
		ddWrapperCat2Post.setStyleName("ddWrapperCat2Post");

		// Set the wrapper to wrap tightly around the component
		ddWrapperCat2Post.setSizeUndefined();
		cat2PostsArea.addComponent(ddWrapperCat2Post);
		
		ddWrapperCat2Post.setDropHandler(new DropHandler() {

            @Override
            public void drop(DragAndDropEvent event) {
            	clustercat2Post2.addComponent(event.getTransferable().getSourceComponent());

            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });

		// Category 3
		Label cat3Lbl = new Label("");
		cat3Lbl.setStyleName("catLabel cat3Label");
		cat3Lbl.setSizeUndefined();
		Button cat3Btn = new Button("");
		cat3Btn.setDescription("add a posting to Lacked");
		cat3Btn.addStyleName("addRetroItemBtn");
		cat3Area.addComponent(cat3Lbl);
		cat3Area.addComponent(cat3Btn);
		cat3Area.setComponentAlignment(cat3Lbl, Alignment.MIDDLE_CENTER);
		cat3Area.setComponentAlignment(cat3Btn, Alignment.MIDDLE_CENTER);

		// Category 4
		Label cat4Lbl = new Label("");
		cat4Lbl.setStyleName("catLabel cat4Label");
		cat4Lbl.setSizeUndefined();
		Button cat4Btn = new Button("");
		cat4Btn.addStyleName("addRetroItemBtn");
		cat4Btn.setDescription("add a posting to Longed for");
		cat4Area.addComponent(cat4Lbl);
		cat4Area.addComponent(cat4Btn);
		cat4Area.setComponentAlignment(cat4Lbl, Alignment.MIDDLE_CENTER);
		cat4Area.setComponentAlignment(cat4Btn, Alignment.MIDDLE_CENTER);

		// Activity 
		Label actLblAct = new Label("activity");
		Label actLblDate = new Label("target date (DD.MM.YYYY)");
		Label actLblPrio = new Label("priority");

		TextField actTxtAct = new TextField();
		actTxtAct.setStyleName("boardInput");
		TextField actTxtDate = new TextField();
		actTxtDate.setStyleName("boardInput");
		ComboBox actDropPrio = new ComboBox();
		actDropPrio.setStyleName("boardInput");
		actDropPrio.addItem("Wichtig");
		actDropPrio.addItem("Normal");
		actDropPrio.addItem("Unwichtig");

		Button actBtnNew = new Button("");
		actBtnNew.setDescription("add / save your activity");
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
