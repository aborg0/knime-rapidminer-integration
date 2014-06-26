/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.Icon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortObjectSpec;

/**
 * The predefined {@link Role}s: {@value #label}, {@value #prediction},
 * {@value #cluster}.
 * 
 * @author Gabor Bakos
 */
public enum PredefinedRoles implements Role {
	/** The training data label/class role */
	label("class") {
		@Override
		public Icon icon() {
			return null;
		}

		@Override
		public String description() {
			return "For a supervised learning algorithm the training set's class label column should have this column.";
		}

		@Override
		public String defaultName() {
			return representation();
		}

		@Override
		public Collection<String> alternativeNames() {
			return Arrays.asList(defaultName(), "label");
		}

		@Override
		public DataType preferredDataType() {
			return StringCell.TYPE;
		}

		@Override
		public Collection<DataType> disallowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public Collection<DataType> allowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public boolean isUnique() {
			return false;
		}

		@Override
		public boolean isExclusive() {
			return false;
		}

		@Override
		public boolean shouldBeNominal(final DataType type) {
			return false;
		}

		@Override
		public boolean isNominalPreferred(final DataType type) {
			return StringCell.TYPE.isASuperTypeOf(type);
		}

		@Override
		public boolean isUniquePreferred() {
			return true;
		}

		@Override
		public boolean isExclusivePreferred() {
			return false;
		}

		@Override
		public void configurationChecks(final NodeModel model,
				final PortObjectSpec[] tableSpecs,
				final DataColumnSpec... columnSpecs) {
			// Do nothing
		}

	},
	/** predicted class labels */
	prediction {
		@Override
		public Icon icon() {
			return null;
		}

		@Override
		public String description() {
			return "The result of a model prediction.";
		}

		@Override
		public String defaultName() {
			return representation();
		}

		@Override
		public Collection<String> alternativeNames() {
			return Arrays.asList(defaultName(), "class", "cluster");
		}

		@Override
		public DataType preferredDataType() {
			return StringCell.TYPE;
		}

		@Override
		public Collection<DataType> disallowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public Collection<DataType> allowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public boolean isUnique() {
			return false;
		}

		@Override
		public boolean isExclusive() {
			return false;
		}

		@Override
		public boolean shouldBeNominal(final DataType type) {
			return false;
		}

		@Override
		public boolean isNominalPreferred(final DataType type) {
			return StringCell.TYPE.isASuperTypeOf(type);
		}

		@Override
		public boolean isUniquePreferred() {
			return true;
		}

		@Override
		public boolean isExclusivePreferred() {
			return false;
		}

		@Override
		public void configurationChecks(final NodeModel model,
				final PortObjectSpec[] tableSpecs,
				final DataColumnSpec... columnSpecs) {
			// Do nothing
		}

	},
	/** cluster identifiers */
	cluster {
		@Override
		public Icon icon() {
			return null;
		}

		@Override
		public String description() {
			return "Cluster membership information, these contain the cluster identifiers. (This is usually computed.)";
		}

		@Override
		public String defaultName() {
			return representation();
		}

		@Override
		public Collection<String> alternativeNames() {
			return Arrays.asList(defaultName(), "Cluster Membership", "class");
		}

		@Override
		public DataType preferredDataType() {
			return StringCell.TYPE;
		}

		@Override
		public Collection<DataType> disallowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public Collection<DataType> allowedDataTypes() {
			return Collections.emptySet();
		}

		@Override
		public boolean isUnique() {
			return false;
		}

		@Override
		public boolean isExclusive() {
			return false;
		}

		@Override
		public boolean shouldBeNominal(final DataType type) {
			return false;
		}

		@Override
		public boolean isNominalPreferred(final DataType type) {
			return StringCell.TYPE.isASuperTypeOf(type);
		}

		@Override
		public boolean isUniquePreferred() {
			return false;
		}

		@Override
		public boolean isExclusivePreferred() {
			return false;
		}

		@Override
		public void configurationChecks(final NodeModel model,
				final PortObjectSpec[] tableSpecs,
				final DataColumnSpec... columnSpecs) {
			// Do nothing
		}

	};

	private final String representation;

	private PredefinedRoles(final String representation) {
		this.representation = representation;
	}

	private PredefinedRoles() {
		this.representation = name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind_era.knime.roles.Role#getRepresentation()
	 */
	@Override
	public String representation() {
		return representation;
	}
}
