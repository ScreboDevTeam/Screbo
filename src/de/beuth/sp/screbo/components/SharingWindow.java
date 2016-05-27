package de.beuth.sp.screbo.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;

import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class SharingWindow extends ScreboWindow {

	protected SharingWindow(ScreboUI screboUI) {
		super(screboUI);

		// Create a table and add a style to allow setting the row height in theme.

		final Table table = new Table();
		table.setSizeFull();

		/* Define the names and data types of columns.
		 * The "default value" parameter is meaningless here. */
		table.addContainerProperty("Action", Button.class, null);
		table.addContainerProperty("User", ComboBox.class, null);
		table.addContainerProperty("Rights", ComboBox.class, null);

		table.addItem(new Object[]{new Button("Remove"), new ComboBox(), new ComboBox()}, 0);
		table.addItem(new Object[]{new Button("Add"), null, null}, 1);
		setContent(table);
	}

}
