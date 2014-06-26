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
package com.mind_era.knime_rapidminer.knime.nodes.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * JTextField with right click popup menu for copy/cut/paste.
 * 
 * @author Gabor
 */
public class XJTextField extends JTextField {
	private static final long serialVersionUID = 2943758767801824950L;

	/**
	 * 
	 */
	public XJTextField() {
		this(null, null, 0);
	}

	/**
	 * @param text
	 */
	public XJTextField(final String text) {
		this(null, text, 0);
	}

	/**
	 * @param columns
	 */
	public XJTextField(final int columns) {
		this(null, null, columns);
	}

	/**
	 * @param text
	 * @param columns
	 */
	public XJTextField(final String text, final int columns) {
		this(null, text, columns);
	}

	/**
	 * @param doc
	 * @param text
	 * @param columns
	 */
	public XJTextField(final Document doc, final String text, final int columns) {
		super(doc, text, columns);
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new JMenuItem(new AbstractAction("Copy") {
			private static final long serialVersionUID = 6327532524209962441L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				copy();
			}
		}));
		popupMenu.add(new JMenuItem(new AbstractAction("Cut") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5235151735052208143L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				cut();
			}
		}));
		popupMenu.add(new JMenuItem(new AbstractAction("Paste") {
			private static final long serialVersionUID = 5027179733810671570L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				paste();
			}
		}));
		addMouseListener(new PopupListener(popupMenu));
	}
}
