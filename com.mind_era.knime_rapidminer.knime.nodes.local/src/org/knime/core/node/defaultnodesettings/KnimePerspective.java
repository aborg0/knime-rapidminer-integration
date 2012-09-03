/**
 * 
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
	public static final String RESULT = "knime-result";

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