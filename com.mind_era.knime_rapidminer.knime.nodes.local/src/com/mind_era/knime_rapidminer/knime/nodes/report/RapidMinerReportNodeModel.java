package com.mind_era.knime_rapidminer.knime.nodes.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.data.xml.SvgImageContent;
import org.knime.core.data.container.WrappedTable;
import org.knime.core.data.image.ImageContent;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.util.FileUtil;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.plugin.Plugin;

/**
 * This is the model implementation of RapidMinerReport. Converts data to
 * figures using RapidMiner's Reporting extension.
 * 
 * @author Mind Eratosthenes Kft.
 */
public class RapidMinerReportNodeModel extends NodeModel {
	static {
		RapidMinerInit.init(false);
	}
	static final String REPORTING_EXTENSION_ID = "rmx_reporting";
	private static final boolean IS_SUPPORTED = Plugin
			.getPluginByExtensionId(REPORTING_EXTENSION_ID) != null;
	protected static final String CFGKEY_IMAGE_FORMAT = "image.format";
	private static final String PNG = "PNG";
	private static final String SVG = "SVG";
	protected static final String[] POSSIBLE_IMAGE_FORMATS = new String[] {
			PNG, SVG };
	protected static final String DEFAULT_IMAGE_FORMAT = POSSIBLE_IMAGE_FORMATS[0];

	protected static SettingsModelString createImageFormat() {
		return new SettingsModelString(CFGKEY_IMAGE_FORMAT,
				DEFAULT_IMAGE_FORMAT);
	}

	private final SettingsModelString imageFormat = createImageFormat();
	private final SettingsModelStringArray keys = new SettingsModelStringArray(
			CFGKEY_REPORTERKEYS, new String[0]);
	private final SettingsModelStringArray values = new SettingsModelStringArray(
			CFGKEY_REPORTERVALUES, new String[0]);
	protected final static String CFGKEY_REPORTERKEYS = "reporterKeys";
	protected final static String CFGKEY_REPORTERVALUES = "reporterValues";
	static final String REPORTER_OPERATOR_ID = "reporting:report";

	/**
	 * Constructor for the node model.
	 */
	protected RapidMinerReportNodeModel() {
		super(new PortType[] { BufferedDataTable.TYPE },
				new PortType[] { ImagePortObject.TYPE });
		RapidMinerInit.init(false);
		RapidMinerInit.setPreferences();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inData,
			final ExecutionContext exec) throws Exception {
		File tempDir = FileUtil.createTempDir("rm_report_output");
		try {
			Process process = new Process();
			process.getContext().setInputRepositoryLocations(
					Collections.singletonList("//" + KnimeRepository.KNIME
							+ "/"
							+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE
							+ 1));
			Operator generateReport = OperatorService
					.createOperator("reporting:generate_report");
			generateReport.setParameter("format", "HTML");
			generateReport.setParameter("html_output_directory", tempDir
					.getAbsolutePath().toString());
			generateReport.setParameter("report_name", "KNIME");
			process.getRootOperator().getSubprocess(0).addOperator(generateReport, 0);
			Operator report = OperatorService
					.createOperator(REPORTER_OPERATOR_ID);
			assert keys.getStringArrayValue().length == values.getStringArrayValue().length;
			report.setParameter("report_name", "KNIME");
			report.setParameter("specified", "true");
			report.setParameter("renderer_name", "Plot View");
			report.setParameter("reportable_type", "Data Table");
			List<String[]> parameterList = new ArrayList<String[]>();
			for (int i = 0; i < keys.getStringArrayValue().length; ++i) {
				parameterList.add(new String[] {keys.getStringArrayValue()[i], values.getStringArrayValue()[i]});
			}
			report.setParameter("parameters", ParameterTypeList.transformList2String(parameterList));
			process.getRootOperator().getSubprocess(0).addOperator(report, 1);
			generateReport.getOutputPorts().getPortByIndex(0)
			.connectTo(report.getInputPorts().getPortByIndex(0));
			process.getRootOperator().getSubprocess(0).getInnerSources()
					.getPortByIndex(0)
					//.createPort("input 1", true)
					.connectTo(generateReport.getInputPorts().getPortByIndex(0));
			// process.getRootOperator().getInputPorts()
			// .createPort("output 1", true));
			process.run(new IOContainer(MemoryExampleTable
					.createCompleteCopy(
							new KnimeExampleTable(new WrappedTable((BufferedDataTable) inData[0]), false, null))
					.createExampleSet()));
		} catch (final/* IO */Exception e) {
			throw new IllegalStateException("Should not happen: " + e.getMessage(), e);
		}

		ImageContent content = readFile(tempDir, "image1");
		return new ImagePortObject[] { new ImagePortObject(content,
				createSpec()) };
	}

	/**
	 * @param tempDir
	 * @param fileNameWithoutExtension
	 * @return
	 */
	private ImageContent readFile(File tempDir, String fileNameWithoutExtension) {
		try {
			if (imageFormat.getStringValue().equals(SVG)) {
				return readSvg(new File(tempDir, fileNameWithoutExtension
						+ ".svg"));
			}
			if (imageFormat.getStringValue().equals(PNG)) {
				return readPng(new File(tempDir, fileNameWithoutExtension
						+ ".png"));
			}
			throw new UnsupportedOperationException(
					"Not supported image format: "
							+ imageFormat.getStringValue());
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private ImageContent readPng(File file) throws IOException {
		final FileInputStream in = new FileInputStream(file);
		try {
			return new PNGImageContent(in);
		} finally {
			in.close();
		}
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private ImageContent readSvg(File file) throws IOException {
		final FileInputStream in = new FileInputStream(file);
		try {
			return new SvgImageContent(in);
		} finally {
			in.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO: generated method stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {
		if (!IS_SUPPORTED) {
			throw new InvalidSettingsException(
					"The reporting extension is not installed to RapidMiner.");
		}

		return new ImagePortObjectSpec[] { createSpec() };
	}

	/**
	 * @return
	 */
	private ImagePortObjectSpec createSpec() {
		if (imageFormat.getStringValue().equals(PNG)) {
			return new ImagePortObjectSpec(PNGImageContent.TYPE);
		}
		if (imageFormat.getStringValue().equals(SVG)) {
			return new ImagePortObjectSpec(SvgCell.TYPE);
		}
		throw new UnsupportedOperationException("Not supported image format: "
				+ imageFormat.getStringValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		imageFormat.saveSettingsTo(settings);
		keys.saveSettingsTo(settings);
		values.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		imageFormat.loadSettingsFrom(settings);
		keys.loadSettingsFrom(settings);
		values.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		createImageFormat().validateSettings(settings);
		keys.validateSettings(settings);
		values.validateSettings(settings);
		if (keys.getStringArrayValue().length != values.getStringArrayValue().length) {
			throw new InvalidSettingsException("The keys and values do not form pairs:\nKeys:\n"+
					Arrays.asList(keys.getStringArrayValue())  + "\nValues" + Arrays.asList(values.getStringArrayValue()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// TODO: generated method stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		// TODO: generated method stub
	}

}
