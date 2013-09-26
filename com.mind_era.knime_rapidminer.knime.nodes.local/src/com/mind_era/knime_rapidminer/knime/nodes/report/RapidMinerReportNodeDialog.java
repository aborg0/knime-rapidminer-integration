package com.mind_era.knime_rapidminer.knime.nodes.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentRapidMinerProject;

import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.rapidminer.Process;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.properties.celleditors.value.ConfigurationWizardValueCellEditor;
import com.rapidminer.gui.wizards.ConfigurationListener;
import com.rapidminer.gui.wizards.ConfigurationWizardCreator;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterTypeConfiguration;
import com.rapidminer.parameter.Parameters;
import com.rapidminer.parameter.UndefinedParameterError;
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
public class RapidMinerReportNodeDialog extends NodeDialogPane {

	private Operator reporterOperator;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox formatSelection = new JComboBox/* <String> */(
			RapidMinerReportNodeModel.POSSIBLE_IMAGE_FORMATS);
	private DataTableSpec[] lastSpecs;
	private Process process;
	private Parameters parameters;

	/**
	 * New pane for configuring the RapidMinerReport node.
	 */
	protected RapidMinerReportNodeDialog() {
		JPanel panel = getPanel();
		panel.setLayout(new BorderLayout());
		panel.add(formatSelection, BorderLayout.NORTH);
		// ReporterConfigurationWizardCreator creator = new
		// ReporterConfigurationWizardCreator();
		Class<? extends ConfigurationWizardCreator> cls;
		try {
			Plugin plugin = Plugin
					.getPluginByExtensionId(
							RapidMinerReportNodeModel.REPORTING_EXTENSION_ID);
			cls = (Class<? extends ConfigurationWizardCreator>) plugin
					.getClassLoader()
					.loadClass("com.rapidminer.gui.wizards.ReporterConfigurationWizardCreator");
		} catch (ClassNotFoundException e1) {
			throw new IllegalStateException("Not found: " + e1.getMessage(), e1);
		}
		process = new Process();
		parameters = new Parameters();
		ConfigurationWizardValueCellEditor valueCellEditor = new ConfigurationWizardValueCellEditor(
				new ParameterTypeConfiguration(cls, Collections.<String, String>emptyMap(),
						new ConfigurationListener() {
							@Override
							public Parameters getParameters() {
								return parameters;
							}

							@Override
							public void setParameters(Parameters parameters) {
								// TODO Auto-generated method stub
								System.out.println(parameters);
								for (String string : parameters) {
									System.out.println(string);
								}
							}

							@Override
							public Process getProcess() {
								// TODO Auto-generated method stub
								return null;
							}
						}, new Object[] {process.getRootOperator().getInputPorts().createPort("KNIME")}));
		try {
			reporterOperator = OperatorService
					.createOperator(RapidMinerReportNodeModel.REPORTER_OPERATOR_ID);
			valueCellEditor.setOperator(reporterOperator);
			process.getRootOperator().getSubprocess(0).addOperator(reporterOperator);
			process.getRootOperator().getSubprocess(0).getInnerSources()
			.getPortByIndex(0)
			.connectTo(reporterOperator.getInputPorts().getPortByIndex(0));
			if (lastSpecs != null) {
				process.getRootOperator()
				.deliverInputMD(Collections.singletonList(DialogComponentRapidMinerProject.createMetaData(lastSpecs[0], false, null)));
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
		ArrayList<String> keys = new ArrayList<String>(/*reporterOperator
				.getParameters()*/parameters.getKeys());
		settings.addStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERKEYS,
				keys.toArray(new String[keys.size()]));
		String[] values = new String[keys.size()];
		for (int i = 0; i < keys.size(); ++i) {
			try {
				values[i] = /*reporterOperator*/parameters.getParameter(keys.get(i));
			} catch (UndefinedParameterError e) {
				throw new InvalidSettingsException("Error with parameter: "
						+ keys.get(i) + "\n" + e.getMessage(), e);
			}
		}
		settings.addStringArray(
				RapidMinerReportNodeModel.CFGKEY_REPORTERVALUES, values);
		settings.addString(RapidMinerReportNodeModel.CFGKEY_IMAGE_FORMAT,
				(String) formatSelection.getSelectedItem());
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
		formatSelection.setSelectedItem(settings.getString(
				RapidMinerReportNodeModel.CFGKEY_IMAGE_FORMAT,
				RapidMinerReportNodeModel.DEFAULT_IMAGE_FORMAT));
		String[] keys;
		try {
			keys = settings
					.getStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERKEYS);
			String[] values = settings
					.getStringArray(RapidMinerReportNodeModel.CFGKEY_REPORTERVALUES);
			assert keys.length == values.length : "keys: "
					+ Arrays.asList(keys) + "\nvalues: "
					+ Arrays.asList(values);
			for (int i = 0; i < keys.length; ++i) {
				parameters.setParameter(keys[i], values[i]);
				reporterOperator.setParameter(keys[i], values[i]);
			}
//			Process process = new Process();
//			reporterOperator.remove();
//			process.getRootOperator().getSubprocess(0)
//					.addOperator(reporterOperator, 0);
////			OutputPort knimePort = process.getRootOperator()
////					.getOutputPorts()
////					.createPort("KNIME");
//			//knimePort.disconnect();
//			OutputPort knimePort = process.getRootOperator().getSubprocess(0).getInnerSources()
//					.getPortByIndex(0);
//			knimePort
//					.connectTo(
//							reporterOperator.getInputPorts().createPort("data"));
			process.checkProcess(new IOContainer(new MemoryExampleTable(
					KnimeExampleTable.createAttributes(specs[0], true,
							"KNIMERowID")).createExampleSet()));
			process.getRootOperator().deliverInputMD(Collections.singletonList(DialogComponentRapidMinerProject.createMetaData(specs[0], false, null)));
		} catch (InvalidSettingsException e) {
			throw new NotConfigurableException(e.getMessage(), e);
		}
	}
}
