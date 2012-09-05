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
package com.mind_era.knime_rapidminer.knime.nodes.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.internal.RapidMinerNodePlugin;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeChar;
import com.rapidminer.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.parameter.ParameterTypeDatabaseSchema;
import com.rapidminer.parameter.ParameterTypeDatabaseTable;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInnerOperator;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeParameterValue;
import com.rapidminer.parameter.ParameterTypeSQLQuery;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.parameter.ParameterTypeValue;
import com.rapidminer.tools.ParameterService;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	private static final String RAPIDMINER_PREFERENCE_PREFIX = "com.mind_era.knime.rm.";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = RapidMinerNodePlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(
				PreferenceConstants.RAPIDMINER_PATH,
				Platform.getOS().equals(Platform.OS_WIN32) ? "C:\\Program Files (x86)\\Rapid-I\\RapidMiner5"
						: "");
		RapidMinerInit.init(false);
		for (final String parameterKey : ParameterService.getParameterKeys()) {
			final String key = getRapidminerPreferenceKey(parameterKey);
			final ParameterType parameterType = ParameterService
					.getParameterType(parameterKey);
			if (parameterType instanceof ParameterTypeChar) {
				final ParameterTypeChar charType = (ParameterTypeChar) parameterType;
				store.setDefault(key, charType.getDefaultValueAsString());
			}
			if (parameterType instanceof ParameterTypeBoolean) {
				final ParameterTypeBoolean boolType = (ParameterTypeBoolean) parameterType;
				store.setDefault(key, boolType.getDefault());
			}
			if (parameterType instanceof ParameterTypeDouble) {
				final ParameterTypeDouble doubleType = (ParameterTypeDouble) parameterType;
				final Number defaultValue = (Number) doubleType
						.getDefaultValue();
				store.setDefault(key,
						defaultValue == null ? 0.0 : defaultValue.doubleValue());
			}
			if (parameterType instanceof ParameterTypeInt) {
				final ParameterTypeInt intType = (ParameterTypeInt) parameterType;
				store.setDefault(key, intType.getDefaultInt());
			}
			if (parameterType instanceof ParameterTypeString) {
				final ParameterTypeString stringType = (ParameterTypeString) parameterType;
				String defaultValueAsString = stringType.getDefaultValueAsString();
				store.setDefault(key, defaultValueAsString == null ? "" : defaultValueAsString);
			}
			if (parameterType instanceof ParameterTypeStringCategory) {
				final ParameterTypeStringCategory stringCatType = (ParameterTypeStringCategory) parameterType;
				store.setDefault(key, stringCatType.getDefaultValueAsString());
			}
			if (parameterType instanceof ParameterTypeSQLQuery) {
				final ParameterTypeSQLQuery sqlType = (ParameterTypeSQLQuery) parameterType;
				store.setDefault(key, sqlType.getDefaultValueAsString());
			}
			if (parameterType instanceof ParameterTypeCategory) {
				final ParameterTypeCategory categoryType = (ParameterTypeCategory) parameterType;
				store.setDefault(key, categoryType.getDefault());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeValue) {
				final ParameterTypeValue valueType = (ParameterTypeValue) parameterType;
				store.setDefault(key, valueType.getDefaultValueAsString());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeParameterValue) {
				final ParameterTypeParameterValue parameterValueType = (ParameterTypeParameterValue) parameterType;
				store.setDefault(key,
						parameterValueType.getDefaultValueAsString());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeInnerOperator) {
				final ParameterTypeInnerOperator innerType = (ParameterTypeInnerOperator) parameterType;
				store.setDefault(key, innerType.getDefaultValueAsString());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeDatabaseConnection) {
				final ParameterTypeDatabaseConnection dbConnType = (ParameterTypeDatabaseConnection) parameterType;
				store.setDefault(key, dbConnType.getDefaultValueAsString());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeDatabaseSchema) {
				final ParameterTypeDatabaseSchema dbSchemaType = (ParameterTypeDatabaseSchema) parameterType;
				store.setDefault(key, dbSchemaType.getDefaultValueAsString());
			}
			// TODO should support?
			if (parameterType instanceof ParameterTypeDatabaseTable) {
				final ParameterTypeDatabaseTable dbTableType = (ParameterTypeDatabaseTable) parameterType;
				store.setDefault(key, dbTableType.getDefaultValueAsString());
			}
			// if (parameterType instanceof ParameterTypeFile) {
			// final ParameterTypeFile fileType = (ParameterTypeFile)
			// parameterType;
			// store.setDefault(key, fileType.getDefaultValueAsString());
			// }
			// if (parameterType instanceof ParameterTypeDirectory) {
			// final ParameterTypeDirectory dirType = (ParameterTypeDirectory)
			// parameterType;
			// store.setDefault(key, dirType.getDefaultValueAsString());
			// }
		}
		// store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		// store.setDefault(PreferenceConstants.P_CHOICE, "choice2");
		// store.setDefault(PreferenceConstants.P_STRING, "Default value");
	}

	/**
	 * @param parameterKey
	 *            A parameter key.
	 * @return The {@code parameterKey} appended to the RapidMiner preference
	 *         prefix.
	 */
	public static String getRapidminerPreferenceKey(final String parameterKey) {
		return RAPIDMINER_PREFERENCE_PREFIX + parameterKey;
	}
}
