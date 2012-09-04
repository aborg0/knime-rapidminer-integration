package com.mind_era.knime_rapidminer.knime.nodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentRapidMinerProject;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
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
		// addDialogComponent(new DialogComponentFileChooser(
		// new SettingsModelString(RapidMinerNodeModel.CFGKEY_PROCESS,
		// RapidMinerNodeModel.DEFAULT_PROCESS), "ProcessFile",
		// ".rmp"));
		// createNewTab("RapidMiner Workflow Editor");
		// addDialogComponent(new DialogComponentRapidMinerEditor(
		// new SettingsModelString(
		// RapidMinerNodeModel.CFGKEY_PROCESS_CUSTOM,
		// RapidMinerNodeModel.DEFAULT_PROCESS_CUSTOM)));
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
