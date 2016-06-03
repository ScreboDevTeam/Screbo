package de.beuth.sp.screbo.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.database.Activity;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.UserRepository;

@SuppressWarnings("serial")
public class ActivityOverviewPanel extends VerticalLayout {
	public static interface AddEditHandler {
		public void addEditActivity(Activity activity);
	}

	protected AddEditHandler addEditHandler;

	public ActivityOverviewPanel(Retrospective retrospective) {
		super();
		setStyleName(getClass().getSimpleName());
		setSizeFull();

		boolean editable = retrospective.isEditableByUser(UserRepository.getUserFromSession());
		VerticalLayout overviewPane = new VerticalLayout();
		overviewPane.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		
		final Label activityTitleLabel = new Label("activities");
		activityTitleLabel.setStyleName("activityTitleLabel");
		activityTitleLabel.setSizeUndefined();
		overviewPane.addComponent(activityTitleLabel);
		
		
		if (editable) {
			Button addActivityButton = new Button("add activity");
			addActivityButton.setStyleName("addActivityButton ");
			addActivityButton.addClickListener(event -> {
				if (addEditHandler != null) {
					addEditHandler.addEditActivity(null);
				}
			});
			overviewPane.addComponent(addActivityButton);
		}
		
		for (final Activity activity : retrospective.getActivities()) {
			String activityDescription = String.format("%1$te.%1$tm.%1$tY - %2$s%n-------------------------------%n%3$s", 
										activity.getDateOfLatestRealization(), 
										activity.getPriority(),
										activity.getDescription());
			Button activityButton = new Button(activityDescription);
			activityButton.setStyleName("activityButton");
			activityButton.setSizeFull();
			activityButton.setDescription("open activity details");
			if (activity.isRealized()) {
				activityButton.addStyleName("activityButtonDone");
			}
			if (editable) {
				activityButton.addClickListener(event -> {
					if (addEditHandler != null) {
						addEditHandler.addEditActivity(activity);
					}
				});
			}
			overviewPane.addComponent(activityButton);
		}

		addComponent(overviewPane);
		setComponentAlignment(overviewPane, Alignment.TOP_CENTER);
	}

	public AddEditHandler getAddEditHandler() {
		return addEditHandler;
	}

	public void setAddEditHandler(AddEditHandler addEditHandler) {
		this.addEditHandler = addEditHandler;
	}

}
