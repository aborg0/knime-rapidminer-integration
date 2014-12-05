package com.mind_era.knime_rapidminer.knime.nodes.report;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "RapidMinerReport" Node.
 * Converts data to figures using RapidMiner's Reporting extension.
 *
 * @author Mind Eratosthenes Kft.
 */
public class RapidMinerReportNodeFactory 
        extends NodeFactory<RapidMinerReportNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RapidMinerReportNodeModel createNodeModel() {
        return new RapidMinerReportNodeModel();
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
    public NodeView<RapidMinerReportNodeModel> createNodeView(final int viewIndex,
            final RapidMinerReportNodeModel nodeModel) {
    	throw new IndexOutOfBoundsException("Unknown view: " + viewIndex);
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
        return new RapidMinerReportNodeDialog();
    }

}

