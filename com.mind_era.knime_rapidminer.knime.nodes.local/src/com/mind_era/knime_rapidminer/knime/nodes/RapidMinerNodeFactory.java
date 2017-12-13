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

import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.RapidMinerGUI;

/**
 * <code>NodeFactory</code> for the "RapidMiner" Node. Executes a RapidMiner
 * workflow.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerNodeFactory extends NodeFactory<RapidMinerNodeModel> {
	/**
	 * 
	 */
	public RapidMinerNodeFactory() {
//		try {
//			// Wait a bit to make sure the bundle is properly
//			// initialized
//			Thread.sleep(700);
//		} catch (final InterruptedException e) {
//			// No problems
//		}
////		Display.getCurrent().asyncExec(() -> {
//		System.out.println("Before init");
//		RapidMinerInit.init(false);
//		System.out.println("After init");
//		final AbstractUIState state = new AbstractUIState(/* "design", */
//				null, new JPanel()) {
//
//			@Override
//			public void exit(final boolean relaunch) {
//				// Do nothing, we do not exit
//			}
//
//			@Override
//			public boolean close() {
//				metaDataUpdateQueue.shutdown();
//				return true;
//			}
//
//			@Override
//			public void updateRecentFileList() {
//				// Do noting
//			}
//
//			@Override
//			public void setTitle() {
//				// Do nothing
//			}
//		};
//		System.out.println("Setting main frame");
//		SwingUtilities.invokeLater(() ->
//		RapidMinerGUI.setMainFrame(state));
//		try {
//			SwingUtilities.invokeAndWait(() -> state.getValidateAutomaticallyAction().setSelected(true));
//		} catch (InvocationTargetException | InterruptedException | RuntimeException e) {
//			e.printStackTrace();
//			// Not too interesting in case we cannot set the
//			// automatic validation to true.
//		}
//		state.close();
//		System.out.println("Setting preferences");
//		RapidMinerInit.setPreferences(false);
////		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RapidMinerNodeModel createNodeModel() {
		return new RapidMinerNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		//Disabling log views for now.
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<RapidMinerNodeModel> createNodeView(final int viewIndex,
			final RapidMinerNodeModel nodeModel) {
		return new RapidMinerNodeView(nodeModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new RapidMinerNodeDialog();
	}

}
