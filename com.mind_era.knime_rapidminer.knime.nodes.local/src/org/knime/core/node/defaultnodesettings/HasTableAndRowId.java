/*  RapidMiner Integration for KNIME
 *  Copyright (C) 2012 Mind Eratosthenes Kft.
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
package org.knime.core.node.defaultnodesettings;

import java.util.List;

import org.knime.core.node.BufferedDataTable;

/**
 * An interface for additional info for the table content.
 * 
 * @author Gabor Bakos
 */
public interface HasTableAndRowId extends HasTableSpecAndRowId {
	/**
	 * @return The available tables.
	 */
	public List<BufferedDataTable> getFilteredTables();
	
	/**
	 * @return the tables are really available.
	 */
	public boolean isReallyHaveTables();
}
