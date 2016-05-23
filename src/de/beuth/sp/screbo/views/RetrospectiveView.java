package de.beuth.sp.screbo.views;

import java.io.Serializable;
import java.util.Objects;

import org.ektorp.DocumentNotFoundException;
import org.vaadin.peter.contextmenu.ContextMenu;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.components.EditRetroItemWindow;
import de.beuth.sp.screbo.components.EditRetroItemWindow.OnOkClicked;
import de.beuth.sp.screbo.database.Category;
import de.beuth.sp.screbo.database.Cluster;
import de.beuth.sp.screbo.database.MyCouchDbRepositorySupport.TransformationRunnable;
import de.beuth.sp.screbo.database.RetroItem;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.User;
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
	protected static ServerSideCriterion acceptClusterGuiElementsCriterion = new ServerSideCriterion() {

		@Override
		public boolean accept(DragAndDropEvent dragEvent) {
			// Only allow our own ClusterArea items
			Component sourceComponent = dragEvent.getTransferable().getSourceComponent();
			if (sourceComponent instanceof DragAndDropWrapper && sourceComponent != dragEvent.getTargetDetails().getTarget()) {
				return ((DragAndDropWrapper) sourceComponent).getData() instanceof ClusterGuiElement;
			}
			return false;
		}
	};

	protected class PostsArea extends VerticalLayout {
		final protected DragAndDropWrapper wrapper = new DragAndDropWrapper(this);
		final protected Category category;

		public PostsArea(Category category) {
			super();
			this.category = category;
			setWidth("300px");
			setStyleName("PostArea");

			wrapper.setDropHandler(new DropHandler() {

				@Override
				public void drop(DragAndDropEvent event) {
					Component sourceComponent = event.getTransferable().getSourceComponent();
					logger.info("Got dropped component {}", sourceComponent);

					if (!components.contains(sourceComponent) && sourceComponent instanceof DragAndDropWrapper && ((DragAndDropWrapper) sourceComponent).getData() instanceof ClusterGuiElement) {
						String clusterId = ((ClusterGuiElement) ((DragAndDropWrapper) sourceComponent).getData()).getCluster().getId();

						modifyRetrospective(new TransformationRunnable<Retrospective>() {

							@Override
							public void applyChanges(Retrospective retrospectiveToWrite) {
								Cluster clusterToModify = retrospectiveToWrite.getClusterFromId(clusterId); // Get new object
								if (clusterToModify == null) {
									screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The cluster was deleted."));
								} else {
									Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(category.getId()); // Get new object
									if (categoryToModify == null) {
										screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The category was deleted."));
									} else {
										// Remove from all categories
										for (Category category : retrospectiveToWrite.getCategories()) {
											category.getCluster().removeItemWithId(clusterToModify.getId());
										}
										// add to our category
										categoryToModify.getCluster().add(clusterToModify);
									}
								}
							}
						});
					}

				}

				@Override
				public AcceptCriterion getAcceptCriterion() {
					return acceptClusterGuiElementsCriterion;
				}

			});
		}

		public DragAndDropWrapper getWrapper() {
			return wrapper;
		}
	}

	protected class ClusterGuiElement extends VerticalLayout {
		protected final DragAndDropWrapper wrapper = new DragAndDropWrapper(this);
		final protected Category category;
		protected final Cluster cluster;

		public ClusterGuiElement(Category category, Cluster cluster) {
			super();
			this.category = category;
			this.cluster = cluster;
			setStyleName("ClusterGuiElement");
			if (retrospective.isTeamRetroStarted()) {
				wrapper.setDragStartMode(DragStartMode.WRAPPER);
			}
			wrapper.setWidth("100%");
			wrapper.setData(this);

			wrapper.setDropHandler(new DropHandler() {

				@Override
				public void drop(DragAndDropEvent event) {
					Component sourceComponent = event.getTransferable().getSourceComponent();
					logger.info("Got dropped component {}", sourceComponent);

					if (!components.contains(sourceComponent) && sourceComponent instanceof DragAndDropWrapper && ((DragAndDropWrapper) sourceComponent).getData() instanceof ClusterGuiElement) {
						String otherCategoryId = ((ClusterGuiElement) ((DragAndDropWrapper) sourceComponent).getData()).getCategory().getId();
						String otherClusterId = ((ClusterGuiElement) ((DragAndDropWrapper) sourceComponent).getData()).getCluster().getId();

						modifyRetrospective(new TransformationRunnable<Retrospective>() {

							@Override
							public void applyChanges(Retrospective retrospectiveToWrite) {
								Cluster clusterToModify = retrospectiveToWrite.getClusterFromId(cluster.getId()); // Get new object
								if (clusterToModify == null) {
									screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The cluster was deleted."));
								} else {
									Category categoryToRemoveFrom = retrospectiveToWrite.getCategories().getFromID(otherCategoryId); // Get new object
									if (categoryToRemoveFrom == null) {
										screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The category was deleted."));
									} else {
										Cluster clusterToRemove = retrospectiveToWrite.getClusterFromId(otherClusterId); // Get new object
										if (clusterToRemove == null) {
											screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The cluster was deleted."));
										} else {
											categoryToRemoveFrom.getCluster().remove(clusterToRemove);
											clusterToModify.getRetroItems().addAll(clusterToRemove.getRetroItems());
										}
									}
								}
							}
						});
					}

				}

				@Override
				public AcceptCriterion getAcceptCriterion() {
					return acceptClusterGuiElementsCriterion;
				}

			});
		}

		public DragAndDropWrapper getWrapper() {
			return wrapper;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public Category getCategory() {
			return category;
		}

	}

	protected Retrospective retrospective;
	protected HorizontalLayout boardLayout;
	protected VerticalLayout activityArea;

	public RetrospectiveView(ScreboUI screboUI) {
		super(screboUI);
		boardLayout = new HorizontalLayout();
		boardLayout.setStyleName("boardLayout");
		Panel boardMainPanel = new Panel(boardLayout);
		boardMainPanel.setSizeFull();
		activityArea = new VerticalLayout();
		activityArea.setStyleName("activityArea");
		HorizontalLayout horizontalLayout = new HorizontalLayout(boardMainPanel, activityArea);
		horizontalLayout.setComponentAlignment(activityArea, Alignment.TOP_RIGHT);
		horizontalLayout.setExpandRatio(boardMainPanel, 1);
		horizontalLayout.setSizeFull();
		addComponent(horizontalLayout);
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
				showError(alreadyOpen ? "Sorry, you lost the right to view this retrospective." : "Retrospective not found or you have no rights to view it.");
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

		User myUser = UserRepository.getUserFromSession();
		boolean isEditableByUser = retrospective.getEditableByUserIds().contains(myUser.getId());

		// Categories
		boardLayout.removeAllComponents();
		for (Category category : retrospective.getCategories()) {

			final Label catTitleLabel = new Label(category.getName());
			catTitleLabel.setStyleName("catTitleLabel");
			catTitleLabel.setSizeUndefined();

			// Drag&Drop wrapper
			final PostsArea postsArea = new PostsArea(category);

			//Posts
			for (Cluster cluster : category.getCluster()) {
				ClusterGuiElement clusterArea = new ClusterGuiElement(category, cluster);
				postsArea.addComponent(clusterArea.getWrapper());
				for (RetroItem retroItem : cluster.getRetroItems()) {
					Label retroItemGuiElement = new Label(retroItem.getTitle());
					retroItemGuiElement.setStyleName("retroItemGuiElement");
					clusterArea.addComponent(retroItemGuiElement);

					if (isEditableByUser) {
						if (retrospective.isTeamRetroStarted()) {
							retroItemGuiElement.setStyleName("movable", true);
						}
						ContextMenu retroItemContextMenu = new ContextMenu();
						retroItemContextMenu.setAsContextMenuOf(retroItemGuiElement);

						retroItemContextMenu.addItem("Edit").addItemClickListener((ContextMenu.ContextMenuItemClickListener & Serializable) (event) -> {

							EditRetroItemWindow editRetroItemWindow = new EditRetroItemWindow(screboUI, retroItem, new OnOkClicked() {

								@Override
								public void onOkClicked(RetroItem retroItem) {
									editPosting(category.getId(), cluster.getId(), retroItem);
								}
							});
							editRetroItemWindow.setCaption("Edit Posting");
							editRetroItemWindow.center();
							editRetroItemWindow.setVisible(true);
							screboUI.addWindow(editRetroItemWindow);

						});
						if (cluster.getRetroItems().size() > 1) {
							retroItemContextMenu.addItem("Remove from cluster").addItemClickListener((ContextMenu.ContextMenuItemClickListener & Serializable) (event) -> {
								removeRetroItemFromCluster(category.getId(), cluster.getId(), retroItem.getId());
							});
						}
					}

				}
			}

			VerticalLayout catArea = new VerticalLayout(catTitleLabel);
			catArea.setStyleName("catArea");
			catArea.setComponentAlignment(catTitleLabel, Alignment.MIDDLE_CENTER);

			if (isEditableByUser) {

				final Button addRetroItemButton = new Button("Add a posting");
				addRetroItemButton.setDescription("Adds a posting to the category.");
				addRetroItemButton.addClickListener(event -> {

					EditRetroItemWindow editRetroItemWindow = new EditRetroItemWindow(screboUI, new RetroItem(""), new OnOkClicked() {

						@Override
						public void onOkClicked(RetroItem retroItem) {
							createPosting(category.getId(), retroItem);
						}
					});
					editRetroItemWindow.setCaption("New Posting");
					editRetroItemWindow.center();
					editRetroItemWindow.setVisible(true);
					editRetroItemWindow.setResizable(false);
					editRetroItemWindow.setModal(true);
					screboUI.addWindow(editRetroItemWindow);

				});
				catArea.addComponent(addRetroItemButton);
				catArea.setComponentAlignment(addRetroItemButton, Alignment.MIDDLE_CENTER);
			}

			catArea.addComponent(postsArea.getWrapper());
			boardLayout.addComponent(catArea);
		}

		// Activityarea

		if (retrospective.isTeamRetroStarted()) {
			activityArea.removeAllComponents();

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
		}

	}

	protected void removeRetroItemFromCluster(String categoryId, String clusterId, String retroItemId) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster clusterToModify = categoryToModify.getCluster().getFromID(clusterId);
					if (clusterToModify == null) {
						screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The cluster was deleted."));
					} else {
						RetroItem retroItemToModify = clusterToModify.getRetroItems().getFromID(retroItemId);
						if (retroItemToModify == null) {
							screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The item was deleted."));
						} else {
							clusterToModify.getRetroItems().remove(retroItemToModify);
							Cluster newCluster = new Cluster();
							newCluster.getRetroItems().add(retroItemToModify);
							categoryToModify.getCluster().add(newCluster);
						}
					}
				}
			}
		});
	}

	protected void editPosting(String categoryId, String clusterId, RetroItem retroItem) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster clusterToModify = categoryToModify.getCluster().getFromID(clusterId);
					if (clusterToModify == null) {
						screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The cluster was deleted."));
					} else {
						clusterToModify.getRetroItems().replace(retroItem);
					}
				}
			}
		});
	}

	protected void createPosting(String categoryId, RetroItem retroItem) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster newCluster = new Cluster();
					newCluster.getRetroItems().add(retroItem);
					categoryToModify.getCluster().add(newCluster);
				}
			}
		});
	}

	protected boolean modifyRetrospective(TransformationRunnable<Retrospective> transformationRunnable) {
		try {
			return ScreboServlet.getRetrospectiveRepository().update(retrospective, transformationRunnable);
		} catch (Exception e) {
			logger.error("Could not write to database.", e);
			screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("Could not write to database.", e));
		}
		return false;
	}

}
