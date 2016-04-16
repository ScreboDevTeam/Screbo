package de.beuth.sp.screbo.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.beuth.sp.screbo.EventBus.ScreboEvent;
import de.beuth.sp.screbo.EventBus.ScreboEventListener;
import de.beuth.sp.screbo.EventBus.UserChangedEvent;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.User;

@SuppressWarnings("serial")
public class TopBar extends HorizontalLayout implements ScreboEventListener {
	protected final ScreboUI screboUI;
	protected Label screboLabel;

	public TopBar(ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;
		screboLabel = new Label();

		ComboBox boardComboBox = new ComboBox();
		boardComboBox.setEnabled(false);

		setStyleName("topBar", true);
		addComponent(screboLabel);
		addComponent(boardComboBox);
		setComponentAlignment(screboLabel, Alignment.MIDDLE_RIGHT);
		setComponentAlignment(boardComboBox, Alignment.MIDDLE_RIGHT);

		screboUI.getEventBus().addEventListener(this);
		setLabelText();
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		if (screboEvent instanceof UserChangedEvent) {
			setLabelText();
		}
	}

	protected void setLabelText() {
		User user = User.getUserFromSession();
		System.out.println("u:" + user);
		screboLabel.setCaption("Welcome to Screbo" + (user == null ? "" : ", " + user.getUserName()) + ".");
	}
}
