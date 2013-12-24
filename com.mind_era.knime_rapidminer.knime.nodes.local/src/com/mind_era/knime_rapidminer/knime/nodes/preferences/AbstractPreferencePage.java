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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.mind_era.knime_rapidminer.knime.nodes.RapidMinerInit;
import com.mind_era.knime_rapidminer.knime.nodes.internal.RapidMinerNodePlugin;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeChar;
import com.rapidminer.parameter.ParameterTypeColor;
import com.rapidminer.parameter.ParameterTypeDirectory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypePassword;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.tools.ParameterService;

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
public class AbstractPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Special key for unknown groups. */
	private static final String OTHER = "other";

	private final String group;

	private static final Set<String> predefinedGroups = new TreeSet<String>();
	static {
		for (final String name : Arrays.asList("general", "system", "gui",
				"init", "tools", "parallel", "update")) {
			predefinedGroups.add(name);
		}
		assert !predefinedGroups.contains(OTHER);
	}

	protected AbstractPreferencePage(final String group) {
		super(group, ImageDescriptor.createFromImageData(ImageDescriptor
				.createFromFile(AbstractPreferencePage.class,
						"rapidminer_190_49.jpg").getImageData()
				.scaledTo(90, 25)), GRID);
		this.group = group;
		RapidMinerInit.init(false);
		setPreferenceStore(RapidMinerNodePlugin.getDefault()
				.getPreferenceStore());
		setDescription("The RapidMiner settings used within KNIME.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		for (final String parameterKey : ParameterService.getParameterKeys()) {
			final ParameterType type = ParameterService
					.getParameterType(parameterKey);
			if (type == null) {
				continue;
			}
			final String value = ParameterService
					.getParameterValue(parameterKey);
			final String key = PreferenceInitializer
					.getRapidminerPreferenceKey(parameterKey);
			final String group = ParameterService.getGroupKey(parameterKey);
			if (this.group.equals(OTHER) && !isPredefined(group)
					|| this.group.equals(group)) {
				final FieldEditor fieldEditor;
				final String description = type.getDescription();
				final String label = description.length() > 140 ? description
						.contains("(") ? description.substring(0,
						description.indexOf('(') - 1) : description.substring(
						0, 140) : description;
				if (type instanceof ParameterTypeInt) {
					final ParameterTypeInt intType = (ParameterTypeInt) type;
					final IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(
							key, label, getFieldEditorParent());
					integerFieldEditor.setValidRange(intType.isOptional() ? Math.min(-1, intType.getMinValueInt()) : intType.getMinValueInt(),
							intType.getMaxValueInt());
					integerFieldEditor.setStringValue(value);
					fieldEditor = integerFieldEditor;
				} else if (type instanceof ParameterTypeDouble) {
					final ParameterTypeDouble doubleType = (ParameterTypeDouble) type;
					final StringFieldEditor stringFieldEditor = new StringFieldEditor(
							key, label, getFieldEditorParent());
					stringFieldEditor.setStringValue(value);
					fieldEditor = stringFieldEditor;
				} else if (type instanceof ParameterTypeStringCategory) {
					final ParameterTypeStringCategory catType = (ParameterTypeStringCategory) type;
					final String[] vals = catType.getValues();
					final String[][] labelAndValues = new String[vals.length][2];
					for (int i = labelAndValues.length; i-- > 0;) {
						labelAndValues[i][0] = vals[i];
						labelAndValues[i][1] = Integer.toString(i);
					}
					final RadioGroupFieldEditor stringFieldEditor = new RadioGroupFieldEditor(
							key, label, 2, labelAndValues,
							getFieldEditorParent(), true);
					fieldEditor = stringFieldEditor;
				} else if (type instanceof ParameterTypeDirectory) {
					final ParameterTypeDirectory dirType = (ParameterTypeDirectory) type;
					final DirectoryFieldEditor directoryFieldEditor = new DirectoryFieldEditor(
							key, label, getFieldEditorParent());
					directoryFieldEditor.setStringValue(value);
					fieldEditor = directoryFieldEditor;
				} else if (type instanceof ParameterTypeFile) {
					final ParameterTypeFile fileType = (ParameterTypeFile) type;
					final FileFieldEditor fileFieldEditor = new FileFieldEditor(
							key, label, getFieldEditorParent());
					fileFieldEditor.setStringValue(value);
					fieldEditor = fileFieldEditor;
				} else if (type instanceof ParameterTypeBoolean) {
					final ParameterTypeBoolean boolType = (ParameterTypeBoolean) type;
					final BooleanFieldEditor booleanFieldEditor = new BooleanFieldEditor(
							key, label, getFieldEditorParent());
					fieldEditor = booleanFieldEditor;
				} else if (type instanceof ParameterTypeColor) {
					final ParameterTypeColor colorType = (ParameterTypeColor) type;
					final ColorFieldEditor colorFieldEditor = new ColorFieldEditor(
							key, label, getFieldEditorParent());
					fieldEditor = colorFieldEditor;
				} else if (type instanceof ParameterTypeChar) {
					final ParameterTypeChar charType = (ParameterTypeChar) type;
					final StringFieldEditor charFieldEditor = new StringFieldEditor(
							key, label, 1, getFieldEditorParent());
					charFieldEditor.setStringValue(value);
					fieldEditor = charFieldEditor;
				} else {// Not null, probably ParameterTypeString
					final StringFieldEditor stringFieldEditor = new StringFieldEditor(
							key, label, StringFieldEditor.UNLIMITED,
							StringFieldEditor.VALIDATE_ON_FOCUS_LOST,
							getFieldEditorParent());
					if (type instanceof ParameterTypePassword) {
						stringFieldEditor
								.getTextControl(getFieldEditorParent())
								.setEchoChar('*');
					}
					stringFieldEditor.setStringValue(value);
					fieldEditor = stringFieldEditor;
				}
				addField(fieldEditor);
				if (!(fieldEditor instanceof BooleanFieldEditor)) {
					fieldEditor.getLabelControl(getFieldEditorParent())
							.setToolTipText(description);
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean isPredefined(final String name) {
		return name != null && predefinedGroups.contains(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {// Nothing special
	}

}