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
package com.mind_era.knime_rapidminer.knime.nodes;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.defaultnodesettings.HasTableSpecAndRowId;

import com.mind_era.knime_rapidminer.knime.nodes.common.Activator;
import com.mind_era.knime_rapidminer.knime.nodes.common.preferences.PreferenceConstants;
import com.mind_era.knime_rapidminer.knime.nodes.internal.RapidMinerNodePlugin;
import com.mind_era.knime_rapidminer.knime.nodes.preferences.PreferenceInitializer;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapidminer.RapidMiner;
import com.rapidminer.gui.AbstractUIState;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.repository.Repository;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.repository.RepositoryFactory;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.PlatformUtilities;
import com.rapidminer.tools.plugin.ManagedExtension;

/**
 * Initialize RapidMiner based on eclipse settings.
 *
 * @author Gabor
 */
public class RapidMinerInit {
	private static volatile boolean isInitialized = false, isInitializing = false;
	private static volatile boolean preferencesSet = false;

	public static synchronized void init(final boolean force) {
		final boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
				.contains(/* "-agentlib:"+ */"jdwp");
		if (isDebug) {
			UIManager.put("FileChooser.noPlacesBar", Boolean.TRUE);
		}
		if (!isInitialized || force) {
			if (isInitializing && !force) {
				Logger.getAnonymousLogger().warning(
						"Initializing RapidMiner from another initializer, or the first initialization was not finished properly.");
				return;
			}
			isInitializing = true;
			String rapidMinerHome;
			try {
				rapidMinerHome = Activator.getDefault().getPreferenceStore()
						.getString(PreferenceConstants.RAPIDMINER_PATH);
			} catch (final Exception e) {
				rapidMinerHome = "/c:/Program Files/RapidMiner/RapidMiner Studio";
				e.printStackTrace();
			}
			System.setProperty(PlatformUtilities.PROPERTY_RAPIDMINER_HOME, rapidMinerHome);
			RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.EMBEDDED_WITH_UI);
			RepositoryManager.registerFactory(new RepositoryFactory() {

				@Override
				public List<? extends Repository> createRepositoriesFor(final RepositoryAccessor accessor) {
					if (accessor instanceof HasTableSpecAndRowId) {
						final HasTableSpecAndRowId model = (HasTableSpecAndRowId) accessor;
						return Collections.singletonList(new KnimeRepository(model));

					}
					return Collections.emptyList();
				}
			});
			ManagedExtension.init();

			RapidMiner.init();
			// Initialize the static initializers for MainFrame and
			// AbstractUIPlugin
			@SuppressWarnings("unused")
			final String unused = MainFrame.PROPERTY_RAPIDMINER_GUI_LOG_LEVEL.toString() + AbstractUIState.TITLE;
			// End of static init.

			isInitialized = true;
			isInitializing = false;
		}
	}

	/**
	 *
	 */
	public static synchronized void setPreferences() {
		setPreferences(true);
	}

	public static synchronized void setPreferences(boolean force) {
		if (!preferencesSet || force) {
			final IPreferenceStore store = RapidMinerNodePlugin.getDefault().getPreferenceStore();
			for (final String parameterKey : ParameterService.getParameterKeys()) {
				final ParameterType type = ParameterService.getParameterType(parameterKey);
				final String storeKey = PreferenceInitializer.getRapidminerPreferenceKey(parameterKey);
				if (type instanceof ParameterTypeBoolean) {
					ParameterService.setParameterValue(parameterKey, Boolean.toString(store.getBoolean(storeKey)));
				} else if (type instanceof ParameterTypeInt) {
					ParameterService.setParameterValue(parameterKey, Integer.toString(store.getInt(storeKey)));
				} else if (type instanceof ParameterTypeStringCategory) {
					ParameterService.setParameterValue(parameterKey, Integer.toString(store.getInt(storeKey)));
				} else {
					if (type != null && type.getDefaultValueAsString() != null
							&& !type.getDefaultValueAsString().equals(store.getDefaultString(storeKey))) {
						store.setDefault(storeKey, type.getDefaultValueAsString());
					}
					ParameterService.setParameterValue(parameterKey, store.getString(storeKey));
				}
			}
			preferencesSet = true;
		}
	}
}
