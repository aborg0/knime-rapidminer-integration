/**
 * 
 */
package org.knime.core.node.defaultnodesettings;

import java.util.Arrays;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Container to the project location, the editability, and the snapshot
 * property.
 * <p>
 * <b>Terms</b>
 * <ul>
 * <li><b>location of project</b> - an string that specifies the location of the
 * project for the special project type.</li>
 * <li><b>editability</b> - if this is specified, the editor will be active, and
 * allows to change the loaded project, and use the edited result.</li>
 * <li><b>snapshot</b> - creates a snapshot based on the current state of the
 * selected project, or always use the actual state of the project. (There have
 * to be an option to refresh the snapshot.)</li>
 * <li><b>content</b> - a byte array representation of the project, which can be
 * opened in some way.</li>
 * </ul>
 * 
 * @author Gabor
 * @param <ProjectType>
 *            The type of the project it belongs to.
 */
public abstract class SettingsModelProject<ProjectType> extends
		SettingsModelString {
	/** Is the project editable? */
	private boolean editability;
	/**
	 * Should we create a snapshot from the currently available state of the
	 * project?
	 */
	private boolean snapshot;
	/** The byte array representation of the project (snapshot, or edited). */
	private byte[] content;

	/**
	 * @param configName
	 *            The name of the config.
	 * @param defaultProjectLocation
	 *            The default location of the project.
	 * @param defaultEditability
	 *            The default value for editability.
	 * @param defaultSnapshot
	 *            The default value for snapshot.
	 * @param defaultContent
	 *            The default content if default project location is not
	 *            specified, can be {@code null}.
	 */
	public SettingsModelProject(final String configName,
			final String defaultProjectLocation,
			final boolean defaultEditability, final boolean defaultSnapshot,
			final byte[] defaultContent) {
		super(configName, defaultProjectLocation);
		if (!isSupportsEdit() && defaultEditability) {
			throw new IllegalStateException(
					"Edit is not supported by this project type: "
							+ getClass().getSimpleName());
		}
		this.editability = defaultEditability;
		if (!isSupportsSnapshot() && defaultSnapshot) {
			throw new IllegalStateException(
					"Snapshot is not supported by this project type: "
							+ getClass().getSimpleName());
		}
		this.snapshot = defaultSnapshot || defaultEditability;
		this.content = cloneArray(defaultContent);
	}

	/**
	 * Clones an array.
	 * 
	 * @param array
	 *            A nullable byte array.
	 * @return A clone of the array, or null.
	 */
	private static byte[] cloneArray(final byte[] array) {
		return array == null ? null : array.clone();
	}

	/**
	 * @return whether this type of project supports the edit of the project, or
	 *         not.
	 */
	public abstract boolean isSupportsEdit();

	/**
	 * @return whether this type of project supports creating and loading of
	 *         snapshots or not.
	 */
	public abstract boolean isSupportsSnapshot();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelString#
	 * loadSettingsForDialog(org.knime.core.node.NodeSettingsRO,
	 * org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void loadSettingsForDialog(final NodeSettingsRO settings,
			final PortObjectSpec[] specs) throws NotConfigurableException {
		try {
			super.loadSettingsForDialog(settings, specs);
			// use the current value, if no value is stored in the settings
			setEditability(settings.getBoolean(getEditableConfigKey(),
					editability));
			setSnapshot(settings.getBoolean(getSnapshotConfigKey(), snapshot));
			setContent(settings.getByteArray(getContentConfigKey(), content));
		} catch (final IllegalArgumentException e) {
			// if the value is not accepted, keep the old value.
		}
	}

	/**
	 * @param byteArray
	 *            the new content.
	 */
	protected void setContent(final byte[] byteArray) {
		if (!isSupportsEdit() && !isSupportsSnapshot() && byteArray != null) {
			throw new IllegalArgumentException(
					"Editing and snapshots are not supported, so you cannot specify content.");
		}
		final boolean notify = !Arrays.equals(byteArray, content);
		content = cloneArray(byteArray);
		if (notify) {
			notifyChangeListeners();
		}
	}

	/**
	 * @param snap
	 *            the new value of snapshot.
	 */
	void setSnapshot(final boolean snap) {
		if (!isSupportsSnapshot() && snap) {
			throw new IllegalArgumentException("Snapshot is not supported.");
		}
		final boolean notify = snap != snapshot;
		snapshot = snap;
		if (notify) {
			notifyChangeListeners();
		}
	}

	/**
	 * @param edit
	 *            the new value of editability.
	 */
	void setEditability(final boolean edit) {
		if (!isSupportsEdit() && edit) {
			throw new IllegalArgumentException("Editing is not supported.");
		}
		final boolean notify = editability != edit;
		editability = edit;
		if (notify) {
			notifyChangeListeners();
			if (edit) {
				setSnapshot(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelString#
	 * loadSettingsForModel(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void loadSettingsForModel(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		try {
			super.loadSettingsForModel(settings);
			// use the current value, if no value is stored in the settings
			setEditability(settings.getBoolean(getEditableConfigKey(),
					editability));
			setSnapshot(settings.getBoolean(getSnapshotConfigKey(), snapshot));
			setContent(settings.getByteArray(getContentConfigKey(), content));
		} catch (final IllegalArgumentException e) {
			// if the value is not accepted, keep the old value.
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModelString#getModelTypeID
	 * ()
	 */
	@Override
	protected String getModelTypeID() {
		return super.getModelTypeID() + ".project";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelString#
	 * saveSettingsForModel(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsForModel(final NodeSettingsWO settings) {
		super.saveSettingsForModel(settings);
		settings.addBoolean(getEditableConfigKey(), editability);
		settings.addBoolean(getSnapshotConfigKey(), snapshot);
		settings.addByteArray(getContentConfigKey(), content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelString#
	 * saveSettingsForDialog(org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsForDialog(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		saveSettingsForModel(settings);
	}

	/** @return The config key for editable from the settings. */
	protected String getEditableConfigKey() {
		return getConfigName() + ".editable";
	}

	/** @return The config key for snapshot from the settings. */
	protected String getSnapshotConfigKey() {
		return getConfigName() + ".snapshot";
	}

	/** @return The config key to access the content from the settings. */
	protected String getContentConfigKey() {
		return getConfigName() + ".content";
	}

	/**
	 * @return the editability
	 */
	public final boolean isEditability() {
		return editability;
	}

	/**
	 * @return the snapshot
	 */
	public final boolean isSnapshot() {
		return snapshot;
	}

	/**
	 * @return the content
	 */
	public final byte[] getContent() {
		return cloneArray(content);
	}

	/**
	 * Refreshes/saves the snapshot, if {@link #isSupportsSnapshot()}.
	 * 
	 * @throws InvalidSettingsException
	 *             if the refresh was unsuccessful.
	 */
	public abstract void refreshSnapshot() throws InvalidSettingsException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModelString#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "[editability: " + editability
				+ ", snapshot: " + snapshot + "]";
	}

	@Override
	protected void validateSettingsForModel(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		super.validateSettingsForModel(settings);
		final String url = settings.getString(getConfigName());
		final byte[] bs = settings.getByteArray(getContentConfigKey());
		final boolean editable = settings.getBoolean(getEditableConfigKey());
		final boolean snap = settings.getBoolean(getSnapshotConfigKey());
		if (!editable && !snap && bs != null) {
			throw new InvalidSettingsException(
					"Cannot store the content for a project if it is not editable, not snapshotable. ("
							+ getClass().getSimpleName() + ")");
		}
		if (bs == null && url == null) {
			throw new InvalidSettingsException(
					"No project selected, and no content stored. ("
							+ getClass().getSimpleName() + ")");
		}
		if (snap && bs == null) {
			throw new InvalidSettingsException("Snapshot was not created for "
					+ url);
		}
	}

	/**
	 * Loads the project from the location specified by
	 * {@link #getStringValue()}.
	 * 
	 * @return The new project.
	 * @throws Exception
	 *             Problem loading the project.
	 */
	public ProjectType loadFromLocation() throws Exception {
		return loadFromLocation(getStringValue());
	}

	/**
	 * Loads the project from {@code location}.
	 * 
	 * @param location
	 *            The location to load from.
	 * @return The new project.
	 * @throws Exception
	 *             Problem loading the project.
	 */
	protected abstract ProjectType loadFromLocation(String location)
			throws Exception;

	/**
	 * Loads the project from {@link #getContent()}.
	 * 
	 * @return The new project.
	 * @throws Exception
	 *             Problem loading the project.
	 */
	public ProjectType loadFromContent() throws Exception {
		return loadFromContent(getContent());
	}

	/**
	 * Loads the project from {@code content}.
	 * 
	 * @param contentArray
	 *            The content as a byte array.
	 * @return The new project.
	 * @throws Exception
	 *             Problem loading the project.
	 */
	protected abstract ProjectType loadFromContent(byte[] contentArray)
			throws Exception;

	/**
	 * Loads the project based on the current settings.
	 * 
	 * @param forceLoadFromLocation
	 *            if {@code true}, the project will be loaded from the specified
	 *            ({@link #getStringValue()}) location.
	 * @return The new project.
	 * @throws Exception
	 *             An error occurred loading the project.
	 */
	public ProjectType loadProject(final boolean forceLoadFromLocation)
			throws Exception {
		final boolean snap = isSnapshot();
		final byte[] cont = getContent();
		final String location = getStringValue();
		return loadProject(forceLoadFromLocation, snap, cont, location);
	}

	/**
	 * The loadProject which do not use the stored values, ideal for validation.
	 * 
	 * @param forceLoadFromLocation
	 *            if {@code true}, the project will be loaded from the specified
	 *            ({@link #getStringValue()}) location.
	 * @param snap
	 *            see term snapshot
	 * @param cont
	 *            see term content
	 * @param location
	 *            see term location
	 * @return The new project.
	 * @throws Exception
	 *             An error occurred loading the project.
	 */
	protected ProjectType loadProject(final boolean forceLoadFromLocation,
			final boolean snap, final byte[] cont, final String location)
			throws Exception {
		if (!snap || forceLoadFromLocation || cont == null) {
			return loadFromLocation(location);
		}
		return loadFromContent(cont);
	}
}
