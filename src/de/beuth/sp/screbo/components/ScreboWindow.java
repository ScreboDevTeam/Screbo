package de.beuth.sp.screbo.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Window;

import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class ScreboWindow extends Window {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;

	protected ScreboWindow(ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;
	}
}
