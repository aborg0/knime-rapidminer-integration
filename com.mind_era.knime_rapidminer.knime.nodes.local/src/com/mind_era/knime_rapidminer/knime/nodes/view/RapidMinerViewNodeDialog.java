package com.mind_era.knime_rapidminer.knime.nodes.view;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "RapidMinerView" Node. Visualize the node
 * with RapidMiner, allowing to plot the results in 3D, export the images.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Gabor
 */
public class RapidMinerViewNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the RapidMinerView node.
	 */
	protected RapidMinerViewNodeDialog() {
		// No settings.
	}
}
