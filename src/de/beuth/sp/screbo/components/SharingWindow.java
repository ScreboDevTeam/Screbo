package de.beuth.sp.screbo.components;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.beuth.sp.screbo.ScreboServlet;
import de.beuth.sp.screbo.ScreboUI;
import de.beuth.sp.screbo.database.MyCouchDbRepositorySupport.TransformationRunnable;
import de.beuth.sp.screbo.database.Retrospective;
import de.beuth.sp.screbo.database.Retrospective.Right;
import de.beuth.sp.screbo.database.User;
import de.beuth.sp.screbo.database.UserRepository;

@SuppressWarnings("serial")
public class SharingWindow extends ScreboWindow {
	protected static class UserWrapper {
		protected User user;

		public UserWrapper(User user) {
			super();
			this.user = user;
		}

		@Override
		public String toString() {
			return user.getDisplayName();
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			UserWrapper other = (UserWrapper) obj;
			if (user == null) {
				if (other.user != null) {
					return false;
				}
			} else if (!user.equals(other.user)) {
				return false;
			}
			return true;
		}

	}

	protected final Retrospective retrospective;
	protected final Table table = new Table();
	protected final List<UserWrapper> userWrapperList = Lists.newArrayList();
	protected final Map<ComboBox, ComboBox> userRights = Maps.newHashMap();

	protected UserWrapper getFromId(List<UserWrapper> userWrapperList, String id) {
		for (UserWrapper userWrapper : userWrapperList) {
			if (id.equals(userWrapper.getUser().getId())) {
				return userWrapper;
			}
		}
		return null;
	}

	protected SharingWindow(ScreboUI screboUI, Retrospective retrospective) {
		super(screboUI);
		this.retrospective = retrospective;

		setWidth("830px");
		setHeight("430px");
		setCaption("share retrospective");
		setResizable(true);
		setModal(true);
		center();

		// Create a table and add a style to allow setting the row height in theme.

		table.setSizeFull();

		/* Define the names and data types of columns.
		 * The "default value" parameter is meaningless here. */
		table.addContainerProperty("user", ComboBox.class, null);
		table.addContainerProperty("rights", ComboBox.class, null);

		List<User> userList = ScreboServlet.getUserRepository().getAll();
		List<UserWrapper> userWrapperListIncludingMe = Lists.newArrayList();
		User myUser = UserRepository.getUserFromSession();
		for (User user : userList) {
			UserWrapper userWrapper = new UserWrapper(user);
			if (!Objects.equals(user.getId(), myUser.getId())) {
				userWrapperList.add(userWrapper);
			}
			userWrapperListIncludingMe.add(userWrapper);
		}

		for (Entry<String, Right> entry : retrospective.getRights().entrySet()) {
			boolean isNotMe = !Objects.equals(entry.getKey(), myUser.getId());

			ComboBox userComboBox = createUserComboBox(isNotMe ? userWrapperList : userWrapperListIncludingMe, entry);
			userComboBox.select(getFromId(userWrapperListIncludingMe, entry.getKey()));
			userComboBox.setEnabled(isNotMe);
			ComboBox rightsComboBox = createRightsComboBox(entry);
			rightsComboBox.select(entry.getValue());
			rightsComboBox.setEnabled(isNotMe);
			userRights.put(userComboBox, rightsComboBox);
			table.addItem(new Object[]{userComboBox, rightsComboBox}, entry);
		}

		addEmptyItem();

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		Button okButton = new Button("ok");
		horizontalLayout.addComponent(okButton);
		Button cancelButton = new Button("cancel");
		horizontalLayout.addComponent(cancelButton);

		okButton.addClickListener(event -> {
			save();
			close();
		});

		cancelButton.addClickListener(event -> {
			close();
		});

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponent(table);
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.setSizeFull();
		verticalLayout.setExpandRatio(table, 1);
		setContent(verticalLayout);
	}

	protected void save() {
		try {
			ScreboServlet.getRetrospectiveRepository().update(retrospective, new TransformationRunnable<Retrospective>() {

				@Override
				public void applyChanges(Retrospective retrospective) throws Exception {
					retrospective.getRights().clear();
					for (Entry<ComboBox, ComboBox> entry : userRights.entrySet()) {
						UserWrapper userWrapper = (UserWrapper) entry.getKey().getValue();
						if (userWrapper != null) {
							retrospective.getRights().put(userWrapper.getUser().getId(), (Right) entry.getValue().getValue());
						}
					}

				}
			});
		} catch (Exception e) {
			logger.error("Could not write to database.", e);
			screboUI.fireCouldNotWriteToDatabaseEvent(e);
		}
	}

	protected ComboBox createUserComboBox(final List<UserWrapper> userWrapperList, final Object itemId) {
		ComboBox userComboBox = new ComboBox();
		userComboBox.setTextInputAllowed(true);
		userComboBox.addItems(userWrapperList);
		userComboBox.setWidth("100%");
		userComboBox.setNullSelectionAllowed(true);
		userComboBox.addValueChangeListener(event -> {
			if (Objects.equals(table.lastItemId(), itemId)) {
				addEmptyItem();
			}
		});
		return userComboBox;
	}

	protected void addEmptyItem() {
		Object newItemId = new Object();
		ComboBox userComboBox = createUserComboBox(userWrapperList, newItemId);
		ComboBox rightsComboBox = createRightsComboBox(newItemId);
		rightsComboBox.select(Right.EDIT);
		userRights.put(userComboBox, rightsComboBox);
		table.addItem(new Object[]{userComboBox, rightsComboBox}, newItemId);
	}

	protected ComboBox createRightsComboBox(final Object itemId) {
		ComboBox rightsComboBox = new ComboBox();
		rightsComboBox.addItems(Right.VIEW, Right.EDIT, Right.NONE);
		rightsComboBox.setWidth("100%");
		rightsComboBox.setNullSelectionAllowed(false);
		rightsComboBox.setTextInputAllowed(false);
		rightsComboBox.addValueChangeListener(event -> {
			if (Objects.equals(table.lastItemId(), itemId)) {
				addEmptyItem();
			}
		});
		return rightsComboBox;
	}
}
