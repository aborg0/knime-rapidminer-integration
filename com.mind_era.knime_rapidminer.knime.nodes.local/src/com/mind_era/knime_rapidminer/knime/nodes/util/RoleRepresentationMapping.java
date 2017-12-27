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
package com.mind_era.knime_rapidminer.knime.nodes.util;

///**
// * Mapping between KNIME and RapidMiner role representations.
// * 
// * @author Gabor Bakos
// */
//public class RoleRepresentationMapping {
//	private static final RoleRepresentationMapping instance = new RoleRepresentationMapping();
//	private Map<Role, String> roleToRapidMinerRepresentation = new LinkedHashMap<>();
//	private Map<String, Role> rapidMinerRepresentationToRole = new LinkedHashMap<>();
//	{
//		rapidMinerRepresentationToRole.put(Attributes.LABEL_NAME, PredefinedRoles.label);
//		roleToRapidMinerRepresentation.put(PredefinedRoles.label, Attributes.LABEL_NAME);
//	}
//	private RoleRegistry registry = RapidMinerNodePlugin.getDefault().getRoleRegistry();
//
//	/**
//	 * 
//	 */
//	private RoleRepresentationMapping() {
//		super();
//	}
//	
//	/**
//	 * @return the instance
//	 */
//	public static RoleRepresentationMapping getInstance() {
//		return instance;
//	}
//	
//	public Role fromRapidMinerRoleName(String roleName) {
//		return fromRapidMinerRoleName(roleName, DataType.getMissingCell().getType(), roleName);
//	}
//
//	public Role fromRapidMinerRoleName(String roleName, DataType colType, String colName) {
//		if (rapidMinerRepresentationToRole.containsKey(roleName)) {
//			return rapidMinerRepresentationToRole.get(roleName);
//		}
//		if (registry.roleRepresentations().contains(roleName)) {
//			return registry.role(roleName);
//		}
//		return registry.createDynamicRole(roleName, colType, colName);
//	}
//	
//	public String rapidMinerRoleNameOf(Role role) {
//		if (roleToRapidMinerRepresentation.containsKey(role)) {
//			return roleToRapidMinerRepresentation.get(role);
//		}
//		return role.representation();
//	}
//}
