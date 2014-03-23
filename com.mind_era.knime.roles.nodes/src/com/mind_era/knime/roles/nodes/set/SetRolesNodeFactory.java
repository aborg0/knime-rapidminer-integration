/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.set;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SetRoles" Node. Allows you to assign roles
 * to specific columns.
 * 
 * @author Gabor Bakos
 */
public class SetRolesNodeFactory extends NodeFactory<SetRolesNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SetRolesNodeModel createNodeModel() {
		return new SetRolesNodeModel();
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
	public NodeView<SetRolesNodeModel> createNodeView(final int viewIndex,
			final SetRolesNodeModel nodeModel) {
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
		return new SetRolesNodeDialog();
	}

}
