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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.knime.core.node.port.PortObjectSpec;

/**
 * A project loader/saver interface.
 * 
 * @author Gabor
 * @param <ProjectType>
 *            The type of the project.
 */
public interface ProjectHandling<ProjectType> {
	/**
	 * Common implementation base for {@link ProjectHandling}.
	 * 
	 * @author Gabor
	 * @param <ProjectType>
	 *            The type of the project.
	 */
	public static abstract class AbstractProjectHandling<ProjectType>
			implements ProjectHandling<ProjectType> {
		public void saveAs() {
			final JFileChooser chooser = new JFileChooser();
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
			final int returnVal = chooser.showDialog(null, null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					saveAs(chooser.getSelectedFile().getAbsoluteFile());
				} catch (final Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Error saving the file", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		/**
		 * Saves the project to {@code file}.
		 * 
		 * @param file
		 *            The result file, not null.
		 * @throws Exception
		 *             Error during save.
		 */
		protected abstract void saveAs(final File file) throws Exception;
	}

	public void load(ProjectType project);

	public void saveAs();

	public byte[] getContent() throws Exception;

	public void onInputSpecChange(final PortObjectSpec[] specs);
}
