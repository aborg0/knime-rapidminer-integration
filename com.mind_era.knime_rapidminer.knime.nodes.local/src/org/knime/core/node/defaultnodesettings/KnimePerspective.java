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
package org.knime.core.node.defaultnodesettings;

import com.rapidminer.gui.Perspective;
import com.rapidminer.gui.Perspectives;
import com.rapidminer.gui.processeditor.results.ResultDisplay;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.ws.WSDesktop;
import com.vlsolutions.swing.docking.ws.WSDockKey;

/**
 * The perspective available for the view.
 * 
 * @author Gabor Bakos
 */
public class KnimePerspective extends Perspectives {
	public static final String DESIGN = "design";
	public static final String RESULT = "result";

	/**
	 * Constructs the {@link KnimePerspective} with the specified perspective
	 * name.
	 * 
	 * @param context
	 *            A {@link DockingContext}.
	 */
	public KnimePerspective(final DockingContext context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.Perspectives#makePredefined()
	 */
	@Override
	protected void makePredefined() {
		addPerspective(DESIGN, false);
		addPerspective(RESULT, false);
		restoreDefault(DESIGN);
		// restoreDefault(RESULT);
		final Perspective resultPerspective = getPerspective(RESULT);
		final WSDesktop resultsDesktop = resultPerspective.getWorkspace()
				.getDesktop(0);
		resultsDesktop.clear();
		final WSDockKey resultsKey = new WSDockKey(
				ResultDisplay.RESULT_DOCK_KEY);
		resultsDesktop.addDockable(resultsKey);
	}
}