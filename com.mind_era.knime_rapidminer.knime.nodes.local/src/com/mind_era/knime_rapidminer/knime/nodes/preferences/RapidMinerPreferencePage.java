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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mind_era.knime_rapidminer.knime.nodes.internal.RapidMinerNodePlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class RapidMinerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public RapidMinerPreferencePage() {
		super(GRID);
		final IPreferenceStore preferenceStore = RapidMinerNodePlugin
				.getDefault().getPreferenceStore();
		setPreferenceStore(preferenceStore);
		preferenceStore
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(final PropertyChangeEvent pce) {
						if (pce.getProperty().equals(
								PreferenceConstants.RAPIDMINER_PATH)) {
							final Object old = pce.getOldValue();
							final Object newValue = pce.getNewValue();
							if (newValue != null && !newValue.equals(old)) {
								MessageDialog.openWarning(
										RapidMinerPreferencePage.this
												.getShell(), "Restart Eclipse",
										"Please restart eclipse to get the effects of the new RapidMiner path.");
								// RapidMinerInit.init(true);
							}
						}
					}
				});
		setDescription("RapidMiner related preferences");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.RAPIDMINER_PATH,
				"&RapidMiner location: ", getFieldEditorParent()));
		// addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN,
		// "&An example of a boolean preference", getFieldEditorParent()));

		// addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE,
		// "An example of a multiple-choice preference", 1,
		// new String[][] { { "&Choice 1", "choice1" },
		// { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		// addField(new StringFieldEditor(PreferenceConstants.P_STRING,
		// "A &text preference:", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		// Nothing special
	}

}