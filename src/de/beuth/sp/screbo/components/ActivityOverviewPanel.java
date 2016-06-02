package de.beuth.sp.screbo.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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

		for (final Activity activity : retrospective.getActivities()) {
			Button activityButton = new Button(activity.getDescription());
			if (editable) {
				activityButton.addClickListener(event -> {
					if (addEditHandler != null) {
						addEditHandler.addEditActivity(activity);
					}
				});
			}
			overviewPane.addComponent(activityButton);
		}

		if (editable) {
			Button addActivityButton = new Button("add activity");
			addActivityButton.addClickListener(event -> {
				if (addEditHandler != null) {
					addEditHandler.addEditActivity(null);
				}
			});
			overviewPane.addComponent(addActivityButton);
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
