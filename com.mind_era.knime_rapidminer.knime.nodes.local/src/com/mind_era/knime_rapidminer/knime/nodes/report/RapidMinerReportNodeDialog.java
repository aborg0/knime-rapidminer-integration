package com.mind_era.knime_rapidminer.knime.nodes.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumberEdit;
import org.knime.core.node.defaultnodesettings.DialogComponentRapidMinerProject;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.properties.celleditors.value.ConfigurationWizardValueCellEditor;
import com.rapidminer.gui.wizards.ConfigurationListener;
import com.rapidminer.gui.wizards.ConfigurationWizardCreator;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.parameter.ParameterTypeConfiguration;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.Parameters;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.plugin.Plugin;

/**
 * <code>NodeDialog</code> for the "RapidMinerReport" Node. Converts data to
 * figures using RapidMiner's Reporting extension.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Mind Eratosthenes Kft.
 */
public class RapidMinerReportNodeDialog extends NodeDialogPane implements
		RepositoryAccessor {

	private Operator reporterOperator;
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	private DialogComponentStringSelection formatSelection = new DialogComponentStringSelection(
			RapidMinerReportNodeModel.createImageFormat(), "Image format: ",
			RapidMinerReportNodeModel.POSSIBLE_IMAGE_FORMATS);
	private DialogComponentNumberEdit imageWidth = new DialogComponentNumberEdit(
			RapidMinerReportNodeModel.createImageWidth(), "Width"),
			imageHeight = new DialogComponentNumberEdit(
					RapidMinerReportNodeModel.createImageHeight(), "Height");
	private DataTableSpec[] lastSpecs;
	private Process process;
	private Parameters parameters;

	/**
	 * New pane for configuring the RapidMinerReport node.
	 */
	protected RapidMinerReportNodeDialog() {
		JPanel panel = getPanel(), subPanel = new JPanel(new FlowLayout());
		panel.setLayout(new BorderLayout());
		subPanel.add(formatSelection.getComponentPanel());
		subPanel.add(imageWidth.getComponentPanel());
		subPanel.add(imageHeight.getComponentPanel());
		panel.add(subPanel, BorderLayout.NORTH);
		// ReporterConfigurationWizardCreator creator = new
		// ReporterConfigurationWizardCreator();
		Class<? extends ConfigurationWizardCreator> cls;
		try {
			Plugin plugin = Plugin
					.getPluginByExtensionId(RapidMinerReportNodeModel.REPORTING_EXTENSION_ID);
			cls = loadClass(plugin);
		} catch (ClassNotFoundException e1) {
			throw new IllegalStateException("Not found: " + e1.getMessage(), e1);
		}
		process = new Process();
		parameters = new Parameters();
		ConfigurationWizardValueCellEditor valueCellEditor = new ConfigurationWizardValueCellEditor(
				new ParameterTypeConfiguration(cls,
						Collections.<String, String> emptyMap(),
						new ConfigurationListener() {
							@Override
							public Parameters getParameters() {
								return parameters;
							}

							@Override
							public void setParameters(Parameters parameters) {
								// TODO Auto-generated method stub
								RapidMinerReportNodeDialog.this.parameters
										.addAll(parameters);
								// System.out.println(parameters);
								// for (String string : parameters) {
								// System.out.println(string);
								// }
							}

							@Override
							public Process getProcess() {
								return process;
							}
						}, new Object[] { process.getRootOperator()
								.getInputPorts().createPort("KNIME") }));
		try {
			reporterOperator = OperatorService
					.createOperator(RapidMinerReportNodeModel.REPORTER_OPERATOR_ID);
			valueCellEditor.setOperator(reporterOperator);
			process.getRootOperator().getSubprocess(0)
					.addOperator(reporterOperator);
			process.getRootOperator()
					.getSubprocess(0)
					.getInnerSources()
					.getPortByIndex(0)
					.connectTo(
							reporterOperator.getInputPorts().getPortByIndex(0));
			if (lastSpecs != null) {
				process.getRootOperator().deliverInputMD(
						Collections
								.singletonList(DialogComponentRapidMinerProject
										.createMetaData(lastSpecs[0], false,
												null)));
			}
		} catch (OperatorCreationException e) {
			throw new IllegalStateException(
					"Cannot create the Reporter operator\n" + e.getMessage(), e);
		}
		Component editor = valueCellEditor.getTableCellEditorComponent(null,
				null, false, 0, 0);// arguments are not used
		valueCellEditor.getCellEditorValue();
		panel.add(editor, BorderLayout.CENTER);
	}

	/**
	 * Loads the
	 * {@link com.rapidminer.gui.wizards.ReporterConfigurationWizardCreator}
	 * class using the plugin classloader.
	 * 
	 * @param plugin
	 *            A {@link Plugin} instance.
	 * @return The class of the {@link ConfigurationWizardCreator}.
	 * @throws ClassNotFoundException
	 *             Should not happen when the Reporter plugin is properly
	 *             installed, happens when it is not.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends ConfigurationWizardCreator> loadClass(
			Plugin plugin) throws ClassNotFoundException {
		return (Class<? extends ConfigurationWizardCreator>) plugin
				.getClassLoader()
				.loadClass(
						"com.rapidminer.gui.wizards.ReporterConfigurationWizardCreator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.NodeDialogPane#saveSettingsTo(org.knime.core.node
	 * .NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		String rawValue;
		try {
			rawValue = parameters.getParameter("parameters");
		} catch (UndefinedParameterError e1) {
			throw new InvalidSettingsException(e1.getMessage(), e1);
		}
		List<String[]> parameters = ParameterTypeList
				.transformString2List(rawValue);
		String[] keys = new String[parameters.size()];
		String[] values = new String[parameters.size()];
		for (int i = parameters.size(); i-- > 0;) {
			keys[i] = parameters.get(i)[0];
			values[i] = parameters.get(i)[1];
		}
		settings.addStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERKEYS,
				keys);
		settings.addStringArray(
				RapidMinerReportNodeModel.CFGKEY_REPORTERVALUES, values);
		formatSelection.saveSettingsTo(settings);
		imageWidth.saveSettingsTo(settings);
		imageHeight.saveSettingsTo(settings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.NodeDialogPane#loadSettingsFrom(org.knime.core.node
	 * .NodeSettingsRO, org.knime.core.data.DataTableSpec[])
	 */
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		this.lastSpecs = specs;
		try {
			reporterOperator = OperatorService
					.createOperator(RapidMinerReportNodeModel.REPORTER_OPERATOR_ID);
		} catch (OperatorCreationException e1) {
			throw new NotConfigurableException(e1.getMessage(), e1);
		}
		formatSelection.loadSettingsFrom(settings, specs);
		String[] keys;
		try {
			keys = settings
					.getStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERKEYS);
			String[] values = settings
					.getStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERVALUES);
			assert keys.length == values.length : "keys: "
					+ Arrays.asList(keys) + "\nvalues: "
					+ Arrays.asList(values);
			reporterOperator.setParameter("renderer_name", "Plot View");
			reporterOperator.setParameter("reportable_type", "Data Table");
			imageWidth.loadSettingsFrom(settings, specs);
			imageHeight.loadSettingsFrom(settings, specs);

			reporterOperator.setParameter("image_width", Integer
					.toString(((SettingsModelInteger) imageWidth.getModel())
							.getIntValue()));
			reporterOperator.setParameter("image_height", Integer
					.toString(((SettingsModelInteger) imageHeight.getModel())
							.getIntValue()));

			List<String[]> params = new ArrayList<String[]>(keys.length);
			for (int i = 0; i < keys.length; ++i) {
				// parameters.setParameter(keys[i], values[i]);
				params.add(new String[] { keys[i], values[i] });
				// reporterOperator.setParameter(keys[i], values[i]);
			}
			reporterOperator.setParameter("parameters",
					ParameterTypeList.transformList2String(params));
			parameters.setParameter("parameters",
					ParameterTypeList.transformList2String(params));
			parameters.setParameter("renderer_name", "Plot View");
			parameters.setParameter("reportable_type", "Data Table");
			// Process process = new Process();
			// reporterOperator.remove();
			// process.getRootOperator().getSubprocess(0)
			// .addOperator(reporterOperator, 0);
			// // OutputPort knimePort = process.getRootOperator()
			// // .getOutputPorts()
			// // .createPort("KNIME");
			// //knimePort.disconnect();
			// OutputPort knimePort =
			// process.getRootOperator().getSubprocess(0).getInnerSources()
			// .getPortByIndex(0);
			// knimePort
			// .connectTo(
			// reporterOperator.getInputPorts().createPort("data"));
			process.checkProcess(new IOContainer(new MemoryExampleTable(
					KnimeExampleTable.createAttributes(specs[0], true,
							"KNIMERowID")).createExampleSet()));
			process.getRootOperator().deliverInputMD(
					Collections.singletonList(DialogComponentRapidMinerProject
							.createMetaData(specs[0], false, null)));
			String location = "//" + KnimeRepository.KNIME + "/"
					+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE;
			// process.getContext().setInputRepositoryLocation(0, location);
			process.setRepositoryAccessor(this);
			process.getContext().setInputRepositoryLocations(
					Collections.singletonList(location));
			process.getRootOperator().checkAll();
		} catch (InvalidSettingsException e) {
			throw new NotConfigurableException(e.getMessage(), e);
		}
	}
}
