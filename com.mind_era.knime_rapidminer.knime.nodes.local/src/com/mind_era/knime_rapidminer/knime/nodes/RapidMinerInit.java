/**
 * 
 */
package com.mind_era.knime_rapidminer.knime.nodes;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.node.defaultnodesettings.HasTableSpecAndRowId;

import com.mind_era.knime_rapidminer.knime.nodes.internal.RapidMinerNodePlugin;
import com.mind_era.knime_rapidminer.knime.nodes.preferences.PreferenceConstants;
import com.mind_era.knime_rapidminer.knime.nodes.preferences.PreferenceInitializer;
import com.mind_era.knime_rapidminer.knime.nodes.util.KnimeRepository;
import com.rapid_i.Launcher;
import com.rapid_i.deployment.update.client.ManagedExtension;
import com.rapidminer.RapidMiner;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.repository.Repository;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.repository.RepositoryFactory;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.ParameterService;

/**
 * Initialize RapidMiner based on eclipse settings.
 * 
 * @author Gabor
 */
public class RapidMinerInit {
	private static boolean isInitialized = false;

	public static synchronized void init(final boolean force) {
		if (!isInitialized || force) {
			final String rapidMinerHome = RapidMinerNodePlugin.getDefault()
					.getPreferenceStore()
					.getString(PreferenceConstants.RAPIDMINER_PATH);
			System.setProperty(Launcher.PROPERTY_RAPIDMINER_HOME,
					rapidMinerHome/*
								 * . getAbsolutePath ( )
								 */);
			RapidMiner
					.setExecutionMode(RapidMiner.ExecutionMode.EMBEDDED_WITH_UI);
			RepositoryManager.registerFactory(new RepositoryFactory() {

				@Override
				public List<? extends Repository> createRepositoriesFor(
						final RepositoryAccessor accessor) {
					if (accessor instanceof HasTableSpecAndRowId) {
						final HasTableSpecAndRowId model = (HasTableSpecAndRowId) accessor;
						return Collections.singletonList(new KnimeRepository(
								model));

					}
					return Collections.emptyList();
				}
			});
			ManagedExtension.init();
			// RapidMiner.showSplash();
			RapidMiner.init();
			// RapidMiner.hideSplash();
			/*
			 * Plugin.setInitPlugins(true);
			 * Plugin.setPluginLocation(rapidMinerHome + File.separator + "lib"
			 * + File.separator + "plugins"); Plugin.initAll();
			 */
			isInitialized = true;
		}
	}

	/**
	 * 
	 */
	public static synchronized void setPreferences() {
		final IPreferenceStore store = RapidMinerNodePlugin.getDefault()
				.getPreferenceStore();
		for (final String parameterKey : ParameterService.getParameterKeys()) {
			final ParameterType type = ParameterService
					.getParameterType(parameterKey);
			if (type == null) {
				continue;
			}
			if (type instanceof ParameterTypeBoolean) {
				ParameterService.setParameterValue(parameterKey, Boolean
						.toString(store.getBoolean(PreferenceInitializer
								.getRapidminerPreferenceKey(parameterKey))));
			} else if (type instanceof ParameterTypeInt) {
				ParameterService.setParameterValue(parameterKey, Integer
						.toString(store.getInt(PreferenceInitializer
								.getRapidminerPreferenceKey(parameterKey))));
			} else if (type instanceof ParameterTypeStringCategory) {
				ParameterService.setParameterValue(parameterKey, Integer
						.toString(store.getInt(PreferenceInitializer
								.getRapidminerPreferenceKey(parameterKey))));
			} else {
				ParameterService.setParameterValue(parameterKey, store
						.getString(PreferenceInitializer
								.getRapidminerPreferenceKey(parameterKey)));
			}
		}
	}
}
