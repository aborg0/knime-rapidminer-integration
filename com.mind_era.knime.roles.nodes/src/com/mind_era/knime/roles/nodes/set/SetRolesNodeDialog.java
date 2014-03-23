/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

import org.knime.core.data.def.StringCell;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.Pair;

import com.mind_era.knime.roles.RoleRegistry;
import com.mind_era.knime.util.DialogComponentPairs;
import com.mind_era.knime.util.DialogComponentPairs.Columns;
import com.mind_era.knime.util.SettingsModelPairs;

/**
 * <code>NodeDialog</code> for the "SetRoles" Node. Allows you to assign roles
 * to specific columns.
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Gabor Bakos
 */
public class SetRolesNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring the SetRoles node.
	 */
	protected SetRolesNodeDialog() {
		addDialogComponent(new DialogComponentPairs<StringCell, StringCell>(
				new SettingsModelPairs<>(
						SetRolesNodeModel.CFGKEY_ROLE/*
													 * , SetRolesNodeModel.
													 * DEFAULT_ROLE
													 */, StringCell.TYPE,
						StringCell.TYPE,
						Collections.<Pair<StringCell, StringCell>> emptyList(),
						false, false), "column", "role"
				/*
				 * ,"role" , new RoleRegistry (). roleRepresentations (
				 * ).toArray(new String[0])
				 */, EnumSet.of(Columns.Add, Columns.Remove, Columns.Enable)) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.mind_era.knime.util.DialogComponentPairs#isEnumerable(org
			 * .knime.core.node.port.PortObjectSpec[], boolean)
			 */
			@Override
			protected boolean isEnumerable(final PortObjectSpec[] specs,
					final boolean leftColumn) {
				return leftColumn ? specs != null && specs.length > 0
						&& specs[0] != null : true;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.mind_era.knime.util.DialogComponentPairs#rightPossibleValues
			 * (org.knime.core.node.port.PortObjectSpec[])
			 */
			@Override
			protected Collection<StringCell> rightPossibleValues(
					final PortObjectSpec[] specs) {
				final RoleRegistry registry = new RoleRegistry();
				final Collection<? extends String> roleRepresentations = registry
						.roleRepresentations();
				final Collection<StringCell> ret = new ArrayList<>(
						roleRepresentations.size());
				for (final String r : roleRepresentations) {
					ret.add(new StringCell(r));
				}
				return ret;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.mind_era.knime.util.DialogComponentPairs#leftPossibleValues
			 * (org.knime.core.node.port.PortObjectSpec[])
			 */
			@Override
			protected Collection<StringCell> leftPossibleValues(
					final PortObjectSpec[] specs) {
				return columnsFromSpec(specs, 0);
			}
		});
	}
}
