package com.mind_era.knime_rapidminer.knime.nodes.view;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * {@link NodeFactory} for the "RapidMinerView" Node. Visualize the node with
 * RapidMiner, allowing to plot the results in 3D, export the images.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerViewNodeFactory extends
		NodeFactory<RapidMinerViewNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RapidMinerViewNodeModel createNodeModel() {
		return new RapidMinerViewNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<RapidMinerViewNodeModel> createNodeView(
			final int viewIndex, final RapidMinerViewNodeModel nodeModel) {
		return new RapidMinerViewNodeView(nodeModel);
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
		return new RapidMinerViewNodeDialog();
	}
}
