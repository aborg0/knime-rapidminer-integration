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

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentRapidMinerProject;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelRapidMinerProject;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

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
public class RapidMinerNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring RapidMiner node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */
	protected RapidMinerNodeDialog() {
		super();
		final DialogComponentRapidMinerProject rapidMinerComponent = new DialogComponentRapidMinerProject(
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
				RapidMinerNodeDialog.this.setButtonText(rowIdColumnNameModel,
						this);
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
		createNewTab("Advanced");
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				RapidMinerNodeModel.CFGKEY_INFER_OUTPUT,
				RapidMinerNodeModel.DEFAULT_INFER_OUTPUT), "Infer output?"));
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
}
