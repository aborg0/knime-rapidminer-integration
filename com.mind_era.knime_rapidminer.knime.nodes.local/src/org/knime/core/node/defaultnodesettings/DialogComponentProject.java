/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2012 Mind Eratosthenes Kft.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.knime.core.node.defaultnodesettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.SimpleFileFilter;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mind_era.knime_rapidminer.knime.nodes.ProjectHandling;
import com.mind_era.knime_rapidminer.knime.nodes.util.XJTextField;

/**
 * The common {@link DialogComponent} for various {@link SettingsModelProject}s.
 * 
 * @author Gabor
 * @param <ProjectType>
 *            The type of the project.
 * @param <Model>
 *            The type of the {@link SettingsModelProject}.
 */
public abstract class DialogComponentProject<ProjectType, Model extends SettingsModelProject<ProjectType>>
		extends DialogComponent {
	protected final JTextField location = new XJTextField(40);
	protected final JCheckBox editable = new JCheckBox("Editable?");
	protected final JCheckBox alwaysReload = new JCheckBox(
			"Always reload on execution?");
	private ProjectHandling<ProjectType> editor;
	protected transient volatile boolean isLoading = false;

	public DialogComponentProject(final Model model) {
		this(model, false);
	}

	/**
	 * @param model
	 *            The {@link SettingsModelProject} for the
	 *            {@link DialogComponentProject}.
	 * @param allowSaveAs
	 *            If set, when editable a save dialog will appear to save the
	 *            project locally.
	 * @param extensions
	 *            Supported file name extensions.
	 */
	public DialogComponentProject(final Model model, final boolean allowSaveAs,
			final String... extensions) {
		super(model);
		final JLabel locationText = new JLabel("Location: ");
		location.setText(model.getStringValue());
		editable.setSelected(model.isEditability());
		alwaysReload.setSelected(!model.isSnapshot());
		final JPanel controlsPanel = new JPanel();
		final Box box = Box.createHorizontalBox();
		controlsPanel.add(locationText);
		controlsPanel.add(location);
		location.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				onChange(e);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				onChange(e);
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				onChange(e);
			}

			private void onChange(
					@SuppressWarnings("unused") final DocumentEvent e) {
				if (isLoading) {
					return;
				}
				if (allowSaveAs && editable.isSelected()) {
					editor.saveAs();
				}
				model.setStringValue(location.getText());
				try {
					final ProjectType project = model.loadFromLocation();
					editor.load(project);
					if (model.isEditability()) {
						model.setContent(editor.getContent());
					}
				} catch (final Exception e1) {
					// Do nothing
				}
				updateModel();
			}
		});
		controlsPanel.add(new JButton(new AbstractAction("Browse") {
			private static final long serialVersionUID = 3476346101038882980L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser chooser = new JFileChooser(location
						.getText());
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				final List<FileFilter> fileFilter = Lists.transform(
						Arrays.asList(extensions),
						new Function<String, FileFilter>() {
							@Override
							public FileFilter apply(final String extension) {
								return new SimpleFileFilter(extension
										.split("\\|"));
							}
						});
				// if extensions are defined
				if (fileFilter != null && fileFilter.size() > 0) {
					// disable "All Files" selection
					chooser.setAcceptAllFileFilterUsed(false);
					// set the file filter for the given extensions
					for (final FileFilter filter : fileFilter) {
						chooser.setFileFilter(filter);
					}
					// set the first filter as default filter
					chooser.setFileFilter(fileFilter.get(0));
				}
				final int returnVal = chooser.showDialog(getComponentPanel()
						.getParent(), null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String newFile;
					try {
						newFile = chooser.getSelectedFile().getAbsoluteFile()
								.toString();
						// check if the user has added the extension
						// if (!directoryOnly && m_fileFilter != null) {
						// boolean extensionFound = false;
						// for (final SimpleFileFilter filter : m_fileFilter) {
						// final String[] extensions = filter
						// .getValidExtensions();
						// for (final String extension : extensions) {
						// if (newFile.endsWith(extension)) {
						// extensionFound = true;
						// break;
						// }
						// }
						// if (extensionFound) {
						// break;
						// }
						// }
						// otherwise add the extension of the selected
						// FileFilter
						// if (!extensionFound) {
						// final FileFilter fileFilter = chooser
						// .getFileFilter();
						// if (fileFilter != null
						// && fileFilter instanceof SimpleFileFilter) {
						// final SimpleFileFilter filter = (SimpleFileFilter)
						// fileFilter;
						// final String[] extensions = filter
						// .getValidExtensions();
						// if (extensions != null
						// && extensions.length > 0) {
						// // append the first extension
						// newFile = newFile + extensions[0];
						// }
						// }
						// }
						// }
					} catch (final SecurityException se) {
						newFile = "<Error: " + se.getMessage() + ">";
					}
					// avoid adding the same string twice...
					// m_fileComboBox.removeItem(newFile);
					// m_fileComboBox.addItem(newFile);
					// m_fileComboBox.setSelectedItem(newFile);
					location.setText(newFile);
					getComponentPanel().revalidate();
				}
			}
		}));
		controlsPanel.add(editable);
		final ChangeListener editableListener = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				final byte[] oldContent = model.getContent();
				if (editable.isSelected()) {
					try {
						editor.load(model.loadFromContent());
					} catch (final Exception e1) {
						e1.printStackTrace();
						model.setContent(oldContent);
					}
					alwaysReload.setEnabled(false);
				} else {
					try {
						editor.load(model.loadFromLocation());
					} catch (final Exception e1) {
						// do nothing, use the current process
						model.setContent(oldContent);
					}
					alwaysReload.setEnabled(model.isSupportsSnapshot());
				}
				model.setEditability(editable.isSelected());
				controlsPanel.repaint();
			}
		};
		editable.addChangeListener(editableListener);

		controlsPanel.add(new JButton(new AbstractAction("Reload") {
			private static final long serialVersionUID = 6409006450012078064L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final ProjectType loaded = model.loadFromLocation();
					editor.load(loaded);
					model.setContent(editor.getContent());
				} catch (final Exception e1) {
					JOptionPane.showMessageDialog(
							getComponentPanel(),
							"Failed to load the content from: "
									+ model.getStringValue());
				}
				alwaysReload.setEnabled(model.isSupportsSnapshot());
			}
		}));
		controlsPanel.add(alwaysReload);
		if (allowSaveAs) {
			controlsPanel.add(new JButton(new AbstractAction("Save As") {
				private static final long serialVersionUID = -6001418326690240684L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					editor.saveAs();
				}
			}));
		}
		box.add(controlsPanel);
		getComponentPanel().setLayout(new BorderLayout());
		getComponentPanel().add(box, BorderLayout.NORTH);
		if (model.isSupportsEdit()) {
			final Entry<? extends Component, ? extends ProjectHandling<ProjectType>> editorEntry = createEditor(model);
			editor = editorEntry.getValue();
			getComponentPanel().add(editorEntry.getKey(), BorderLayout.CENTER);
		}
		getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				safeUpdateComponent(false);
			}
		});
		editableListener.stateChanged(null);
		safeUpdateComponent(true);
	}

	/**
	 * @param model
	 *            The model with the defaults.
	 * @return The component containing the editor and a {@link ProjectHandling}
	 *         instance.
	 */
	protected abstract Map.Entry<? extends Component, ? extends ProjectHandling<ProjectType>> createEditor(
			Model model);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#updateComponent()
	 */
	@Override
	protected void updateComponent() {
		isLoading = true;
		safeUpdateComponent(true);
		isLoading = false;
	}

	/**
	 * @param setText
	 * 
	 */
	protected void safeUpdateComponent(final boolean setText) {
		@SuppressWarnings("unchecked")
		final Model model = (Model) getModel();
		editable.setSelected(model.isEditability());
		alwaysReload.setSelected(!model.isSnapshot());
		if (setText) {
			location.setText(model.getStringValue());
		}
		getComponentPanel().repaint();
		if (model.isSupportsEdit()) {
			try {
				final ProjectType project = model.loadProject(false);
				editor.load(project);
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(null,
						"Cannot load project: " + e.getMessage());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.DialogComponent#
	 * validateSettingsBeforeSave()
	 */
	@Override
	protected void validateSettingsBeforeSave() throws InvalidSettingsException {
		updateModel();
	}

	/**
	 * 
	 */
	private void updateModel() {
		@SuppressWarnings("unchecked")
		final Model model = (Model) getModel();
		model.setSnapshot(editable.isSelected() || !alwaysReload.isSelected());
		model.setEditability(editable.isSelected());
		if (!location.getText().isEmpty()) {
			model.setStringValue(location.getText());
		}
		if (!model.isSnapshot()) {
			try {
				model.loadFromLocation();
			} catch (final Exception e) {
				// ignore errors?
				e.printStackTrace();
			}
			model.setContent(null);
		} else if (model.isEditability()) {
			try {
				model.setContent(editor.getContent());
			} catch (final Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(),
						"Unable to save the edited project.",
						JOptionPane.ERROR_MESSAGE);
				model.setContent(null);
			}
		} else {
			try {
				model.loadFromLocation();
			} catch (final Exception e) {
				// ignore errors?
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.DialogComponent#
	 * checkConfigurabilityBeforeLoad(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs)
			throws NotConfigurableException {
		if (editor != null) {
			editor.onInputSpecChange(specs);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#setEnabledComponents
	 * (boolean)
	 */
	@Override
	protected void setEnabledComponents(final boolean enabled) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#setToolTipText
	 * (java.lang.String)
	 */
	@Override
	public void setToolTipText(final String text) {
		getComponentPanel().setToolTipText(text);
	}
}
