package com.mind_era.knime_rapidminer.knime.nodes;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "RapidMiner" Node. Executes a RapidMiner
 * workflow.
 * 
 * @author Gabor Bakos
 */
public class RapidMinerNodeFactory extends NodeFactory<RapidMinerNodeModel> {

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
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<RapidMinerNodeModel> createNodeView(final int viewIndex,
			final RapidMinerNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("No views yet: " + viewIndex);
		// return new RapidMinerNodeView(nodeModel);
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
