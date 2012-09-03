/**
 * 
 */
package org.knime.core.node.defaultnodesettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.w3c.dom.Document;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.rapidminer.Process;
import com.rapidminer.io.process.XMLImporter;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.XMLException;

/**
 * A {@link SettingsModelProject} implementation for RapidMiner's
 * {@link Process}.
 * 
 * @author Gabor
 */
public class SettingsModelRapidMinerProject extends
		SettingsModelProject<Process> {

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
	 * @see SettingsModelProject
	 */
	public SettingsModelRapidMinerProject(final String configName,
			final String defaultProjectLocation,
			final boolean defaultEditability, final boolean defaultSnapshot,
			final byte[] defaultContent) {
		super(configName, defaultProjectLocation, defaultEditability,
				defaultSnapshot, defaultContent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModelProject#isSupportsEdit
	 * ()
	 */
	@Override
	public boolean isSupportsEdit() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelProject#
	 * isSupportsSnapshot()
	 */
	@Override
	public boolean isSupportsSnapshot() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModelProject#refreshSnapshot
	 * ()
	 */
	@Override
	public void refreshSnapshot() throws InvalidSettingsException {
		if (!isEditability() && !isSnapshot()) {
			throw new IllegalStateException(
					"Neither edit, nor snapshot can be restored.");
		}
		try {
			final Process p = loadFromLocation(getStringValue());
			final byte[] newContent = saveProcessAsByteArray(p);
			setContent(newContent);
		} catch (final MalformedURLException e) {
			throw new InvalidSettingsException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new InvalidSettingsException(e.getMessage(), e);
		} catch (final XMLException e) {
			throw new InvalidSettingsException(e.getMessage(), e);
		}
	}

	/**
	 * Saves the {@code process} to a new byte array.
	 * 
	 * @param process
	 *            A {@link Process}.
	 * @return The byte array representation of the {@code process}.
	 * @throws IOException
	 *             Error saving.
	 * @throws XMLException
	 *             Error generating XML.
	 */
	static byte[] saveProcessAsByteArray(final Process process)
			throws IOException, XMLException {
		final Document document = process.getRootOperator()
				.getDOMRepresentation();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLTools.stream(document, out, XMLImporter.PROCESS_FILE_CHARSET);
		final byte[] newContent = out.toByteArray();
		return newContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModel#createClone()
	 */
	@Override
	protected SettingsModelProject<Process> createClone() {
		return new SettingsModelRapidMinerProject(getConfigName(),
				getStringValue(), isEditability(), isSnapshot(), getContent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModelProject#
	 * validateSettingsForModel(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void validateSettingsForModel(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		super.validateSettingsForModel(settings);
		try {
			RapidMinerInit.init(false);
			RapidMinerInit.setPreferences();
			loadProject(false, settings.getBoolean(getSnapshotConfigKey()),
					settings.getByteArray(getContentConfigKey()),
					settings.getString(getConfigName()));
		} catch (final Exception e) {
			throw new InvalidSettingsException(e.getMessage(), e);
		}
	}

	@Override
	protected Process loadFromContent(final byte[] content) throws IOException,
			XMLException {
		try {
			return new Process(new ByteArrayInputStream(content));
		} catch (final NullPointerException e) {
			return new Process();
		} catch (final RuntimeException e) {
			e.printStackTrace();
			return new Process();
		}
	}

	@Override
	protected Process loadFromLocation(final String location)
			throws IOException, XMLException {
		if (location == null || location.isEmpty()) {
			return new Process();
		}
		try {
			final URL url = new URL(location);
			return new Process(url);
		} catch (final MalformedURLException e) {
			try {
				return new Process(new File(location));
			} catch (final RuntimeException e2) {
				e2.printStackTrace();
				return new Process();
			}
		}
	}
}
