package com.mind_era.knime_rapidminer.knime.nodes.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.KnimePerspective;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.datatable.DataTableExampleSetAdapter;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.tools.XMLException;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.toolbars.ToolBarContainer;

/**
 * {@link NodeView} for the "RapidMinerView" Node. Visualize the node with
 * RapidMiner, allowing to plot the results in 3D, export the images.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerViewNodeView extends NodeView<RapidMinerViewNodeModel> {

	private KnowsRowCountTable table;
	private AbstractUIState state;
	private Process process;

	/**
	 * Creates a new view.
	 * 
	 * @param nodeModel
	 *            The model (class: {@link RapidMinerViewNodeModel})
	 */
	protected RapidMinerViewNodeView(final RapidMinerViewNodeModel nodeModel) {
		super(nodeModel);
		RapidMinerInit.init(false);
		RapidMinerInit.setPreferences();
		table = nodeModel.getTable();
		final JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1100, 800));
		panel.setMaximumSize(new Dimension(1100, 800));
		state = new AbstractUIState(KnimePerspective.RESULT, null, panel) {

			@Override
			public boolean close() {
				metaDataUpdateQueue.shutdown();
				return true;
			}

			@Override
			public void exit(final boolean relaunch) {
				// TODO Auto-generated method stub

			}

			@Override
			public void updateRecentFileList() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void setTitle() {
				// TODO Auto-generated method stub

			}
		};
		RapidMinerGUI.setMainFrame(state);
		state.getValidateAutomaticallyAction().setSelected(true);
		final DockingContext dockingContext = state.getDockingDesktop()
				.getContext();
		final KnimePerspective perspective = new KnimePerspective(
				dockingContext);
		final DockingDesktop dockingDesktop = state.getDockingDesktop();
		dockingDesktop.setPreferredSize(new Dimension(1000, 700));
		final ToolBarContainer toolBarContainer = ToolBarContainer
				.createDefaultContainer(true, true, true, true);
		toolBarContainer.setPreferredSize(new Dimension(1000, 750));
		final JScrollPane pane = new JScrollPane(toolBarContainer);
		pane.getViewport().setPreferredSize(new Dimension(1000, 850));
		toolBarContainer.add(dockingDesktop, BorderLayout.CENTER);
		perspective.showPerspective(KnimePerspective.RESULT);
		setComponent(pane);
		try {
			process = new Process(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><process version=\"5.2.000\"><context><input><location>"
							+ "//"
							+ KnimeRepository.KNIME
							+ "/"
							+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE
							+ 1
							+ "</location></input><output/><macros/></context><operator activated=\"true\" class=\"process\" compatibility=\"5.2.000\" expanded=\"true\" name=\"Process\"><process expanded=\"true\" height=\"-20\" width=\"-50\"><connect from_port=\"input 1\" to_port=\"result 1\"/><portSpacing port=\"source_input 1\" spacing=\"0\"/><portSpacing port=\"source_input 2\" spacing=\"0\"/><portSpacing port=\"sink_result 1\" spacing=\"0\"/><portSpacing port=\"sink_result 2\" spacing=\"0\"/></process></operator></process>");
		} catch (final IOException e) {
			throw new IllegalStateException("Should not happen", e);
		} catch (final XMLException e) {
			throw new IllegalStateException("Should not happen", e);
		}
		state.setProcess(process, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {
		final RapidMinerViewNodeModel nodeModel = getNodeModel();
		table = nodeModel == null ? null : nodeModel.getTable();
		// try {
		// process.getContext().setInputRepositoryLocations(
		// Arrays.asList("//" + KnimeRepository.KNIME + "/"
		// + KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE
		// + 1));
		// new RunAction(state).actionPerformed(null);
		// process.run(new IOContainer(MemoryExampleTable.createCompleteCopy(
		// new KnimeExampleTable(table, false, null))
		// .createExampleSet()));
		state.getResultDisplay().addDataTable(
				new DataTableExampleSetAdapter(MemoryExampleTable
						.createCompleteCopy(
								new KnimeExampleTable(table, false, null))
						.createExampleSet(), null));
		// } catch (final OperatorException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onClose() {
		// TODO: generated method stub
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onOpen() {
		// TODO: generated method stub
	}

}
