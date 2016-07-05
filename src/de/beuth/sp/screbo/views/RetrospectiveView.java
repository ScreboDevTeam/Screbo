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
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.components.ActivityEditPanel;
import de.beuth.sp.screbo.components.ActivityOverviewPanel;
import de.beuth.sp.screbo.components.EditPostingWindow;
import de.beuth.sp.screbo.components.EditPostingWindow.OnOkClicked;
import de.beuth.sp.screbo.database.Activity;
import de.beuth.sp.screbo.database.Category;
import de.beuth.sp.screbo.database.Cluster;
import de.beuth.sp.screbo.database.MyCouchDbRepositorySupport.TransformationRunnable;
import de.beuth.sp.screbo.database.Posting;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.DatabaseObjectChangedEvent;
import de.beuth.sp.screbo.eventBus.events.RequestCloseRetrospectiveEvent;
import de.beuth.sp.screbo.eventBus.events.RequestDisplayErrorMessageEvent;
import de.beuth.sp.screbo.eventBus.events.RequestNavigateToRetrospectivesViewEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveClosedEvent;
import de.beuth.sp.screbo.eventBus.events.RetrospectiveOpenedEvent;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.steinwedel.messagebox.MessageBox;

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
									screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
								} else {
									Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(category.getId()); // Get new object
									if (categoryToModify == null) {
										screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
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
		protected final Category category;
		protected final Cluster cluster;
		protected DragAndDropWrapper wrapper;

		public ClusterGuiElement(Category category, Cluster cluster) {
			super();
			this.category = category;
			this.cluster = cluster;
			setStyleName("ClusterGuiElement");
		}

		protected void initWrapper() {
			wrapper = new DragAndDropWrapper(this);
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
									screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
								} else {
									Category categoryToRemoveFrom = retrospectiveToWrite.getCategories().getFromID(otherCategoryId); // Get new object
									if (categoryToRemoveFrom == null) {
										screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
									} else {
										Cluster clusterToRemove = retrospectiveToWrite.getClusterFromId(otherClusterId); // Get new object
										if (clusterToRemove == null) {
											screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
										} else {
											categoryToRemoveFrom.getCluster().remove(clusterToRemove);
											clusterToModify.getPostings().addAll(clusterToRemove.getPostings());
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
			if (wrapper == null) {
				initWrapper();
			}
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
	protected Panel boardActivityPanel;

	public RetrospectiveView(ScreboUI screboUI) {
		super(screboUI);
		boardLayout = new HorizontalLayout();
		boardLayout.setStyleName("boardLayout");
		boardLayout.setSizeFull();
		Panel boardMainPanel = new Panel(boardLayout);
		boardMainPanel.setSizeFull();

		boardActivityPanel = new Panel();
		boardActivityPanel.setSizeFull();
		boardActivityPanel.setWidth(250, Unit.PIXELS);
		boardActivityPanel.setStyleName("activityArea");

		HorizontalLayout horizontalLayout = new HorizontalLayout(boardMainPanel, boardActivityPanel);
		horizontalLayout.setComponentAlignment(boardActivityPanel, Alignment.TOP_RIGHT);
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
					retrospective = null;
					screboUI.getEventBus().fireEvent(new RequestNavigateToRetrospectivesViewEvent());
				}
			}
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		openRetrospective(event.getParameters(), false);
	}

	protected void openRetrospective(String retrospectiveId, boolean alreadyOpen) {
		try {
			retrospective = ScreboServlet.getRetrospectiveRepository().get(retrospectiveId);

			if (retrospective.isVisibleByUser(UserRepository.getUserFromSession())) {
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
		screboUI.getEventBus().fireEvent(new RetrospectiveClosedEvent(retrospective));
		screboUI.getEventBus().fireEvent(new RequestNavigateToRetrospectivesViewEvent());
		screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent(message));
	}

	protected void openRetrospective() {

		boolean isEditableByUser = retrospective.isEditableByUser(UserRepository.getUserFromSession());
		boolean isDragAndDropEnabled = isEditableByUser && retrospective.isTeamRetroStarted();

		// Categories
		boardLayout.removeAllComponents();
		for (Category category : retrospective.getCategories()) {

			final Label catTitleLabel = new Label(category.getName());
			catTitleLabel.setStyleName("catTitleLabel");
			catTitleLabel.setSizeUndefined();

			// Drag&Drop wrapper
			final PostsArea postsArea = new PostsArea(category);
			postsArea.setStyleName("postsArea");

			//Posts
			for (Cluster cluster : category.getCluster()) {
				ClusterGuiElement clusterArea = new ClusterGuiElement(category, cluster);
				if (isDragAndDropEnabled) {
					postsArea.addComponent(clusterArea.getWrapper());
				} else {
					postsArea.addComponent(clusterArea);
				}
				for (Posting posting : cluster.getPostings()) {
					Label postingGuiElement = new Label(posting.getTitle());
					postingGuiElement.setStyleName("postingGuiElement");
					postingGuiElement.setDescription("right-click for more options");
					clusterArea.addComponent(postingGuiElement);

					if (isEditableByUser) {
						if (isDragAndDropEnabled) {
							postingGuiElement.setStyleName("movable", true);
						}
						ContextMenu retroItemContextMenu = new ContextMenu();
						retroItemContextMenu.setAsContextMenuOf(postingGuiElement);

						retroItemContextMenu.addItem("edit posting").addItemClickListener((ContextMenu.ContextMenuItemClickListener & Serializable) (event) -> {

							EditPostingWindow editPostingWindow = new EditPostingWindow(screboUI, posting, new OnOkClicked() {

								@Override
								public void onOkClicked(Posting postingToEdit) {
									editPosting(category.getId(), cluster.getId(), postingToEdit);
								}
							});
							editPostingWindow.setCaption("edit posting");
							editPostingWindow.center();
							editPostingWindow.setVisible(true);
							screboUI.addWindow(editPostingWindow);

						});

						if (cluster.getPostings().size() > 1) {
							retroItemContextMenu.addItem("remove posting from cluster").addItemClickListener((ContextMenu.ContextMenuItemClickListener & Serializable) (event) -> {
								removeRetroItemFromCluster(category.getId(), cluster.getId(), posting.getId());
							});
						}

						retroItemContextMenu.addItem("delete posting").addItemClickListener((ContextMenu.ContextMenuItemClickListener & Serializable) (event) -> {

							MessageBox.createQuestion().withCaption("deletion").withMessage("Do you really want to delete this posting?").withYesButton(new Runnable() {

								@Override
								public void run() {
									deletePosting(category.getId(), cluster.getId(), posting.getId());
								}
							}).withNoButton().open();

						});
					}

				}
			}

			VerticalLayout catArea = new VerticalLayout(catTitleLabel);
			catArea.setStyleName("catArea");
			catArea.setComponentAlignment(catTitleLabel, Alignment.MIDDLE_CENTER);

			if (isEditableByUser) {

				final Button addRetroItemButton = new Button("add a posting");
				addRetroItemButton.setDescription("adds a posting to the category");
				addRetroItemButton.addClickListener(event -> {

					EditPostingWindow editPostingWindow = new EditPostingWindow(screboUI, new Posting(""), new OnOkClicked() {

						@Override
						public void onOkClicked(Posting retroItem) {
							createPosting(category.getId(), retroItem);
						}
					});
					editPostingWindow.setCaption("new posting");
					editPostingWindow.center();
					editPostingWindow.setVisible(true);
					editPostingWindow.setResizable(false);
					editPostingWindow.setModal(true);
					screboUI.addWindow(editPostingWindow);

				});
				catArea.addComponent(addRetroItemButton);
				catArea.setComponentAlignment(addRetroItemButton, Alignment.MIDDLE_CENTER);
			}

			catArea.addComponent(postsArea.getWrapper());
			boardLayout.addComponent(catArea);
		}

		// Activityarea

		if (retrospective.isTeamRetroStarted()) {
			boardActivityPanel.setVisible(true);
			setActivityOverviewPanel();
		} else {
			boardActivityPanel.setVisible(false);
		}

	}

	protected void setActivityOverviewPanel() {
		ActivityOverviewPanel activityOverviewPanel = new ActivityOverviewPanel(retrospective);
		boardActivityPanel.setContent(activityOverviewPanel);
		activityOverviewPanel.setAddEditHandler(activity -> {
			setActivityEditPanel(activity);
		});
	}

	protected void setActivityEditPanel(Activity activity) {
		ActivityEditPanel activityEditPanel = new ActivityEditPanel(screboUI, retrospective, activity);
		boardActivityPanel.setContent(activityEditPanel);
		activityEditPanel.setOnReturnToOverview(() -> {
			setActivityOverviewPanel();
		});
	}

	protected void removeRetroItemFromCluster(String categoryId, String clusterId, String retroItemId) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster clusterToModify = categoryToModify.getCluster().getFromID(clusterId);
					if (clusterToModify == null) {
						screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
					} else {
						Posting retroItemToModify = clusterToModify.getPostings().getFromID(retroItemId);
						if (retroItemToModify == null) {
							screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The item was deleted."));
						} else {
							clusterToModify.getPostings().remove(retroItemToModify);
							Cluster newCluster = new Cluster();
							newCluster.getPostings().add(retroItemToModify);
							categoryToModify.getCluster().add(newCluster);
						}
					}
				}
			}
		});
	}

	protected void editPosting(String categoryId, String clusterId, Posting posting) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster clusterToModify = categoryToModify.getCluster().getFromID(clusterId);
					if (clusterToModify == null) {
						screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
					} else {
						clusterToModify.getPostings().replace(posting);
					}
				}
			}
		});
	}

	protected void createPosting(String categoryId, Posting posting) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster newCluster = new Cluster();
					newCluster.getPostings().add(posting);
					categoryToModify.getCluster().add(newCluster);
				}
			}
		});
	}

	protected void deletePosting(String categoryId, String clusterId, String postingId) {
		modifyRetrospective(new TransformationRunnable<Retrospective>() {

			@Override
			public void applyChanges(Retrospective retrospectiveToWrite) {
				Category categoryToModify = retrospectiveToWrite.getCategories().getFromID(categoryId);
				if (categoryToModify == null) {
					screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The category was deleted."));
				} else {
					Cluster clusterToModify = categoryToModify.getCluster().getFromID(clusterId);
					if (clusterToModify == null) {
						screboUI.getEventBus().fireEvent(new RequestDisplayErrorMessageEvent("The cluster was deleted."));
					} else {
						clusterToModify.getPostings().removeItemWithId(postingId);
						if (clusterToModify.getPostings().size() == 0) {
							categoryToModify.getCluster().removeItemWithId(clusterId);
						}
					}
				}
			}
		});
	}

	protected boolean modifyRetrospective(TransformationRunnable<Retrospective> transformationRunnable) {
		try {
			return ScreboServlet.getRetrospectiveRepository().update(retrospective, transformationRunnable);
		} catch (Exception e) {
			logger.error("Could not write to database.", e);
			screboUI.fireCouldNotWriteToDatabaseEvent(e);
		}
		return false;
	}

}
