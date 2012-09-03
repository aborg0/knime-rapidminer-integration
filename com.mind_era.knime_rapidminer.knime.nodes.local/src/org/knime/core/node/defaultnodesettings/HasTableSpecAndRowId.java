/*
 *
 */
package org.knime.core.node.defaultnodesettings;

import java.util.List;

import org.knime.core.data.DataTableSpec;

/**
 * The row id related preferences with convenience method.
 * 
 * @author Gabor Bakos
 */
public interface HasTableSpecAndRowId {

	/**
	 * @return The existing and compatible table specs.
	 */
	public List<? extends DataTableSpec> getFilteredTableSpecs();

	public boolean isWithRowIds();

	public String getRowIdColumnName();

}