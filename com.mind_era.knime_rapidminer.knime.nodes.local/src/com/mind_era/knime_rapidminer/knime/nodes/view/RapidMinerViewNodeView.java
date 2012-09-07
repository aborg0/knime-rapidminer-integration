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
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.KnimePerspective;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeExampleTable;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.Process;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.renderer.Renderer;
import com.rapidminer.gui.renderer.RendererService;
import com.rapidminer.operator.IOContainer;
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
	private JScrollPane pane;

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
		pane = new JScrollPane(toolBarContainer);
		// final DockableResultDisplay resultDisplay = new
		// DockableResultDisplay();
		// resultDisplay.init(state);
		// pane = new JScrollPane(resultDisplay);

		// Remove the incompatible metadata renderer
		final List<Renderer> renderers = RendererService
				.getRenderers(RendererService.getName(SimpleExampleSet.class));
/*		Renderer toRemove = null;
		for (final Renderer renderer : renderers) {
			if (renderer.getClass().getSimpleName()
					.equals("ExampleSetMetaDataRenderer")) {
				toRemove = renderer;
			}
		}
		if (toRemove != null) {
			renderers.remove(toRemove);
		}*/
		// end remove

		// final JScrollPane pane = new JScrollPane(
		// ResultDisplayTools.createVisualizationComponent(
		// MemoryExampleTable.createCompleteCopy(
		// new KnimeExampleTable(table, false, null))
		// .createExampleSet(), null, ""));
		// final JScrollPane pane = new JScrollPane(ResultDisplayTools
		// .makeResultDisplay().getComponent());
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
		// try {
		// process.run();
		// } catch (final OperatorException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void modelChanged() {
		final RapidMinerViewNodeModel nodeModel = getNodeModel();
		table = nodeModel == null ? null : nodeModel.getTable();
		if (table != null) {
			state.processEnded(
					process,
					new IOContainer(MemoryExampleTable.createCompleteCopy(
							new KnimeExampleTable(table, false, null))
							.createExampleSet()));
		}
		// state.getResultDisplay().addDataTable(
		// new DataTableExampleSetAdapter(MemoryExampleTable
		// .createCompleteCopy(
		// new KnimeExampleTable(table, false, null))
		// .createExampleSet(), null));
		// pane = new
		// JScrollPane(ResultDisplayTools.createVisualizationComponent(
		// MemoryExampleTable.createCompleteCopy(
		// new KnimeExampleTable(table, false, null))
		// .createExampleSet(), null, ""));
		// setComponent(pane);
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
