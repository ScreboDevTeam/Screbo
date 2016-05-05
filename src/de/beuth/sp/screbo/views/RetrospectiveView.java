package de.beuth.sp.screbo.views;

import java.util.Objects;

import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;

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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Category;
import de.beuth.sp.screbo.database.Cluster;
import de.beuth.sp.screbo.database.RetroItem;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.events.DisplayErrorMessageEvent;
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
	protected static class PostsArea extends VerticalLayout {
		final DragAndDropWrapper wrapper = new DragAndDropWrapper(this);

		public PostsArea() {
			super();
			setWidth("300px");
			setStyleName("PostArea");

			wrapper.setDropHandler(new DropHandler() {

				@Override
				public void drop(DragAndDropEvent event) {
					addComponent(event.getTransferable().getSourceComponent());
				}

				@Override
				public AcceptCriterion getAcceptCriterion() {
					return AcceptAll.get();
				}

			});
		}

		public DragAndDropWrapper getWrapper() {
			return wrapper;
		}

	}

	protected static class ClusterArea extends VerticalLayout {
		final DragAndDropWrapper wrapper = new DragAndDropWrapper(this);

		public ClusterArea(Cluster cluster) {
			super();
			setStyleName("ClusterArea");
		}

		public DragAndDropWrapper getWrapper() {
			return wrapper;
		}

	}

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

		HorizontalLayout boardLayout = new HorizontalLayout();
		Panel boardMainPanel = new Panel();
		boardMainPanel.setSizeFull();
		boardMainPanel.setContent(boardLayout);

		// Categories
		for (Category category : retrospective.getCategories()) {

			final Label catTitleLabel = new Label(category.getName());
			catTitleLabel.setStyleName("catLabel");
			catTitleLabel.setSizeUndefined();

			// Drag&Drop wrapper
			final PostsArea postsArea = new PostsArea();

			//Posts
			for (Cluster cluster : category.getCluster()) {
				ClusterArea clusterArea = new ClusterArea(cluster);
				postsArea.addComponent(clusterArea.getWrapper());
				for (RetroItem retroItem : cluster.getRetroItems()) {
					clusterArea.addComponent(new Label(retroItem.getTitle()));
				}
			}

			//TODO I did not understand this, you can reimplement this maybe
			//			Label cat1Post = new Label("Good Teamwork");
			//			cat1Post.setStyleName("posting");
			//			DragAndDropWrapper ddWrapperCat1PostAlone = new DragAndDropWrapper(cat1Post);
			//			ddWrapperCat1PostAlone.setSizeUndefined();
			//			ddWrapperCat1PostAlone.setStyleName("ddWrapperCat1PostAlone");
			//			ddWrapperCat1PostAlone.setDragStartMode(DragStartMode.COMPONENT);
			//			VerticalLayout clustercat1Post1 = new VerticalLayout();
			//			clustercat1Post1.addComponent(ddWrapperCat1PostAlone);
			//			DragAndDropWrapper ddWrapperCat1Post = new DragAndDropWrapper(clustercat1Post1);
			//			ddWrapperCat1Post.setDragStartMode(DragStartMode.COMPONENT);
			//
			//			// Set the wrapper to wrap tightly around the component
			//			ddWrapperCat1Post.setSizeUndefined();
			//			postsArea.addComponent(ddWrapperCat1Post);
			//			ddWrapperCat1Post.setStyleName("ddWrapperCat1Post");
			//
			//			ddWrapperCat1Post.setDropHandler(new DropHandler() {
			//
			//				@Override
			//				public void drop(DragAndDropEvent event) {
			//					clustercat1Post1.addComponent(event.getTransferable().getSourceComponent());
			//					clustercat1Post1.setStyleName("cluster_marker");
			//
			//				}
			//
			//				@Override
			//				public AcceptCriterion getAcceptCriterion() {
			//					return AcceptAll.get();
			//				}
			//
			//			});

			final Button catAddButton = new Button("Add posting");
			catAddButton.setDescription("Add a posting to category.");
			catAddButton.addClickListener(e -> {
				createPosting(category, "Neu");
			});

			VerticalLayout catArea = new VerticalLayout(catTitleLabel, postsArea.getWrapper(), catAddButton);
			catArea.setStyleName("catArea");
			catArea.setComponentAlignment(catTitleLabel, Alignment.MIDDLE_CENTER);
			catArea.setComponentAlignment(catAddButton, Alignment.MIDDLE_CENTER);
			boardLayout.addComponent(catArea);
		}

		// Activityarea
		VerticalLayout activityArea = new VerticalLayout();
		activityArea.setStyleName("activityArea");

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

		activityArea.setWidth("250px");

		HorizontalLayout horizontalLayout = new HorizontalLayout(boardMainPanel, activityArea);
		horizontalLayout.setComponentAlignment(activityArea, Alignment.TOP_RIGHT);
		horizontalLayout.setExpandRatio(boardMainPanel, 1);
		horizontalLayout.setSizeFull();
		addComponent(horizontalLayout);
	}

	protected void createPosting(Category category, String title) {
		createPosting(category, title, System.currentTimeMillis() + 10_000);
	}

	protected void createPosting(Category category, String title, long retryUntil) {
		Cluster cluster = new Cluster();
		cluster.getRetroItems().add(new RetroItem(title));
		category.getCluster().add(cluster);
		try {
			ScreboServlet.getRetrospectiveRepository().update(retrospective);
		} catch (UpdateConflictException e) { // Somebody else was faster
			logger.warn("Got UpdateConflictException", e);
			if (retryUntil < System.currentTimeMillis()) {
				createPosting(category, title, retryUntil);
			} else {
				screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("Could not write to database", e));
			}
		}
	}

}
