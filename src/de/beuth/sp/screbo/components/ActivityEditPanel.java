package de.beuth.sp.screbo.components;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.Activity;
import de.beuth.sp.screbo.database.Activity.ActivityPriority;
import de.beuth.sp.screbo.database.MyCouchDbRepositorySupport.TransformationRunnable;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;
import de.beuth.sp.screbo.eventBus.events.DisplayErrorMessageEvent;

@SuppressWarnings("serial")
public class ActivityEditPanel extends VerticalLayout {
	protected static final Logger logger = LogManager.getLogger();
	protected final ScreboUI screboUI;
	protected final Retrospective retrospective;
	protected final Activity activity;

	protected Runnable onReturnToOverview;
	protected TextField actTxtAct = new TextField();
	protected DateField actTxtDate = new DateField();
	protected ComboBox actDropPrio = new ComboBox();
	protected Button buttonSave = new Button("");

	protected void updateSaveButtonState(String text, Date date) {
		buttonSave.setEnabled(!Objects.equals(text, "") && date != null);
	}

	public ActivityEditPanel(ScreboUI screboUI, Retrospective retrospective, final Activity activity) {
		super();
		this.screboUI = screboUI;
		this.retrospective = retrospective;
		this.activity = activity;
		setStyleName(getClass().getSimpleName());
		setSizeFull();

		User user = UserRepository.getUserFromSession();

		VerticalLayout editPane = new VerticalLayout();

		Label actLblAct = new Label("Activity");
		actLblAct.setStyleName("boardLbl");
		Label actLblDate = new Label("Target Date");
		actLblDate.setStyleName("boardLbl");
		Label actLblPrio = new Label("Priority");
		actLblPrio.setStyleName("boardLbl");

		actTxtAct.setImmediate(true);
		actTxtAct.setStyleName("boardInput");
		actTxtAct.addTextChangeListener(e -> {
			updateSaveButtonState(e.getText(), actTxtDate.getValue());
		});

		actTxtDate.setImmediate(true);
		actTxtDate.setStyleName("boardInput");
		actTxtDate.addValueChangeListener(e -> {
			updateSaveButtonState(actTxtAct.getValue(), actTxtDate.getValue());
		});

		actDropPrio.setStyleName("boardInput");
		actDropPrio.addItem(ActivityPriority.HIGH);
		actDropPrio.addItem(ActivityPriority.NORMAL);
		actDropPrio.addItem(ActivityPriority.LOW);
		actDropPrio.setNullSelectionAllowed(false);
		actDropPrio.setInvalidAllowed(false);
		actDropPrio.setTextInputAllowed(false);
		actDropPrio.setNewItemsAllowed(false);
		actDropPrio.setConverter(new Converter<Object, ActivityPriority>() {

			Map<ActivityPriority, Object> translations = Maps.newHashMap();

			{
				translations.put(ActivityPriority.HIGH, "High");
				translations.put(ActivityPriority.NORMAL, "Normal");
				translations.put(ActivityPriority.LOW, "Low");
			}

			@Override
			public ActivityPriority convertToModel(Object value, Class<? extends ActivityPriority> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
				for (Entry<ActivityPriority, Object> entry : translations.entrySet()) {
					if (Objects.equals(value, entry.getValue())) {
						return entry.getKey();
					}
				}

				return (ActivityPriority) value;
			}

			@Override
			public Object convertToPresentation(ActivityPriority value, Class<? extends Object> targetType, Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
				return translations.get(value);
			}

			@Override
			public Class<ActivityPriority> getModelType() {
				return ActivityPriority.class;
			}

			@Override
			public Class<Object> getPresentationType() {
				return Object.class;
			}
		});

		actTxtDate.setRangeStart(Date.from(ZonedDateTime.now(user.getTimeZoneId()).withHour(0).withMinute(0).withSecond(0).withNano(0).toInstant()));
		actTxtDate.setLocale(Locale.forLanguageTag(user.getLocale()));

		if (activity != null) {
			actTxtAct.setValue(activity.getDescription());
			actTxtDate.setValue(Date.from(activity.getDateOfLatestRealization().toInstant()));
			actDropPrio.select(activity.getPriority());
		} else {
			actDropPrio.select(ActivityPriority.NORMAL);
		}

		HorizontalLayout buttonLayout = new HorizontalLayout();

		buttonSave.setImmediate(true);
		buttonSave.setDescription(((activity == null) ? "Add" : "Save") + " the activity");
		buttonSave.setStyleName("addSaveBtn");
		buttonLayout.addComponent(buttonSave);
		buttonSave.addClickListener(event -> {
			if (onReturnToOverview != null) {
				onReturnToOverview.run();
			}
			modifyRetrospective(new TransformationRunnable<Retrospective>() {

				@Override
				public void applyChanges(Retrospective retrospective) throws Exception {
					final Activity activityToSave;
					if (activity == null) {
						activityToSave = new Activity();
						retrospective.getActivities().add(activityToSave);
					} else {
						activityToSave = retrospective.getActivities().getFromID(activity.getId());
						if (activityToSave == null) {
							throw new Exception("Activitiy already deleted.");
						}
					}
					activityToSave.setDescription(actTxtAct.getValue());
					activityToSave.setDateOfLatestRealization(ZonedDateTime.ofInstant(actTxtDate.getValue().toInstant(), ZoneId.systemDefault()));
					activityToSave.setPriority((ActivityPriority) actDropPrio.getConvertedValue());

				}
			});

		});

		if (activity != null) {
			Button buttonDelete = new Button("Delete the activity");
			buttonDelete.setDescription("Delete the activity");
			//	buttonDelete.setStyleName("addSaveBtn");
			buttonLayout.addComponent(buttonDelete);
			buttonDelete.addClickListener(event -> {
				if (onReturnToOverview != null) {
					onReturnToOverview.run();
				}
				modifyRetrospective(new TransformationRunnable<Retrospective>() {

					@Override
					public void applyChanges(Retrospective retrospective) throws Exception {
						retrospective.getActivities().removeItemWithId(activity.getId());
					}
				});
			});
		}

		Button buttonCancel = new Button("Return to overview");
		buttonCancel.setDescription("Return to overview");
		//buttonCancel.setStyleName("addSaveBtn");
		buttonLayout.addComponent(buttonCancel);
		buttonCancel.addClickListener(event -> {
			if (onReturnToOverview != null) {
				onReturnToOverview.run();
			}
		});

		editPane.addComponent(actLblAct);
		editPane.addComponent(actTxtAct);

		editPane.addComponent(actLblDate);
		editPane.addComponent(actTxtDate);

		editPane.addComponent(actLblPrio);
		editPane.addComponent(actDropPrio);

		editPane.addComponent(buttonLayout);

		addComponent(editPane);
		setComponentAlignment(editPane, Alignment.TOP_LEFT);
		updateSaveButtonState(actTxtAct.getValue(), actTxtDate.getValue());
	}

	public void setOnReturnToOverview(Runnable onReturnToOverview) {
		this.onReturnToOverview = onReturnToOverview;
	}

	protected boolean modifyRetrospective(TransformationRunnable<Retrospective> transformationRunnable) {
		try {
			return ScreboServlet.getRetrospectiveRepository().update(retrospective, transformationRunnable);
		} catch (Exception e) {
			logger.error("Could not write to database.", e);
			screboUI.getEventBus().fireEvent(new DisplayErrorMessageEvent("Could not write to database.", e));
		}
		return false;
	}
}
