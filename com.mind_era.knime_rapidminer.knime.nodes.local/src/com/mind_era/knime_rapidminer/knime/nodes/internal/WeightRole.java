/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2014 Mind Eratosthenes Kft.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mind_era.knime_rapidminer.knime.nodes.internal;

/**
 * Role for weight columns.
 * 
 * @author Gabor Bakos
 */
//public final class WeightRole implements Role {
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#representation()
//	 */
//	@Override
//	public String representation() {
//		return Attributes.WEIGHT_NAME;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#icon()
//	 */
//	@Override
//	public Icon icon() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#description()
//	 */
//	@Override
//	public String description() {
//		return "Weight of the example/row.";
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#defaultName()
//	 */
//	@Override
//	public String defaultName() {
//		return "weight";
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#alternativeNames()
//	 */
//	@Override
//	public Collection<String> alternativeNames() {
//		return Arrays.asList(defaultName());
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#preferredDataType()
//	 */
//	@Override
//	public DataType preferredDataType() {
//		return DoubleCell.TYPE;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#disallowedDataTypes()
//	 */
//	@Override
//	public Collection<DataType> disallowedDataTypes() {
//		return Collections.emptyList();
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#allowedDataTypes()
//	 */
//	@Override
//	public Collection<DataType> allowedDataTypes() {
//		return Arrays.asList(DoubleCell.TYPE, FuzzyIntervalCell.TYPE, FuzzyNumberCell.TYPE);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#isUnique()
//	 */
//	@Override
//	public boolean isUnique() {
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#isExclusive()
//	 */
//	@Override
//	public boolean isExclusive() {
//		return true;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#shouldBeNominal(org.knime.core.data.DataType)
//	 */
//	@Override
//	public boolean shouldBeNominal(DataType type) {
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#isNominalPreferred(org.knime.core.data.DataType)
//	 */
//	@Override
//	public boolean isNominalPreferred(DataType type) {
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#isUniquePreferred()
//	 */
//	@Override
//	public boolean isUniquePreferred() {
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#isExclusivePreferred()
//	 */
//	@Override
//	public boolean isExclusivePreferred() {
//		return true;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.mind_era.knime.roles.Role#configurationChecks(org.knime.core.node.NodeModel, org.knime.core.node.port.PortObjectSpec[], org.knime.core.data.DataColumnSpec[])
//	 */
//	@Override
//	public void configurationChecks(NodeModel model,
//			PortObjectSpec[] tableSpecs, DataColumnSpec... columnSpecs)
//			throws InvalidSettingsException {
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		return representation();
//	}
//}
