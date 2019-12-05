/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.guess;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeDialog</code> for the "GuessRoles" Node. Tries to guess the columns
 * roles based on certain properties.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Gabor Bakos
 */
public class GuessRolesNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the GuessRoles node.
	 */
	protected GuessRolesNodeDialog() {

	}
}
