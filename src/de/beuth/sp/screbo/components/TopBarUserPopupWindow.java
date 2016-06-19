package de.beuth.sp.screbo.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.eventBus.ScreboEventListener;
import de.beuth.sp.screbo.eventBus.events.ScreboEvent;
import de.beuth.sp.screbo.eventBus.events.UserChangedEvent;

@SuppressWarnings("serial")
public class TopBarUserPopupWindow extends ScreboWindow implements ScreboEventListener {

	protected TopBarUserPopupWindow(ScreboUI screboUI) {
		super(screboUI);
		setClosable(false);
		setResizable(false);
		setDraggable(false);

		Button editButton = new Button("edit profile");
		editButton.addClickListener(event -> {
			close();
			screboUI.openEditAccountView();
		});

		Button logoutButton = new Button("logout");
		logoutButton.addClickListener(event -> {
			close();
			screboUI.doLogout();
		});

		VerticalLayout verticalLayout = new VerticalLayout(editButton, logoutButton);
		verticalLayout.setStyleName("VerticalLayout");
		setContent(verticalLayout);
		setVisible(true);

		screboUI.getEventBus().addEventListener(this, true);
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			close();
		}
	}

}
