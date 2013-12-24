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
package com.mind_era.knime_rapidminer.knime.nodes.common.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mind_era.knime_rapidminer.knime.nodes.common.Activator;

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
		final IPreferenceStore store = Activator.getDefault()
				.getPreferenceStore();
		store.setDefault(
				PreferenceConstants.RAPIDMINER_PATH,
				Platform.getOS().equals(Platform.OS_WIN32) ? "C:\\Program Files (x86)\\Rapid-I\\RapidMiner5"
						: "");	}

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
