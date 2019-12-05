/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2013 Mind Eratosthenes Kft.
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
package com.mind_era.knime_rapidminer.knime.nodes.util;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;

/**
 * A simple container for an {@link Attribute} and its role.
 * 
 * @author Gabor Bakos
 */
public final class AttributeWithRole {

	private final Attribute attribute;
	private final AttributeRole role;

	/**
	 * Constructs the pair.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to store.
	 * @param role
	 *            The role of {@code attribute}. ({@code null} means no role,
	 *            regular.)
	 */
	public AttributeWithRole(Attribute attribute, AttributeRole role) {
		super();
		this.attribute = attribute;
		this.role = role;
	}

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/**
	 * @return the role
	 */
	public AttributeRole getRole() {
		return role;
	}
}
