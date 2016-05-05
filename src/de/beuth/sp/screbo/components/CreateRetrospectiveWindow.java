package de.beuth.sp.screbo.components;

import com.google.common.base.Strings;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.UserRepository;

@SuppressWarnings("serial")
public class CreateRetrospectiveWindow extends ScreboWindow {
	protected final TextField titleField = new TextField();
	protected final Button createButton = new Button("Create");

	protected CreateRetrospectiveWindow(ScreboUI screboUI) {
		super(screboUI);
		setCaption("Create Retrospective");
		setResizable(false);

		titleField.addTextChangeListener(e -> {
			setCreateButtonStatus(e.getText());
		});
		createButton.addClickListener(e -> {
			createRetrospective(titleField.getValue());
		});

		setCreateButtonStatus(titleField.getValue());
		VerticalLayout verticalLayout = new VerticalLayout(new Label("title"), titleField, new Label("Members"), createButton);
		setContent(verticalLayout);
		titleField.focus();
	}

	protected void setCreateButtonStatus(String title) {
		createButton.setEnabled(!Strings.isNullOrEmpty(title));
	}

	protected void createRetrospective(String title) {
		if (!Strings.isNullOrEmpty(title)) {
			Retrospective retrospective = new Retrospective(title, UserRepository.getUserFromSession());
			ScreboServlet.getRetrospectiveRepository().add(retrospective);
			close();
		}
	}

}
