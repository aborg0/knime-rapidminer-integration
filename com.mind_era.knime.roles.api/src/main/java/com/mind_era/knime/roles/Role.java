/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles;

import java.util.Collection;

import javax.swing.Icon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObjectSpec;

/**
 * The interface representing the role of a column in KNIME. Each methods return
 * non-{@code null} values, except other stated.
 * 
 * @author Gabor Bakos
 */
public interface Role {

	/**
	 * The property key for the roles.
	 */
	public static final String PROPERTY_KEY = "role";

	/**
	 * @return The {@link String} representation of the role.
	 */
	public String representation();

	/**
	 * @return The graphical representation of the role. (Can be {@code null}.)
	 */
	public Icon icon();

	/**
	 * @return A HTML formatted description of the role.
	 */
	public String description();

	/**
	 * @return The default name when a new column should be created, or a column
	 *         could be selected.
	 */
	public String defaultName();

	/**
	 * @return The alternative column names that can be used for this role.
	 *         (Should include {@link #defaultName()}.)
	 */
	public Collection<String> alternativeNames();

	/**
	 * @return The preferred data type when a new column should be created.
	 */
	public DataType preferredDataType();

	/**
	 * Columns with {@link DataType}s from this collection cannot have this
	 * role.
	 * 
	 * @return A possibly empty {@link Collection} of {@link DataType}s.
	 */
	public Collection<DataType> disallowedDataTypes();

	/**
	 * One of these {@link DataType}s should be
	 * {@link DataType#isASuperTypeOf(DataType)} to the {@link DataType} of the
	 * columns with this role. The empty collection means that there are no
	 * constraints.
	 * 
	 * @return A possibly empty {@link Collection} of {@link DataType}s that can
	 *         be the super-type of the {@link DataType} of the role's columns.
	 */
	public Collection<DataType> allowedDataTypes();

	/**
	 * @return Is this role has to be unique among the {@link DataTableSpec}
	 *         columns, or can be multiple columns with the same role?
	 * @see #isUniquePreferred()
	 */
	public boolean isUnique();

	/**
	 * @return Other roles can also be present for the same column (return value
	 *         {@code false}), or not (return value {@code true})?
	 * @see #isExclusivePreferred()
	 */
	public boolean isExclusive();

	/**
	 * @param type
	 *            The {@link DataType} of the column.
	 * @return Is it a requirement to have calculated domain for this role?
	 * @see #isNominalPreferred(DataType)
	 */
	public boolean shouldBeNominal(DataType type);

	/**
	 * @param type
	 *            The {@link DataType} of the column.
	 * @return Should a warning be signaled if the column of this role is not
	 *         nominal?
	 * @see #shouldBeNominal(DataType)
	 */
	public boolean isNominalPreferred(DataType type);

	/**
	 * @return Should a warning be signaled if the column of this role is not
	 *         unique?
	 * @see #isUnique()
	 */
	public boolean isUniquePreferred();

	/**
	 * @return Should a warning be signaled if the there are multiple roles
	 *         associated to a column with this role?
	 * @see #isExclusive()
	 */
	public boolean isExclusivePreferred();

	/**
	 * Checks further constraints for the {@link Role} during the
	 * {@link NodeModel#configure(PortObjectSpec[])}.
	 * 
	 * @param model
	 *            The node model. It can be {@code null}, if not, you can use it
	 *            to set warnings or call other methods and the configuration or
	 *            the execution method is on the call stack.
	 * @param tableSpecs
	 *            The input port specifications.
	 * @param columnSpecs
	 *            The columns' specifications.
	 * @throws InvalidSettingsException
	 *             If the configuration is not compatible with the role.
	 */
	@SuppressWarnings("javadoc")
	public void configurationChecks(NodeModel model,
			PortObjectSpec[] tableSpecs, DataColumnSpec... columnSpecs)
			throws InvalidSettingsException;
}