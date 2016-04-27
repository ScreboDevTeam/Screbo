package de.beuth.sp.screbo.components;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.beuth.sp.screbo.EventBus.ScreboEvent;
import de.beuth.sp.screbo.EventBus.ScreboEventListener;
import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class TopBar extends HorizontalLayout implements ScreboEventListener {
	protected final ScreboUI screboUI;
	protected Label screboLabel;

	public TopBar(ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;

		setStyleName("topBar", true);
		//screboUI.getEventBus().addEventListener(this);
	}

	@Override
	public void onScreboEvent(ScreboEvent screboEvent) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onScreboEvent(ScreboEvent screboEvent) {
//		if (screboEvent instanceof UserChangedEvent) {
//			setLabelText();
//		}
//	}

//	protected void setLabelText() {
//		User user = User.getUserFromSession();
//		System.out.println("u:" + user);
//		screboLabel.setCaption("Welcome to Screbo" + (user == null ? "" : ", " + user.getUserName()) + ".");
//	}
}
