/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2013 Mind Eratosthenes Kft.
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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.knime.core.node.NodeView;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.gui.viewer.DataTableViewer;

/**
 * Node view (logs) for the RapidMiner node.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerNodeView extends NodeView<RapidMinerNodeModel> {

	private final CloseableTabbedResultDisplay resultDisplay;

	/**
	 * @param nodeModel
	 *            The associated {@link RapidMinerNodeModel}.
	 */
	public RapidMinerNodeView(final RapidMinerNodeModel nodeModel) {
		super(nodeModel);
		resultDisplay = new CloseableTabbedResultDisplay() {
			private static final long serialVersionUID = -5411030705713523611L;

			@Override
			protected void onCloseDataTable(final DataTableViewer viewer) {
				getNodeModel().removeLogDataTable(viewer.getDataTable());
				clear();
				for (final DataTable logTable : getNodeModel()
						.getLogDataTables()) {
					addDataTable(logTable);
				}
			}
		};
		nodeModel.addLogListener(this.resultDisplay);
		setComponent(resultDisplay);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeView#onClose()
	 */
	@Override
	protected void onClose() {
		getNodeModel().removeLogListener(this.resultDisplay);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.NodeView#onOpen()
	 */
	@Override
	protected void onOpen() {
		resultDisplay.setPreferredSize(new Dimension(500, 500));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.AbstractNodeView#modelChanged()
	 */
	@Override
	protected void modelChanged() {
		resultDisplay.clear();
		for (final DataTable table : getNodeModel().getLogDataTables()) {
			resultDisplay.addDataTable(table);
		}
		if (resultDisplay.getCurrentlyDisplayedComponent() instanceof DataTableViewer) {
			final DataTableViewer viewer = (DataTableViewer) resultDisplay
					.getCurrentlyDisplayedComponent();
			final Component c0 = viewer.getComponents()[0];
			if (c0 instanceof JPanel) {
				final JPanel jp = (JPanel) c0;
				final LayoutManager layout = jp.getLayout();
				if (layout instanceof CardLayout) {
					final CardLayout card = (CardLayout) layout;
					card.show(jp, DataTableViewer.TABLE_MODE);
				}
			}
		}
	}
}
