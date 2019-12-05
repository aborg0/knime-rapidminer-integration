/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles.nodes.dummy;

import java.util.Collection;
import java.util.Collections;

import javax.swing.Icon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObjectSpec;

import com.mind_era.knime.roles.Role;

/**
 * @author Gabor
 * 
 */
public class DummyRole implements Role {

	/**
	 * 
	 */
	public DummyRole() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#representation()
	 */
	@Override
	public String representation() {
		return "dummy";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#icon()
	 */
	@Override
	public Icon icon() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#description()
	 */
	@Override
	public String description() {
		return "A role to delete";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#defaultName()
	 */
	@Override
	public String defaultName() {
		return "dummy";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#alternativeNames()
	 */
	@Override
	public Collection<String> alternativeNames() {
		return Collections.singleton(defaultName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#preferredDataType()
	 */
	@Override
	public DataType preferredDataType() {
		return StringCell.TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#disallowedDataTypes()
	 */
	@Override
	public Collection<DataType> disallowedDataTypes() {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#allowedDataTypes()
	 */
	@Override
	public Collection<DataType> allowedDataTypes() {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#isUnique()
	 */
	@Override
	public boolean isUnique() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#isExclusive()
	 */
	@Override
	public boolean isExclusive() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind_era.knime.roles.Role#shouldBeNominal(org.knime.core.data.DataType
	 * )
	 */
	@Override
	public boolean shouldBeNominal(final DataType type) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind_era.knime.roles.Role#isNominalPreferred(org.knime.core.data.
	 * DataType)
	 */
	@Override
	public boolean isNominalPreferred(final DataType type) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#isUniquePreferred()
	 */
	@Override
	public boolean isUniquePreferred() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#isExclusivePreferred()
	 */
	@Override
	public boolean isExclusivePreferred() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind_era.knime.roles.Role#configurationChecks(org.knime.core.node
	 * .NodeModel, org.knime.core.node.port.PortObjectSpec[],
	 * org.knime.core.data.DataColumnSpec[])
	 */
	@Override
	public void configurationChecks(final NodeModel model,
			final PortObjectSpec[] tableSpecs,
			final DataColumnSpec... columnSpecs) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return representation();
	}
}
