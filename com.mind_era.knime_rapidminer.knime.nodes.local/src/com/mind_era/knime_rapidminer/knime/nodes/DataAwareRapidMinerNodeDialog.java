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
package com.mind_era.knime_rapidminer.knime.nodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DataAwareNodeDialogPane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentRapidMinerProject;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelRapidMinerProject;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * <code>NodeDialog</code> for the "RapidMiner" Node. Executes a RapidMiner
 * workflow.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Gabor Bakos
 */
public class DataAwareRapidMinerNodeDialog extends DataAwareNodeDialogPane {
	private final List<DialogComponent> m_dialogComponents;

	private JPanel m_compositePanel;

	private JPanel m_currentPanel;

	private String m_defaultTabTitle = "Options";

	private Box m_currentBox;

	private boolean m_horizontal = false;

	private BufferedDataTable[] filteredInputTables;

	private DialogComponentRapidMinerProject rapidMinerComponent;

	/**
	 * New pane for configuring RapidMiner node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected DataAwareRapidMinerNodeDialog() {
		super();
		m_dialogComponents = new ArrayList<DialogComponent>();
		createNewPanels();
		super.addTab(m_defaultTabTitle, m_compositePanel);

		rapidMinerComponent = new DialogComponentRapidMinerProject(
				new SettingsModelRapidMinerProject(
						RapidMinerNodeModel.CFGKEY_PROCESS_CUSTOM,
						RapidMinerNodeModel.DEFAULT_PROCESS_CUSTOM, true, true,
						null));

		addDialogComponent(rapidMinerComponent);
		createNewTab("Row ID");
		final SettingsModelString rowIdColumnNameModel = new SettingsModelString(
				RapidMinerNodeModel.CFGKEY_ROWID_COLUMN_NAME,
				RapidMinerNodeModel.DEFAULT_ROWID_COLUMN_NAME);
		setHorizontalPlacement(true);
		addDialogComponent(new DialogComponentString(rowIdColumnNameModel,
				"Row ID column name: "));
		final DialogComponentButton rowIdEnabler = new DialogComponentButton(
				"Use") {
			@Override
			protected void updateComponent() {
				super.updateComponent();
				DataAwareRapidMinerNodeDialog.this.setButtonText(
						rowIdColumnNameModel, this);
				rapidMinerComponent.setRowIdColumnName(rowIdColumnNameModel
						.isEnabled() ? rowIdColumnNameModel.getStringValue()
						: null);
			}
		};
		rowIdEnabler.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				rowIdColumnNameModel.setEnabled(!rowIdColumnNameModel
						.isEnabled());
				setButtonText(rowIdColumnNameModel, rowIdEnabler);
				rapidMinerComponent.setRowIdColumnName(rowIdColumnNameModel
						.isEnabled() ? rowIdColumnNameModel.getStringValue()
						: null);
			}
		});
		addDialogComponent(rowIdEnabler);
	}

	/**
	 * Sets the {@code rowIdEnabler}'s button text to the proper value.
	 * 
	 * @param rowIdColumnNameModel
	 *            The model based upon the value.
	 * @param rowIdEnabler
	 *            The {@link DialogComponentButton} to change the button text.
	 */
	void setButtonText(final SettingsModelString rowIdColumnNameModel,
			final DialogComponentButton rowIdEnabler) {
		rowIdEnabler.setText(!rowIdColumnNameModel.isEnabled() ? "Do not use"
				: "Use");
	}

	// /* (non-Javadoc)
	// * @see
	// org.knime.core.node.NodeDialogPane#saveSettingsTo(org.knime.core.node.NodeSettingsWO)
	// */
	// @Override
	// protected void saveSettingsTo(NodeSettingsWO arg0)
	// throws InvalidSettingsException {
	// // TODO Auto-generated method stub
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.DataAwareNodeDialogPane#loadSettingsFrom(org.knime
	 * .core.node.NodeSettingsRO, org.knime.core.node.BufferedDataTable[])
	 */
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			BufferedDataTable[] input) throws NotConfigurableException {
		// super.loadSettingsFrom(settings, input);
		filteredInputTables = Collections2.filter(Arrays.asList(input),
				new Predicate<BufferedDataTable>() {
					@Override
					public boolean apply(BufferedDataTable table) {
						return table != null;
					}
				}).toArray(new BufferedDataTable[0]);
		defaultLoadSettingsFrom(
				settings,
				Lists.transform(Arrays.asList(filteredInputTables),
						new Function<BufferedDataTable, DataTableSpec>() {
							@Override
							public DataTableSpec apply(BufferedDataTable table) {
								return table.getDataTableSpec();
							}
						}).toArray(new DataTableSpec[0]));
		rapidMinerComponent.setInputTables(filteredInputTables);
	}

	/**
	 * The implementation copied from the {@link DefaultNodeSettingsPane}.
	 * 
	 * @param settings
	 *            The node settings to read.
	 * @param specs
	 *            The specs to set.
	 * @throws NotConfigurableException
	 *             When we cannot configure a component.
	 */
	protected void defaultLoadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		assert settings != null;
		assert specs != null;

		for (DialogComponent comp : m_dialogComponents) {
			comp.loadSettingsFrom(settings, specs);
		}
		loadAdditionalSettingsFrom(settings, specs);
	}

	private void createNewPanels() {
		m_compositePanel = new JPanel();
		m_compositePanel.setLayout(new BoxLayout(m_compositePanel,
				BoxLayout.Y_AXIS));
		m_currentPanel = m_compositePanel;
		m_currentBox = createBox(m_horizontal);
		m_currentPanel.add(m_currentBox);
	}

	/**
	 * Sets the title of the default tab that is created and used until you call
	 * {@link #createNewTab}.
	 * 
	 * @param tabTitle
	 *            the new title of the first tab. Can't be null or empty.
	 * @throws IllegalArgumentException
	 *             if the title is already used by another tab, or if the
	 *             specified title is null or empty.
	 */
	public void setDefaultTabTitle(final String tabTitle) {
		if ((tabTitle == null) || (tabTitle.length() == 0)) {
			throw new IllegalArgumentException("The title of a tab can't be "
					+ "null or empty.");
		}
		if (tabTitle.equals(m_defaultTabTitle)) {
			return;
		}
		// check if we already have a tab with the new title
		if (super.getTab(tabTitle) != null) {
			throw new IllegalArgumentException("A tab with the specified new"
					+ " name (" + tabTitle + ") already exists.");
		}
		super.renameTab(m_defaultTabTitle, tabTitle);
		m_defaultTabTitle = tabTitle;
	}

	/**
	 * Creates a new tab in the dialog. All components added from now on are
	 * placed in that new tab. After creating a new tab the previous tab is no
	 * longer accessible. If a tab with the same name was created before an
	 * Exception is thrown. The new panel in the new tab has no group set (i.e.
	 * has no border). The new tab is placed at the specified position (or at
	 * the right most position, if the index is too big).
	 * 
	 * @param tabTitle
	 *            the title of the new tab to use from now on. Can't be null or
	 *            empty.
	 * @param index
	 *            the index to place the new tab at. Can't be negative.
	 * @throws IllegalArgumentException
	 *             if you specify a title that is already been used by another
	 *             tab. Or if the specified title is null or empty.
	 * @see #setDefaultTabTitle(String)
	 */
	public void createNewTabAt(final String tabTitle, final int index) {
		if ((tabTitle == null) || (tabTitle.length() == 0)) {
			throw new IllegalArgumentException("The title of a tab can't be "
					+ "null nor empty.");
		}
		// check if we already have a tab with the new title
		if (super.getTab(tabTitle) != null) {
			throw new IllegalArgumentException("A tab with the specified new"
					+ " name (" + tabTitle + ") already exists.");
		}
		createNewPanels();
		super.addTabAt(index, tabTitle, m_compositePanel);
	}

	/**
	 * Creates a new tab in the dialog. All components added from now on are
	 * placed in that new tab. After creating a new tab the previous tab is no
	 * longer accessible. If a tab with the same name was created before an
	 * Exception is thrown. The new panel in the new tab has no group set (i.e.
	 * has no border). The tab is placed at the right most position.
	 * 
	 * @param tabTitle
	 *            the title of the new tab to use from now on. Can't be null or
	 *            empty.
	 * @throws IllegalArgumentException
	 *             if you specify a title that is already been used by another
	 *             tab. Or if the specified title is null or empty.
	 * @see #setDefaultTabTitle(String)
	 */
	public void createNewTab(final String tabTitle) {
		createNewTabAt(tabTitle, Integer.MAX_VALUE);
	}

	/**
	 * Brings the specified tab to front and shows its components.
	 * 
	 * @param tabTitle
	 *            the title of the tab to select. If the specified title doesn't
	 *            exist, this method does nothing.
	 */
	public void selectTab(final String tabTitle) {
		setSelected(tabTitle);
	}

	/**
	 * Creates a new dialog component group and closes the current one. From now
	 * on the dialog components added with the addDialogComponent method are
	 * added to the current group. The group is a bordered and titled panel.
	 * 
	 * @param title
	 *            - the title of the new group.
	 */
	public void createNewGroup(final String title) {
		checkForEmptyBox();
		m_currentPanel = createSubPanel(title);
		m_currentBox = createBox(m_horizontal);
		m_currentPanel.add(m_currentBox);
	}

	private JPanel createSubPanel(final String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), title));
		m_compositePanel.add(panel);
		return panel;
	}

	/**
	 * Closes the current group. Further added dialog components are added to
	 * the default panel outside any border.
	 * 
	 */
	public void closeCurrentGroup() {
		checkForEmptyBox();
		if (m_currentPanel.getComponentCount() == 0) {
			m_compositePanel.remove(m_currentPanel);
		}
		m_currentPanel = m_compositePanel;
		m_currentBox = createBox(m_horizontal);
		m_currentPanel.add(m_currentBox);
	}

	/**
	 * Add a new DialogComponent to the underlying dialog. It will automatically
	 * be added in the dialog and saved/loaded from/to the config.
	 * 
	 * @param diaC
	 *            component to be added
	 */
	public void addDialogComponent(final DialogComponent diaC) {
		m_dialogComponents.add(diaC);
		m_currentBox.add(diaC.getComponentPanel());
		addGlue(m_currentBox, m_horizontal);
	}

	/**
	 * Changes the orientation the components get placed in the dialog.
	 * 
	 * @param horizontal
	 *            <code>true</code> if the next components should be placed next
	 *            to each other or <code>false</code> if the next components
	 *            should be placed below each other.
	 */
	public void setHorizontalPlacement(final boolean horizontal) {
		if (m_horizontal != horizontal) {
			m_horizontal = horizontal;
			checkForEmptyBox();
			m_currentBox = createBox(m_horizontal);
			m_currentPanel.add(m_currentBox);
		}
	}

	/**
	 * Save settings of all registered <code>DialogComponents</code> into the
	 * configuration object.
	 * 
	 * @param settings
	 *            the <code>NodeSettings</code> to write into
	 * @throws InvalidSettingsException
	 *             if the user has entered wrong values
	 */
	@Override
	public final void saveSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		for (DialogComponent comp : m_dialogComponents) {
			comp.saveSettingsTo(settings);
		}

		saveAdditionalSettingsTo(settings);
	}

	/**
	 * This method can be overridden to load additional settings. Override this
	 * method if you have mixed input types (different port types).
	 * Alternatively, if your node only has ordinary data inputs, consider to
	 * overwrite the
	 * {@link #loadAdditionalSettingsFrom(NodeSettingsRO, DataTableSpec[])}
	 * method, which does the type casting already.
	 * 
	 * @param settings
	 *            the <code>NodeSettings</code> to read from
	 * @param specs
	 *            the input specs
	 * @throws NotConfigurableException
	 *             if the node can currently not be configured
	 */
	public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
			final PortObjectSpec[] specs) throws NotConfigurableException {
		DataTableSpec[] dtsArray = new DataTableSpec[specs.length];
		boolean canCallDTSMethod = true;
		for (int i = 0; i < dtsArray.length; i++) {
			PortObjectSpec s = specs[i];
			if (s instanceof DataTableSpec) {
				dtsArray[i] = (DataTableSpec) s;
			} else if (s == null) {
				dtsArray[i] = new DataTableSpec();
			} else {
				canCallDTSMethod = false;
			}
		}
		if (canCallDTSMethod) {
			loadAdditionalSettingsFrom(settings, dtsArray);
		}
	}

	/**
	 * Override hook to load additional settings when all input ports are data
	 * ports. This method is the specific implementation to
	 * {@link #loadAdditionalSettingsFrom(NodeSettingsRO, PortObjectSpec[])} if
	 * all input ports are data ports. All elements in the <code>specs</code>
	 * argument are guaranteed to be non-null.
	 * 
	 * @param settings
	 *            The settings of the node
	 * @param specs
	 *            The <code>DataTableSpec</code> of the input tables.
	 * @throws NotConfigurableException
	 *             If not configurable
	 */
	public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
			final DataTableSpec[] specs) throws NotConfigurableException {
	}

	/**
	 * This method can be overridden to save additional settings to the given
	 * settings object.
	 * 
	 * @param settings
	 *            the <code>NodeSettings</code> to write into
	 * @throws InvalidSettingsException
	 *             if the user has entered wrong values
	 */
	public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
			throws InvalidSettingsException {
		assert settings != null;
	}

	private void checkForEmptyBox() {
		if (m_currentBox.getComponentCount() == 0) {
			m_currentPanel.remove(m_currentBox);
		}
	}

	/**
	 * @param currentBox
	 * @param horizontal
	 */
	private static void addGlue(final Box box, final boolean horizontal) {
		if (horizontal) {
			box.add(Box.createVerticalGlue());
		} else {
			box.add(Box.createHorizontalGlue());
		}
	}

	/**
	 * @param horizontal
	 *            <code>true</code> if the layout is horizontal
	 * @return the box
	 */
	private static Box createBox(final boolean horizontal) {
		final Box box;
		if (horizontal) {
			box = new Box(BoxLayout.X_AXIS);
			box.add(Box.createVerticalGlue());
		} else {
			box = new Box(BoxLayout.Y_AXIS);
			box.add(Box.createHorizontalGlue());
		}
		return box;
	}
}
