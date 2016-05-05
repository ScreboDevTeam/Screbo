package de.beuth.sp.screbo.views;

import java.util.Objects;

import org.ektorp.DocumentNotFoundException;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.events.RequestCloseRetrospectiveEvent;
import de.beuth.sp.screbo.eventBus.events.RequestNavigateToRetrospectivesViewEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveClosedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveOpenedEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;

/**
 * Displays a retrospective board.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class RetrospectiveView extends ScreboView implements ScreboEventListener {
	protected Retrospective retrospective;

	public RetrospectiveView(ScreboUI screboUI) {
		super(screboUI);
		screboUI.getEventBus().addEventListener(this, true);
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (retrospective != null) { // If opened
			if (screboEvent instanceof DatabaseObjectChangedEvent) {
				DatabaseObjectChangedEvent databaseObjectChangedEvent = ((DatabaseObjectChangedEvent) screboEvent);
				if (Retrospective.class.equals(databaseObjectChangedEvent.getObjectClass())) {
					if (Objects.equals(retrospective.getId(), databaseObjectChangedEvent.getDocumentId())) {
						// The currently opened retrospective was changed
						if (databaseObjectChangedEvent.isDeleted()) {
							screboUI.getEventBus().fireEvent(new RetrospectiveClosedEvent(retrospective));
							screboUI.getEventBus().fireEvent(new RequestNavigateToRetrospectivesViewEvent());
							Notification.show("Sorry, your retrospective was deleted.");
						} else {
							screboUI.getEventBus().fireEvent(new RetrospectiveClosedEvent(retrospective));
							openRetrospective(databaseObjectChangedEvent.getDocumentId(), true);
						}
					}
				}
			} else if (screboEvent instanceof RequestCloseRetrospectiveEvent) {
				if (Objects.equals(retrospective.getId(), ((RequestCloseRetrospectiveEvent) screboEvent).getRetrospective().getId())) {
					screboUI.getEventBus().fireEvent(new RetrospectiveClosedEvent(retrospective));
					screboUI.getEventBus().fireEvent(new RequestNavigateToRetrospectivesViewEvent());
				}
			}
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		openRetrospective(event.getParameters(), false);
	}

	protected void openRetrospective(String retrospectiveId, boolean alreadyOpen) {
		try {
			retrospective = ScreboServlet.getRetrospectiveRepository().get(retrospectiveId);

			if (retrospective.getVisibleByUserIds().contains(UserRepository.getUserFromSession().getId())) {
				openRetrospective();
				screboUI.getEventBus().fireEvent(new RetrospectiveOpenedEvent(retrospective));
			} else {
				showError(alreadyOpen ? "Sorry, you lost the right to view the retrospective." : "Retrospective not found or you have no rights to view it.");
			}
		} catch (DocumentNotFoundException e) {
			showError("Retrospective not found or you have no rights to view it.");
		}
	}

	protected void showError(String message) {
		removeAllComponents();
		addComponent(new Label(message));
	}

	protected void openRetrospective() {
		removeAllComponents();

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
		boardLblBez.setStyleName("boardLbl");
		Label boardLblDate = new Label("date (DD.MM.YYYY)");
		boardLblDate.setStyleName("boardLbl");
		Label boardLblProject = new Label("related projects");
		boardLblProject.setStyleName("boardLbl");

		TextField boardTxtBez = new TextField();
		boardTxtBez.setStyleName("boardInput");
		DateField boardTxtDate = new DateField();
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
				clustercat1Post1.setStyleName("cluster_marker");

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
				clustercat2Post2.setStyleName("cluster_marker");

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

		// Drag&Drop wrapper
		VerticalLayout cat3PostsArea = new VerticalLayout();
		cat3PostsArea.setStyleName("PostArea");
		DragAndDropWrapper ddWrappercat3 = new DragAndDropWrapper(cat3PostsArea);
		cat3Area.addComponent(ddWrappercat3);
		cat3Area.setComponentAlignment(ddWrappercat3, Alignment.MIDDLE_CENTER);

		ddWrappercat3.setDropHandler(new DropHandler() {

			@Override
			public void drop(DragAndDropEvent event) {
				cat3PostsArea.addComponent(event.getTransferable().getSourceComponent());

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

		});

		//Posts
		Label cat3Post = new Label("Whatever");
		cat3Post.setStyleName("posting");
		DragAndDropWrapper ddWrapperCat3PostAlone = new DragAndDropWrapper(cat3Post);
		ddWrapperCat3PostAlone.setSizeUndefined();
		ddWrapperCat3PostAlone.setStyleName("ddWrapperCat3PostAlone");
		ddWrapperCat3PostAlone.setDragStartMode(DragStartMode.COMPONENT);
		VerticalLayout clustercat3Post3 = new VerticalLayout();
		clustercat3Post3.addComponent(ddWrapperCat3PostAlone);
		DragAndDropWrapper ddWrapperCat3Post = new DragAndDropWrapper(clustercat3Post3);
		ddWrapperCat3Post.setDragStartMode(DragStartMode.COMPONENT);

		// Set the wrapper to wrap tightly around the component
		ddWrapperCat3Post.setSizeUndefined();
		cat3PostsArea.addComponent(ddWrapperCat3Post);
		ddWrapperCat3Post.setStyleName("ddWrapperCat3Post");

		ddWrapperCat3Post.setDropHandler(new DropHandler() {

			@Override
			public void drop(DragAndDropEvent event) {
				clustercat3Post3.addComponent(event.getTransferable().getSourceComponent());
				clustercat3Post3.setStyleName("cluster_marker");

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

		});

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

		// Drag&Drop wrapper
		VerticalLayout cat4PostsArea = new VerticalLayout();
		cat4PostsArea.setStyleName("PostArea");
		DragAndDropWrapper ddWrappercat4 = new DragAndDropWrapper(cat4PostsArea);
		cat4Area.addComponent(ddWrappercat4);
		cat4Area.setComponentAlignment(ddWrappercat4, Alignment.MIDDLE_CENTER);

		ddWrappercat4.setDropHandler(new DropHandler() {

			@Override
			public void drop(DragAndDropEvent event) {
				cat4PostsArea.addComponent(event.getTransferable().getSourceComponent());

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

		});

		//Posts
		Label cat4Post = new Label("Whatever");
		cat4Post.setStyleName("posting");
		DragAndDropWrapper ddWrapperCat4PostAlone = new DragAndDropWrapper(cat4Post);
		ddWrapperCat4PostAlone.setSizeUndefined();
		ddWrapperCat4PostAlone.setStyleName("ddWrapperCat4PostAlone");
		ddWrapperCat4PostAlone.setDragStartMode(DragStartMode.COMPONENT);
		VerticalLayout clustercat4Post4 = new VerticalLayout();
		clustercat4Post4.addComponent(ddWrapperCat4PostAlone);
		DragAndDropWrapper ddWrapperCat4Post = new DragAndDropWrapper(clustercat4Post4);
		ddWrapperCat4Post.setDragStartMode(DragStartMode.COMPONENT);

		// Set the wrapper to wrap tightly around the component
		ddWrapperCat4Post.setSizeUndefined();
		cat4PostsArea.addComponent(ddWrapperCat4Post);
		ddWrapperCat4Post.setStyleName("ddWrapperCat4Post");

		ddWrapperCat4Post.setDropHandler(new DropHandler() {

			@Override
			public void drop(DragAndDropEvent event) {
				clustercat4Post4.addComponent(event.getTransferable().getSourceComponent());
				clustercat4Post4.setStyleName("cluster_marker");

			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}

		});

		// Activity 
		Label actLblAct = new Label("activity");
		actLblAct.setStyleName("boardLbl");
		Label actLblDate = new Label("target date (DD.MM.YYYY)");
		actLblDate.setStyleName("boardLbl");
		Label actLblPrio = new Label("priority");
		actLblPrio.setStyleName("boardLbl");

		TextField actTxtAct = new TextField();
		actTxtAct.setStyleName("boardInput");
		DateField actTxtDate = new DateField();
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
