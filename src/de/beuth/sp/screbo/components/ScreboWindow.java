package de.beuth.sp.screbo.components;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Window;

import de.beuth.sp.screbo.ScreboUI;

@SuppressWarnings("serial")
public class ScreboWindow extends Window implements Serializable {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;

	protected ScreboWindow(ScreboUI screboUI) {
		super();
		this.screboUI = screboUI;
		setStyleName(getClass().getSimpleName());
	}
}
