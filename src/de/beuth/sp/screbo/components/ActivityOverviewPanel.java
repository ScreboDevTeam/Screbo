package de.beuth.sp.screbo.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.database.Activity;
import de.beuth.sp.screbo.database.Retrospective;

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

		VerticalLayout overviewPane = new VerticalLayout();

		for (final Activity activity : retrospective.getActivities()) {
			Button activityButton = new Button(activity.getDescription());
			activityButton.addClickListener(event -> {
				if (addEditHandler != null) {
					addEditHandler.addEditActivity(activity);
				}
			});
			overviewPane.addComponent(activityButton);
		}

		Button addActivityButton = new Button("Add Activity");
		addActivityButton.addClickListener(event -> {
			if (addEditHandler != null) {
				addEditHandler.addEditActivity(null);
			}
		});
		overviewPane.addComponent(addActivityButton);

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
