/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package com.mind_era.knime.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;

/**
 * Gets or sets properties for columns.
 * 
 * @author Gabor Bakos
 */
public class RoleHandler {

	private final RoleRegistry registry;

	/**
	 * Creates {@link RoleHandler}.
	 * 
	 * @param registry
	 *            A {@link RoleRegistry}.
	 */
	public RoleHandler(final RoleRegistry registry) {
		super();
		this.registry = registry;
	}

	/**
	 * Collects the {@link Role}s for each column.
	 * 
	 * @param spec
	 *            A {@link DataTableSpec}.
	 * @return A {@link Map} from column names to {@link Role}s.
	 */
	public Map<String, Collection<? extends Role>> roles(
			final DataTableSpec spec) {
		final Map<String, Collection<? extends Role>> ret = new TreeMap<>();
		for (final DataColumnSpec dataColumnSpec : spec) {
			final String property = dataColumnSpec.getProperties().getProperty(
					Role.PROPERTY_KEY);
			if (property != null) {
				ret.put(dataColumnSpec.getName(), rolesOfProperty(property));
			}
		}
		return ret;
	}

	/**
	 * @param property
	 *            A property text.
	 * @return {@link Role}s parsed from {@code property}.
	 */
	private List<? extends Role> rolesOfProperty(final String property) {
		return map(property.split(";"));
	}

	/**
	 * @param spec
	 *            A {@link DataColumnSpec}.
	 * @return The {@link Role}s parsed from {@code spec}'s
	 *         {@link Role#PROPERTY_KEY} {@link DataColumnSpec#getProperties()
	 *         property}.
	 */
	private List<? extends Role> rolesOfSpec(final DataColumnSpec spec) {
		final String property = spec.getProperties().getProperty(
				Role.PROPERTY_KEY);
		if (property == null) {
			return Collections.emptyList();
		}
		return rolesOfProperty(property);
	}

	/**
	 * @param list
	 *            Some {@link String}s {@link Role#representation()
	 *            representing} {@link Role}s.
	 * @return A {@link List} of {@link Role}s.
	 * @see RoleRegistry#role(String)
	 */
	private List<? extends Role> map(final String... list) {
		final List<Role> ret = new ArrayList<>(list.length);
		for (final String string : list) {
			ret.add(registry.role(string));
		}
		return Collections.unmodifiableList(ret);
	}

	/**
	 * Adds some roles to a column.
	 * 
	 * @param spec
	 *            A {@link DataColumnSpec}.
	 * @param roles
	 *            The {@link Role}s to add.
	 * @return A new {@link DataColumnSpec} with the additional {@code roles}.
	 */
	public DataColumnSpec addRoles(final DataColumnSpec spec,
			final Role... roles) {
		final DataColumnSpecCreator creator = new DataColumnSpecCreator(spec);
		final List<Role> rolesOfSpec = new ArrayList<>(rolesOfSpec(spec));
		for (final Role role : roles) {
			boolean found = false;
			for (final Role currentRole : rolesOfSpec) {
				found |= currentRole.equals(role);
			}
			if (!found) {
				rolesOfSpec.add(role);
			}
		}
		final String rolesAsString = rolesToString(rolesOfSpec);
		creator.setProperties(spec.getProperties().cloneAndOverwrite(
				rolesOfSpec.isEmpty() ? Collections.<String, String> emptyMap()
						: Collections.singletonMap(Role.PROPERTY_KEY,
								rolesAsString)));
		return creator.createSpec();
	}

	/**
	 * Sets roles of a column.
	 * 
	 * @param spec
	 *            A {@link DataColumnSpec}.
	 * @param roles
	 *            The {@link Role}s to set.
	 * @return A new {@link DataColumnSpec} with {@code roles} set.
	 */
	public DataColumnSpec setRoles(final DataColumnSpec spec,
			final Collection<? extends Role> roles) {
		final DataColumnSpecCreator creator = new DataColumnSpecCreator(spec);
		final String rolesAsString = rolesToString(roles);
		creator.setProperties(spec.getProperties().cloneAndOverwrite(
				roles.isEmpty() ? Collections.<String, String> emptyMap()
						: Collections.singletonMap(Role.PROPERTY_KEY,
								rolesAsString)));
		return creator.createSpec();
	}

	/**
	 * Computes the "serialized" version of {@code roles} to be saved in
	 * {@link DataColumnSpec#getProperties() properties}.
	 * 
	 * @param roles
	 *            Some {@link Role}s.
	 * @return {@link String} version of {@code roles}.
	 * @see #rolesToString(Iterable)
	 */
	public String rolesToString(final Role... roles) {
		return rolesToString(Arrays.asList(roles));
	}

	/**
	 * Computes the "serialized" version of {@code roles} to be saved in
	 * {@link DataColumnSpec#getProperties() properties}.
	 * 
	 * @param roles
	 *            Some {@link Role}s.
	 * @return {@link String} version of {@code roles}.
	 */
	public String rolesToString(final Iterable<? extends Role> roles) {
		final StringBuilder ret = new StringBuilder();
		for (final Iterator<? extends Role> iterator = roles.iterator(); iterator
				.hasNext();) {
			final Role role = iterator.next();
			ret.append(role.representation());
			if (iterator.hasNext()) {
				ret.append(';');
			}
		}
		return ret.toString();
	}
}
