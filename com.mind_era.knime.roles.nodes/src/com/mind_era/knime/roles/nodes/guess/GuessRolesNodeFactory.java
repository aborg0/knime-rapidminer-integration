/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.guess;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "GuessRoles" Node. Tries to guess the
 * columns roles based on certain properties.
 * 
 * @author Gabor Bakos
 */
public class GuessRolesNodeFactory extends NodeFactory<GuessRolesNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GuessRolesNodeModel createNodeModel() {
		return new GuessRolesNodeModel();
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
	public NodeView<GuessRolesNodeModel> createNodeView(final int viewIndex,
			final GuessRolesNodeModel nodeModel) {
		throw new IndexOutOfBoundsException("No such view: " + viewIndex);
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
		return new GuessRolesNodeDialog();
	}

}
