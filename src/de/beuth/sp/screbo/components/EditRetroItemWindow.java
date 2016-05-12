package de.beuth.sp.screbo.components;

import com.google.common.base.Strings;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.Helper;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.RetroItem;

@SuppressWarnings("serial")
public class EditRetroItemWindow extends ScreboWindow {
	public static interface OnOkClicked {
		public void onOkClicked(RetroItem retroItem);
	}
	protected final VerticalLayout verticalLayout = new VerticalLayout();
	protected final Button okButton = new Button("Ok");
	protected final Button cancelButton = new Button("Cancel");
	protected final RetroItem retroItem;
	protected boolean exitedWithOk;
	protected final OnOkClicked onOkClicked;

	public EditRetroItemWindow(ScreboUI screboUI, RetroItem retroItem, OnOkClicked onOkClicked) {
		super(screboUI);
		this.retroItem = (RetroItem) Helper.slowDeepClone(retroItem);
		this.onOkClicked = onOkClicked;
		HorizontalLayout horizontalLayout = new HorizontalLayout();

		horizontalLayout.addComponent(new Label("Title:"));

		TextField titleTextField = new TextField();
		titleTextField.setValue(retroItem.getTitle());
		titleTextField.addTextChangeListener(event -> {
			EditRetroItemWindow.this.retroItem.setTitle(event.getText());
			setOkButtonEnabled();
		});
		setOkButtonEnabled();

		horizontalLayout.addComponent(titleTextField);
		verticalLayout.addComponent(horizontalLayout);

		cancelButton.addClickListener(event -> {
			close();
		});

		okButton.addClickListener(event -> {
			exitedWithOk = true;
			close();
			onOkClicked.onOkClicked(EditRetroItemWindow.this.retroItem);
		});

		horizontalLayout = new HorizontalLayout();

		horizontalLayout.addComponent(okButton);
		horizontalLayout.addComponent(cancelButton);
		verticalLayout.addComponent(horizontalLayout);

		verticalLayout.setWidth("300px");
		verticalLayout.setHeight("200px");
		setContent(verticalLayout);
		titleTextField.focus();
	}

	protected void setOkButtonEnabled() {
		okButton.setEnabled(!Strings.isNullOrEmpty(retroItem.getTitle()));
	}

	public boolean isExitedWithOk() {
		return exitedWithOk;
	}

	public RetroItem getRetroItem() {
		return retroItem;
	}
}
