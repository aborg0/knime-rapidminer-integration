/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.guess;

import java.io.File;
import java.util.Collection;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.mind_era.knime.roles.Role;
import com.mind_era.knime.roles.RoleHandler;
import com.mind_era.knime.roles.RoleRegistry;

/**
 * This is the model implementation of GuessRoles. Tries to guess the columns
 * roles based on certain properties.
 * 
 * @author Gabor Bakos
 */
public class GuessRolesNodeModel extends NodeModel {

	/**
	 * Constructor for the node model.
	 */
	protected GuessRolesNodeModel() {
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) {

		return new BufferedDataTable[] { exec.createSpecReplacerTable(
				inData[0], guessSpec(inData[0].getDataTableSpec())) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) {
		return new DataTableSpec[] { guessSpec(inSpecs[0]) };
	}

	/**
	 * @param dataTableSpec Input {@link DataTableSpec}.
	 * @return The guessed {@link DataTableSpec}.
	 */
	private DataTableSpec guessSpec(final DataTableSpec dataTableSpec) {
		final RoleRegistry registry = new RoleRegistry();
		final RoleHandler handler = new RoleHandler(registry);
		// TODO improve, check for constraint violations, warnings, ...
		final DataColumnSpec[] newSpecs = new DataColumnSpec[dataTableSpec
				.getNumColumns()];
		int i = 0;
		for (final DataColumnSpec dataColumnSpec : dataTableSpec) {
			final Collection<Role> rolesForName = registry
					.possibleRolesForName(dataColumnSpec.getName());
			newSpecs[i++] = handler.addRoles(dataColumnSpec,
					rolesForName.toArray(new Role[0]));

		}
		return new DataTableSpec(newSpecs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		// no settings
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
		// no settings
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) {
		// no settings
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) {
		// nothing to do
	}

}
