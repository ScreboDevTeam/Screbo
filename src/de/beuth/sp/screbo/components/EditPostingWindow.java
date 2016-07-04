package de.beuth.sp.screbo.components;

import com.google.common.base.Strings;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.Helper;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Posting;

@SuppressWarnings("serial")
public class EditPostingWindow extends ScreboWindow {
	public static interface OnOkClicked {
		public void onOkClicked(Posting retroItem);
	}
	protected final VerticalLayout verticalLayout = new VerticalLayout();
	protected final Button okButton = new Button("ok");
	protected final Button cancelButton = new Button("cancel");
	protected final Posting retroItem;
	protected boolean exitedWithOk;
	protected final OnOkClicked onOkClicked;

	public EditPostingWindow(ScreboUI screboUI, Posting retroItem, OnOkClicked onOkClicked) {
		super(screboUI);
		setStyleName("EditPostingWindow");
		this.retroItem = (Posting) Helper.slowDeepClone(retroItem);
		this.onOkClicked = onOkClicked;

		Label title = new Label("title:");
		title.setSizeFull();
		verticalLayout.addComponent(title);

		TextArea titleTextField = new TextArea();
		titleTextField.setTextChangeEventMode(TextChangeEventMode.EAGER);
		titleTextField.setSizeFull();
		titleTextField.setValue(retroItem.getTitle());
		titleTextField.addTextChangeListener(event -> {
			EditPostingWindow.this.retroItem.setTitle(event.getText());
			setOkButtonEnabled();
		});
		setOkButtonEnabled();

		verticalLayout.addComponent(titleTextField);

		cancelButton.addClickListener(event -> {
			close();
		});

		okButton.addClickListener(event -> {
			exitedWithOk = true;
			close();
			onOkClicked.onOkClicked(EditPostingWindow.this.retroItem);
		});

		HorizontalLayout horizontalLayout = new HorizontalLayout();

		horizontalLayout.setSizeFull();
		horizontalLayout.addComponent(cancelButton);
		horizontalLayout.addComponent(okButton);
		horizontalLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_LEFT);
		horizontalLayout.setComponentAlignment(okButton, Alignment.MIDDLE_RIGHT);
		verticalLayout.addComponent(horizontalLayout);

		verticalLayout.setWidth("300px");
		setContent(verticalLayout);
		titleTextField.focus();
	}

	protected void setOkButtonEnabled() {
		okButton.setEnabled(!Strings.isNullOrEmpty(retroItem.getTitle()));
	}

	public boolean isExitedWithOk() {
		return exitedWithOk;
	}

	public Posting getRetroItem() {
		return retroItem;
	}
}
