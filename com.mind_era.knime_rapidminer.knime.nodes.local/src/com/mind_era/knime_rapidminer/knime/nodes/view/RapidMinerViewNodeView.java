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
package com.mind_era.knime_rapidminer.knime.nodes.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.KnimePerspective;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.PerspectiveModel;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
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
	private static final NodeLogger logger = NodeLogger
			.getLogger(RapidMinerViewNodeView.class);

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
		state = new AbstractUIState(/*KnimePerspective.RESULT,*/ null, panel) {

			@Override
			public boolean close() {
				metaDataUpdateQueue.shutdown();
				return true;
			}

			@Override
			public void exit(final boolean relaunch) {
			}

			@Override
			public void updateRecentFileList() {
			}

			@Override
			public void setTitle() {
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
		state.getPerspectiveController().showPerspective(state.getPerspectiveController().getModel().getPerspective(PerspectiveModel.RESULT));
		setComponent(pane);
		try {
			process = new Process();
			process.getContext().setInputRepositoryLocations(
					Collections.singletonList("//" + KnimeRepository.KNIME
							+ "/"
							+ KnimeRepository.KnimeIOObjectEntry.KNIME_TABLE
							+ 1));
			process.getRootOperator()
					.getOutputPorts()
					.createPort("input 1", true)
					.connectTo(
							process.getRootOperator().getInputPorts()
									.createPort("output 1", true));
		} catch (final/* IO */Exception e) {
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
		if (table != null) {
			final ExampleSet exampleSet = MemoryExampleTable
					.createCompleteCopy(
							new KnimeExampleTable(table, false, null))
					.createExampleSet();
			try {
				process.run(new IOContainer(exampleSet));
			} catch (final OperatorException e) {
				logger.error(e.getMessage());
				logger.debug(e.getMessage(), e);
			}
			state.processEnded(process, new IOContainer(exampleSet));
		}
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
