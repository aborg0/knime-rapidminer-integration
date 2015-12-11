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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.rapidminer.gui.processeditor.results.DockableResultDisplay;
import com.rapidminer.gui.viewer.DataTableViewer;

/**
 * An extended version of {@link TabbedResultDisplay} which allows to close
 * certain tabs. <br>
 * Unfortunately the {@link DataTableViewer} and {@link TabbedResultDisplay} do
 * not offer an easy way to set the default tab to show. :(
 * 
 * @author Gabor Bakos
 */
public class CloseableTabbedResultDisplay extends DockableResultDisplay {
	private static final long serialVersionUID = -7873107853694395187L;

	/**
	 * Constructs the {@link CloseableTabbedResultDisplay}.
	 */
	public CloseableTabbedResultDisplay() {
		super();
		for (final Component comp : getComponents()) {
			if (comp instanceof JLabel) {
				final JLabel label = (JLabel) comp;
				remove(label);
				final JPanel panel = new JPanel(new BorderLayout());
				add(panel, BorderLayout.NORTH);
				panel.add(label, BorderLayout.WEST);
				final JButton closeButton = new JButton(
						new AbstractAction("X") {
							private static final long serialVersionUID = 8134528038216810552L;

							@Override
							public void actionPerformed(final ActionEvent e) {
								final Component currentlyDisplayedComponent = getComponent();
								if (currentlyDisplayedComponent instanceof DataTableViewer) {
									final DataTableViewer viewer = (DataTableViewer) currentlyDisplayedComponent;
									onCloseDataTable(viewer);
								}
							}
						});
				panel.add(closeButton, BorderLayout.EAST);
				break;
			}
		}
	}

	/**
	 * Called after removing the table to signal other parts if necessary. <br/>
	 * Intended to be overridden.
	 * 
	 * @param viewer
	 *            The {@link DataTableViewer} to remove.
	 */
	protected void onCloseDataTable(final DataTableViewer viewer) {
		// Do nothing if it is not required.
	}
}
