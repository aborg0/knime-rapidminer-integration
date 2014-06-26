/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;

import com.mind_era.knime.roles.RoleCheck.CheckResult.Violation;
import com.mind_era.knime.roles.RoleCheck.CheckResult.ViolationKind;

/**
 * Some helper methods to test whether the {@link DataTableSpec} is consistent
 * with the roles' properties or not.
 * 
 * @author Gabor Bakos
 */
public class RoleCheck {

	private final RoleHandler handler;

	/**
	 * 
	 * 
	 * @author Gabor Bakos
	 */
	public static class CheckResult implements Serializable {

		private static final long serialVersionUID = 2808531683664690905L;
		private final Collection<Violation> warnings;
		private final Collection<Violation> errors;

		/**
		 * Represents a violation of {@link Role} contracts. Can be warning or
		 * error depending on the {@link CheckResult}'s collection. <br/>
		 * All implementations should provide a sensible {@link #hashCode()} and
		 * a {@link #equals(Object)} method.
		 */
		public static interface Violation {
			/** @return The {@link ViolationKind}. */
			ViolationKind kind();

			/** @return The {@link Role}s associated to the {@link Violation}. */
			Collection<Role> interferingRoles();

			/** @return The column names associated to the {@link Violation}. */
			Collection<String> columns();

			/**
			 * Default implementation of {@link Violation}.
			 */
			static final class Default implements Violation, Serializable {
				private static final long serialVersionUID = 1153842521067481533L;
				private final ViolationKind kind;
				private final Collection<Role> interferingRoles;
				private final Collection<String> columns;

				Default(final ViolationKind kind,
						final Collection<? extends Role> interferingRoles,
						final Collection<? extends String> columns) {
					this.kind = kind;
					this.interferingRoles = Collections
							.unmodifiableList(new ArrayList<>(interferingRoles));
					this.columns = Collections
							.unmodifiableList(new ArrayList<>(columns));
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.mind_era.knime.roles.RoleCheck.CheckResult.Violation#
				 * kind()
				 */
				@Override
				public ViolationKind kind() {
					return kind;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.mind_era.knime.roles.RoleCheck.CheckResult.Violation#
				 * interferingRoles()
				 */
				@Override
				public Collection<Role> interferingRoles() {
					return interferingRoles;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.mind_era.knime.roles.RoleCheck.CheckResult.Violation#
				 * columns()
				 */
				@Override
				public Collection<String> columns() {
					return columns;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Object#hashCode()
				 */
				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result
							+ (columns == null ? 0 : columns.hashCode());
					result = prime
							* result
							+ (interferingRoles == null ? 0 : interferingRoles
									.hashCode());
					result = prime * result
							+ (kind == null ? 0 : kind.hashCode());
					return result;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Object#equals(java.lang.Object)
				 */
				@Override
				public boolean equals(final Object obj) {
					if (this == obj) {
						return true;
					}
					if (obj == null) {
						return false;
					}
					if (getClass() != obj.getClass()) {
						return false;
					}
					final Default other = (Default) obj;
					if (columns == null) {
						if (other.columns != null) {
							return false;
						}
					} else if (!columns.equals(other.columns)) {
						return false;
					}
					if (interferingRoles == null) {
						if (other.interferingRoles != null) {
							return false;
						}
					} else if (!interferingRoles.equals(other.interferingRoles)) {
						return false;
					}
					if (kind != other.kind) {
						return false;
					}
					return true;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Object#toString()
				 */
				@Override
				public String toString() {
					// TODO improve!!
					return "Violation [kind=" + kind + ", interferingRoles="
							+ interferingRoles + ", columns=" + columns + "]";
				}
			}
		}

		/**
		 * Specifies the possible types of violations.
		 */
		public static enum ViolationKind {
			/** This role is associated to multiple columns. */
			uniqueness,
			/** There are other {@link Role}(s) added to the column. */
			exclusivity,
			/** The associated column is not nominal. */
			nominal,
			/** Incompatible type for the {@link Role}. */
			type,
			/** The {@link Role} is not present in the data table. */
			missing;
		}

		CheckResult(final Collection<Violation> warnings,
				final Collection<Violation> errors) {
			this.warnings = Collections.unmodifiableList(new ArrayList<>(
					warnings));
			this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
		}

		/**
		 * @return the errors
		 */
		public Collection<Violation> getErrors() {
			return errors;
		}

		/**
		 * @return the warnings
		 */
		public Collection<Violation> getWarnings() {
			return warnings;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CheckResult [warnings=" + warnings + ", errors=" + errors
					+ "]";
		}
	}

	/**
	 * Constructs a {@link RoleCheck}.
	 * 
	 * @param handler
	 *            The {@link RoleHandler} to use.
	 */
	public RoleCheck(final RoleHandler handler) {
		super();
		this.handler = handler;
	}

	/**
	 * Checks whether the {@link Role}s in {@code spec} are consistent with
	 * their definitions or not.
	 * 
	 * @param spec
	 *            A {@link DataTableSpec}.
	 * @return The warnings and errors as {@link Violation}s.
	 */
	public CheckResult check(final DataTableSpec spec) {
		final List<Violation> warnings = new ArrayList<>();
		final List<Violation> errors = new ArrayList<>();
		final Map<String, Collection<? extends Role>> roles = handler
				.roles(spec);
		final Set<Role> seenRoles = new HashSet<>();
		for (final Entry<String, Collection<? extends Role>> entry : roles
				.entrySet()) {
			final DataColumnSpec columnSpec = spec
					.getColumnSpec(entry.getKey());
			for (final Role role : entry.getValue()) {
				if (seenRoles.contains(role)) {
					if (role.isUnique() || role.isUniquePreferred()) {
						final Violation.Default violation = new Violation.Default(
								ViolationKind.uniqueness,
								Collections.singleton(role),
								Collections.singleton(entry.getKey()));
						(role.isUnique() ? errors : warnings).add(violation);
					}
				}
				if (entry.getValue().size() > 1) {
					if (role.isExclusive() || role.isExclusivePreferred()) {
						(role.isExclusive() ? errors : warnings)
								.add(new Violation.Default(
										ViolationKind.exclusivity, entry
												.getValue(), Collections
												.singleton(entry.getKey())));
					}
				}
				final DataType type = columnSpec.getType();
				if (columnSpec.getDomain().getValues().isEmpty()) {
					if (role.shouldBeNominal(type)
							|| role.isNominalPreferred(type)) {
						(role.shouldBeNominal(type) ? errors : warnings)
								.add(new Violation.Default(
										ViolationKind.nominal, Collections
												.singleton(role), Collections
												.singleton(entry.getKey())));
					}
				}
				for (final DataType disallowed : role.disallowedDataTypes()) {
					if (disallowed.isASuperTypeOf(type)) {
						errors.add(new Violation.Default(ViolationKind.type,
								Collections.singleton(role), Collections
										.singleton(entry.getKey())));
						break;
					}
				}
				seenRoles.add(role);
			}
		}
		fix(errors);
		fix(warnings);
		return new CheckResult(warnings, errors);
	}

	/**
	 * @param violations
	 *            {@link Violation}s with not optimal parameters
	 */
	private void fix(final List<Violation> violations) {
		final Map<Role, Collection<String>> uniqueViolations = new HashMap<>();
		final Map<String, Collection<Role>> exclusiveViolations = new HashMap<>();
		for (final Violation violation : violations) {
			switch (violation.kind()) {
			case uniqueness: {
				for (final Role r : violation.interferingRoles()) {
					if (!uniqueViolations.containsKey(r)) {
						uniqueViolations.put(r, new TreeSet<String>());
					}
					uniqueViolations.get(r).addAll(violation.columns());
				}
				break;
			}
			case exclusivity: {
				for (final String col : violation.columns()) {
					if (exclusiveViolations.containsKey(col)) {
						exclusiveViolations.put(col, new HashSet<Role>());
					}
					exclusiveViolations.get(col).addAll(
							violation.interferingRoles());
				}
				break;
			}
			case missing: {
				// do nothing
				break;
			}
			case nominal: {
				// do nothing
				break;
			}
			case type: {
				// do nothing
				break;
			}
			default:
				break;
			}
		}
		for (final Iterator<Violation> it = violations.iterator(); it.hasNext();) {
			final Violation violation = it.next();
			switch (violation.kind()) {
			case exclusivity:
				it.remove();
				break;
			case uniqueness:
				it.remove();
				break;
			case missing:
				// keep
				break;
			case nominal:
				// keep
				break;
			case type:
				// keep
				break;
			default:
				break;
			}
		}
		for (final Entry<Role, Collection<String>> entry : uniqueViolations
				.entrySet()) {
			violations.add(new Violation.Default(ViolationKind.uniqueness,
					Collections.singleton(entry.getKey()), Collections
							.unmodifiableCollection(entry.getValue())));
		}
		for (final Entry<String, Collection<Role>> entry : exclusiveViolations
				.entrySet()) {
			violations.add(new Violation.Default(ViolationKind.exclusivity,
					entry.getValue(), Collections.singleton(entry.getKey())));
		}
	}

	/**
	 * {@link #check(DataTableSpec) Checks} the {@code spec} and requires the
	 * {@code requiredRoles} to be present in the {@code spec}, else error(s)
	 * will be reported.
	 * 
	 * @param spec
	 *            A {@link DataTableSpec}.
	 * @param requiredRoles
	 *            The required {@link Role}s.
	 * @return The result of {@link #check(DataTableSpec)} and the violations of
	 *         required roles.
	 */
	public CheckResult checkAndRequire(final DataTableSpec spec,
			final Iterable<Role> requiredRoles) {
		final Map<String, Collection<? extends Role>> roles = handler
				.roles(spec);
		final CheckResult preCheck = check(spec);
		final List<Violation> newErrors = new ArrayList<>(preCheck.getErrors());
		final Set<Role> seenRoles = new HashSet<>();
		for (final Collection<? extends Role> rs : roles.values()) {
			for (final Role role : rs) {
				seenRoles.add(role);
			}
		}
		for (final Role required : requiredRoles) {
			if (!seenRoles.contains(required)) {
				newErrors.add(new Violation.Default(ViolationKind.missing,
						Collections.singleton(required), Collections
						// TODO filter all to compatible columns
								.<String> emptySet()));
			}
		}
		return new CheckResult(preCheck.getWarnings(), newErrors);
	}
}
