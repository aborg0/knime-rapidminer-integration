/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://knime.org/downloads/full-license
 */
package com.mind_era.knime.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.Pair;

/**
 * A {@link SettingsModel} to provide a list of pairs.
 * 
 * @author Gabor Bakos
 * @param <Left>
 *            The type of left values.
 * @param <Right>
 *            The type of right values.
 */
public class SettingsModelPairs<Left extends DataCell, Right extends DataCell>
		extends SettingsModel {
	private static final String CFGKEY_LEFT_VALUES = "left-values";
	private static final String CFGKEY_RIGHT_VALUES = "right-values";
	private static final String CFGKEY_ENABLED_VALUES = "enabled-values";

	private final String configKey;
	private final List<Pair<Left, Right>> defaultValues;
	private final List<Pair<Left, Right>> values = new ArrayList<>();
	private final BitSet enabledRows = new BitSet();
	private final DataType leftType;
	private final DataType rightType;
	private final boolean isLeftUnique;
	private final boolean isRightUnique;

	/**
	 * @param configKey
	 *            The configuration key for the pairs.
	 * @param leftType
	 *            The DataType of left values.
	 * @param rightType
	 *            The {@link DataType} of right values.
	 * @param defaultValues
	 *            The default values of the model.
	 * @param isLeftUnique
	 *            Are the left values unique among the enabled rows?
	 * @param isRightUnique
	 *            Are the right values unique among the enabled rows?
	 */
	public SettingsModelPairs(final String configKey, final DataType leftType,
			final DataType rightType,
			final Collection<? extends Pair<Left, Right>> defaultValues,
			final boolean isLeftUnique, final boolean isRightUnique) {
		super();
		this.configKey = configKey;
		this.leftType = leftType;
		this.rightType = rightType;
		this.isLeftUnique = isLeftUnique;
		this.isRightUnique = isRightUnique;
		this.defaultValues = new ArrayList<>(defaultValues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModel#createClone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected SettingsModelPairs<Left, Right> createClone() {
		final SettingsModelPairs<Left, Right> ret = new SettingsModelPairs<>(
				configKey, leftType, rightType, defaultValues, isLeftUnique,
				isRightUnique);
		ret.values.addAll(this.values);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#getModelTypeID()
	 */
	@Override
	protected String getModelTypeID() {
		return "PairList\u00a0" + leftType + "\u00a0" + rightType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#getConfigName()
	 */
	@Override
	protected String getConfigName() {
		return configKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#loadSettingsForDialog
	 * (org.knime.core.node.NodeSettingsRO,
	 * org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void loadSettingsForDialog(final NodeSettingsRO settings,
			final PortObjectSpec[] specs) {
		try {
			loadSettingsForModel(settings);
		} catch (final InvalidSettingsException e) {
			// keep old settings
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#saveSettingsForDialog
	 * (org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsForDialog(final NodeSettingsWO settings) {
		saveSettingsForModel(settings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModel#
	 * validateSettingsForModel(org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void validateSettingsForModel(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		final DataCell[] leftValues = settings.getDataCellArray(
				CFGKEY_LEFT_VALUES, getLeftDefaults());
		final DataCell[] rightValues = settings.getDataCellArray(
				CFGKEY_RIGHT_VALUES, getRightDefaults());
		if (leftValues.length != rightValues.length) {
			throw new InvalidSettingsException(
					"Different number of left and right values: "
							+ leftValues.length + " - " + rightValues.length);
		}
		if (isLeftUnique) {
			final Set<DataCell> cells = new HashSet<>();
			int i = 0;
			for (final DataCell dataCell : leftValues) {
				if (enabledRows.get(i++) && !cells.add(dataCell)) {
					throw new InvalidSettingsException(dataCell
							+ " is not unique in "
							+ Arrays.toString(leftValues));
				}
			}
		}
		if (isRightUnique) {
			final Set<DataCell> cells = new HashSet<>();
			int i = 0;
			for (final DataCell dataCell : rightValues) {
				if (enabledRows.get(i++) && !cells.add(dataCell)) {
					throw new InvalidSettingsException(dataCell
							+ " is not unique in "
							+ Arrays.toString(rightValues));
				}
			}
		}
	}

	/**
	 * @return The left default values.
	 */
	private DataCell[] getLeftDefaults() {
		final DataCell[] ret = new DataCell[defaultValues.size()];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = defaultValues.get(i).getFirst();
		}
		return ret;
	}

	/**
	 * @return The right default values.
	 */
	private DataCell[] getRightDefaults() {
		final DataCell[] ret = new DataCell[defaultValues.size()];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = defaultValues.get(i).getSecond();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#loadSettingsForModel
	 * (org.knime.core.node.NodeSettingsRO)
	 */
	@Override
	protected void loadSettingsForModel(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		values.clear();
		final DataCell[] leftValues = settings.getDataCellArray(
				CFGKEY_LEFT_VALUES, getLeftDefaults());
		final DataCell[] rightValues = settings.getDataCellArray(
				CFGKEY_RIGHT_VALUES, getRightDefaults());
		if (leftValues.length != rightValues.length) {
			throw new InvalidSettingsException(
					"Different number of left and right values: "
							+ leftValues.length + " - " + rightValues.length);
		}
		final boolean[] enabledRowsValues = settings.getBooleanArray(
				CFGKEY_ENABLED_VALUES, new boolean[leftValues.length]);
		for (int i = 0; i < enabledRowsValues.length; i++) {
			enabledRows.set(i, enabledRowsValues[i]);
		}
		for (int i = 0; i < leftValues.length; ++i) {
			@SuppressWarnings("unchecked")
			final Pair<Left, Right> newPair = (Pair<Left, Right>) new Pair<>(
					leftValues[i], rightValues[i]);
			values.add(newPair);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.SettingsModel#saveSettingsForModel
	 * (org.knime.core.node.NodeSettingsWO)
	 */
	@Override
	protected void saveSettingsForModel(final NodeSettingsWO settings) {
		final ArrayList<DataCell> leftValues = new ArrayList<>(values.size());
		final ArrayList<DataCell> rightValues = new ArrayList<>(values.size());
		for (final Pair<Left, Right> pair : values) {
			leftValues.add(pair.getFirst());
			rightValues.add(pair.getSecond());
		}
		settings.addDataCellArray(CFGKEY_LEFT_VALUES,
				leftValues.toArray(new DataCell[values.size()]));
		settings.addDataCellArray(CFGKEY_RIGHT_VALUES,
				rightValues.toArray(new DataCell[values.size()]));
		settings.addBooleanArray(CFGKEY_ENABLED_VALUES,
				toBooleanArray(enabledRows));
	}

	/**
	 * Converts a {@link BitSet} to a {@code boolean} array.
	 * 
	 * @param bitset
	 *            A {@link BitSet}.
	 * @return The {@code boolean} array, where the true values represent the
	 *         numbers present in the {@code bitset}. (Only non-negative
	 *         indices.)
	 */
	private static boolean[] toBooleanArray(final BitSet bitset) {
		final boolean[] ret = new boolean[bitset.length()];
		for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
			ret[i] = true;
		}
		return ret;
	}

	/**
	 * @return The {@link Pair}s that belong to enabled rows.
	 * @see #getEnabledRows()
	 */
	public Collection<Pair<Left, Right>> getEnabledPairs() {
		final Collection<Pair<Left, Right>> ret = new ArrayList<>();
		int i = 0;
		for (final Pair<Left, Right> pair : values) {
			if (enabledRows.get(i++)) {
				ret.add(pair);
			}
		}
		return ret;
	}

	/**
	 * @return The values.
	 */
	protected List<Pair<Left, Right>> getValues() {
		return values;
	}

	/**
	 * @return The enabled rows.
	 */
	protected BitSet getEnabledRows() {
		return enabledRows;
	}

	/**
	 * @param enabledRows
	 *            The new enabled rows.
	 */
	protected void setEnabledRows(final BitSet enabledRows) {
		this.enabledRows.clear();
		this.enabledRows.or(enabledRows);
	}

	/**
	 * @param vs
	 *            The values that should be visible on the
	 *            {@link DialogComponent}.
	 */
	protected void setValues(final Collection<Pair<Left, Right>> vs) {
		values.clear();
		values.addAll(vs);
	}

	/**
	 * @return the leftType
	 */
	public DataType getLeftType() {
		return leftType;
	}

	/**
	 * @return the rightType
	 */
	public DataType getRightType() {
		return rightType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.SettingsModel#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " ('" + configKey + "')";
	}

}
