/* Copyright © 2013 Mind Eratosthenes Kft.
 * Licence: http://knime.org/downloads/full-license
 */
package com.mind_era.knime.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnDomain;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.IntValue;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.renderer.DataValueRendererFamily;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.util.Pair;

/**
 * A {@link DialogComponent} to handle adding a list of pairs as a
 * configuration.
 * 
 * @author Gabor Bakos
 * @param <Left>
 *            The type of the left values.
 * @param <Right>
 *            The type of the right values.
 */
public class DialogComponentPairs<Left extends DataCell, Right extends DataCell>
		extends DialogComponent {

	private static final int LEFT_COL = 0;
	private static final int RIGHT_COL = LEFT_COL + 1;
	private static final int ADD_COL = RIGHT_COL + 1;
	private static final int REMOVE_COL = ADD_COL + 1;
	private static final int UP_COL = REMOVE_COL + 1;
	private static final int DOWN_COL = UP_COL + 1;
	private static final int ENABLE_COL = DOWN_COL + 1;
	private static final int colCount = ENABLE_COL + 1;

	private DefaultTableModel tableModel;
	private final EnumSet<Columns> visibleColumns;
	private boolean hasLeftSuggestions, hasRightSuggestions,
			hasLeftPossibleValues, hasRightPossibleValues;
	private Collection<Left> leftPossibleValues, leftSuggestions;
	private Collection<Right> rightPossibleValues, rightSuggestions;
	private final DefaultComboBoxModel<Left> leftSuggestionsModel,
			leftPossibleValuesModel;
	private final DefaultComboBoxModel<Right> rightSuggestionsModel,
			rightPossibleValuesModel;
	private static final JPanel emptyPanel = new JPanel();

	/**
	 * Optional columns in the {@link DialogComponentPairs}.
	 */
	public enum Columns {
		/** Add new row above */
		Add,
		/** Remove current row */
		Remove,
		/** Move current row up (switch with above) */
		Up,
		/** Move current row down (switch with below) */
		Down,
		/** Enable row in the output values */
		Enable;
	}

	private static abstract class TableCellEditorDelegate implements
			TableCellEditor {
		private final TableCellEditor noHelp;
		private final TableCellEditor hasSuggestions;
		private final TableCellEditor allSpecified;

		private TableCellEditorDelegate(final TableCellEditor noHelp,
				final TableCellEditor hasSuggestions,
				final TableCellEditor allSpecified) {
			this.noHelp = noHelp;
			this.hasSuggestions = hasSuggestions;
			this.allSpecified = allSpecified;
		}

		protected abstract boolean isAllSpecified();

		protected abstract boolean hasSuggestions();

		private TableCellEditor select() {
			return isAllSpecified() ? allSpecified
					: hasSuggestions() ? hasSuggestions : noHelp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		@Override
		public Object getCellEditorValue() {
			return select().getCellEditorValue();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
		 */
		@Override
		public boolean isCellEditable(final EventObject anEvent) {
			return select().isCellEditable(anEvent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
		 */
		@Override
		public boolean shouldSelectCell(final EventObject anEvent) {
			return select().shouldSelectCell(anEvent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#stopCellEditing()
		 */
		@Override
		public boolean stopCellEditing() {
			return select().stopCellEditing();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#cancelCellEditing()
		 */
		@Override
		public void cancelCellEditing() {
			select().cancelCellEditing();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.
		 * CellEditorListener)
		 */
		@Override
		public void addCellEditorListener(final CellEditorListener l) {
			// TODO maybe all?
			select().addCellEditorListener(l);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.CellEditor#removeCellEditorListener(javax.swing.event
		 * .CellEditorListener)
		 */
		@Override
		public void removeCellEditorListener(final CellEditorListener l) {
			// TODO maybe all?
			select().removeCellEditorListener(l);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax
		 * .swing.JTable, java.lang.Object, boolean, int, int)
		 */
		@Override
		public Component getTableCellEditorComponent(final JTable table,
				final Object value, final boolean isSelected, final int row,
				final int column) {
			if (table.getRowCount() - 1 == row) {
				return emptyPanel;
			}
			return select().getTableCellEditorComponent(table, value,
					isSelected, row, column);
		}
	}

	private static class RendererDelegate implements TableCellRenderer {
		private final DataValueRendererFamily delegate;

		/**
		 * @param rendererFamily
		 *            The {@link DataValueRendererFamily} to be wrapped.
		 * 
		 */
		public RendererDelegate(final DataValueRendererFamily rendererFamily) {
			this.delegate = rendererFamily;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent
		 * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		@Override
		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			if (row < table.getRowCount() - 1) {
				return delegate.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}
			return emptyPanel;
		}

	}

	/**
	 * Constructs a {@link DialogComponentPairs}.
	 * 
	 * @param model
	 *            The corresponding {@link SettingsModelPairs} object.
	 * @param leftHeader
	 *            The header text for the left column.
	 * @param rightHeader
	 *            The header text for the right column.
	 * @param visibleColumns
	 *            The visible columns.
	 */
	public DialogComponentPairs(final SettingsModelPairs<Left, Right> model,
			final String leftHeader, final String rightHeader,
			final EnumSet<Columns> visibleColumns) {
		super(model);
		this.visibleColumns = visibleColumns.clone();
//		final JPanel controls = new JPanel();
		final JTable table = new JTable();
		// table.setAutoCreateColumnsFromModel(false);
		table.getTableHeader().setReorderingAllowed(false);
		tableModel = (javax.swing.table.DefaultTableModel) table.getModel();
		tableModel.setColumnIdentifiers(new Object[] { leftHeader, rightHeader, "Add",
				"Del", "Up", "Down", "\u2713" });
		tableModel.setNumRows(1);
		tableModel.setColumnCount(colCount);
//		controls.add(new JScrollPane(table));
		final TableColumnModel colModel = table.getColumnModel();
		final DataValueRendererFamily leftRenderer = model.getLeftType()
				.getRenderer(null);
		colModel.getColumn(LEFT_COL).setCellRenderer(
				new RendererDelegate(leftRenderer));
		final JComboBox<Left> leftSuggestionsBox = new JComboBox<>(
				leftSuggestionsModel = new DefaultComboBoxModel<>());
		leftSuggestionsBox.setEditable(true);
		leftSuggestionsBox.setRenderer(cast(leftRenderer));
		final JComboBox<Left> leftPossibleValuesBox = new JComboBox<>(
				leftPossibleValuesModel = new DefaultComboBoxModel<>());
		leftPossibleValuesBox.setRenderer(cast(leftRenderer));
		colModel.getColumn(LEFT_COL).setCellEditor(
				new TableCellEditorDelegate(new DefaultCellEditor(
						new JTextField()), new DefaultCellEditor(
						leftSuggestionsBox), new DefaultCellEditor(
						leftPossibleValuesBox)) {

					@Override
					protected boolean isAllSpecified() {
						return hasLeftPossibleValues;
					}

					@Override
					protected boolean hasSuggestions() {
						return hasLeftSuggestions;
					}
				});
		final DataValueRendererFamily rightRenderer = model.getRightType()
				.getRenderer(null);
		final JComboBox<Right> rightSuggestionsBox = new JComboBox<>(
				rightSuggestionsModel = new DefaultComboBoxModel<>());
		rightSuggestionsBox.setEditable(true);
		rightSuggestionsBox.setRenderer(cast(rightRenderer));
		final JComboBox<Right> rightPossibleValuesBox = new JComboBox<>(
				rightPossibleValuesModel = new DefaultComboBoxModel<>());
		rightPossibleValuesBox.setRenderer(cast(rightRenderer));
		colModel.getColumn(RIGHT_COL).setCellEditor(
				new TableCellEditorDelegate(new DefaultCellEditor(
						new JTextField()), new DefaultCellEditor(
						rightSuggestionsBox), new DefaultCellEditor(
						rightPossibleValuesBox)) {

					@Override
					protected boolean isAllSpecified() {
						return hasRightPossibleValues;
					}

					@Override
					protected boolean hasSuggestions() {
						return hasRightSuggestions;
					}
				});
		colModel.getColumn(RIGHT_COL).setCellRenderer(
				new RendererDelegate(rightRenderer));
		final int maxColWidth = 44;
		hide(ADD_COL, Columns.Add, colModel, maxColWidth);
		hide(REMOVE_COL, Columns.Remove, colModel, maxColWidth);
		hide(UP_COL, Columns.Up, colModel, maxColWidth);
		hide(DOWN_COL, Columns.Down, colModel, maxColWidth);
		hide(ENABLE_COL, Columns.Enable, colModel, maxColWidth);
		for (int i = RIGHT_COL + 1; i < colCount; ++i) {
			colModel.getColumn(i).setModelIndex(i);
		}
//		controls.setName("controls");
//		final javax.swing.JScrollPane pane = new javax.swing.JScrollPane(
//				controls);
//		pane.setPreferredSize(new java.awt.Dimension(500, 300));
		final javax.swing.Action addAction = new javax.swing.AbstractAction("+") {
			private static final long serialVersionUID = -6930940431718125770L;

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event) {
				tableModel.insertRow(
						Integer.parseInt(event.getActionCommand()),
						new Object[] { null, null, "+", "-", "^", "v",
								!visibleColumns.contains(Columns.Enable) });
			}
		};
		tableModel.setValueAt("+", 0, ADD_COL);

		ButtonColumn.install(table, addAction, ADD_COL);
		final Action toggleEnableAction = new AbstractAction("toggleEnable") {
			private static final long serialVersionUID = 6573077582975369749L;
			private static final int column = ENABLE_COL;

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent event) {
				final int row = Integer.parseInt(event.getActionCommand());
				final Object value = tableModel.getValueAt(row, column);
				if (value instanceof Boolean) {
					@SuppressWarnings("hiding")
					final Boolean enabled = (Boolean) value;
					tableModel.setValueAt(
							Boolean.valueOf(!enabled.booleanValue()), row,
							column);
				}
			}
		};
		ButtonColumn.installCheckBox(table, toggleEnableAction, ENABLE_COL);

		ButtonColumn.install(table, new AbstractAction("-") {
			private static final long serialVersionUID = -7892386498201126105L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				tableModel.removeRow(Integer.parseInt(e.getActionCommand()));
			}
		}, REMOVE_COL);
		ButtonColumn.install(table, new AbstractAction("^") {
			private static final long serialVersionUID = 386788743311420037L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int row = Integer.parseInt(e.getActionCommand());
				final Vector vector = tableModel.getDataVector();
				if (row > 0) {
					final Object r = vector.remove(row);
					vector.insertElementAt(r, row - 1);
					tableModel.fireTableDataChanged();
				}
			}
		}, UP_COL);
		ButtonColumn.install(table, new AbstractAction("v") {
			private static final long serialVersionUID = -9202428866774266726L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int row = Integer.parseInt(e.getActionCommand());
				final Vector vector = tableModel.getDataVector();
				if (row < tableModel.getRowCount() - 2) {
					final Object r = vector.remove(row + 1);
					vector.insertElementAt(r, row);
					tableModel.fireTableDataChanged();
				}
			}
		}, DOWN_COL);
//		controls.setPreferredSize(new Dimension(500, 300));
		// TODO add button to remove unused (not enabled) pairs
		getComponentPanel().add(/*controls*/new JScrollPane(table));
		// http://blog.eclipse-tips.com/2008/02/eclipse-icons.html
		// final Image addIcon = PlatformUI.getWorkbench().getSharedImages()
		// .getImage(ISharedImages.IMG_OBJ_ADD);

	}
	
	/**
	 * Sets the preferred size of the inner scrollpane and the component.
	 * 
	 * @param width The new width.
	 * @param height The new height.
	 */
	public void setPreferredSize(final int width, final int height) {
		final Dimension dim = new Dimension(width, height);
		getComponentPanel().setPreferredSize(dim);
		final Component component = getComponentPanel().getComponent(0);
		if (component instanceof JScrollPane) {
			final JScrollPane pane = (JScrollPane) component;
			pane.setPreferredSize(dim);
			pane.getViewport().setPreferredSize(dim);
		}
	}

	/**
	 * @param renderer
	 *            A {@link ListCellRenderer}
	 * @return The same instance casted to the proper type.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> ListCellRenderer<T> cast(final ListCellRenderer renderer) {
		return renderer;
	}

	/**
	 * Auto-hides a column based on {@link #visibleColumns} and the
	 * {@code colKey}.
	 * 
	 * @param colIndex
	 *            The index of column.
	 * @param colKey
	 *            The {@link Columns} value which represents this column in
	 *            {@link #visibleColumns}.
	 * @param colModel
	 *            The {@link TableColumnModel}.
	 * @param maxColWidth
	 *            The maximum width when visible.
	 */
	protected void hide(final int colIndex, final Columns colKey,
			final TableColumnModel colModel, final int maxColWidth) {
		colModel.getColumn(colIndex).setMaxWidth(
				this.visibleColumns.contains(colKey) ? maxColWidth : 0);
		colModel.getColumn(colIndex).setMinWidth(
				this.visibleColumns.contains(colKey) ? maxColWidth : 0);
		colModel.getColumn(colIndex).setPreferredWidth(
				this.visibleColumns.contains(colKey) ? maxColWidth : 0);
		colModel.getColumn(colIndex).setWidth(
				this.visibleColumns.contains(colKey) ? maxColWidth : 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#updateComponent()
	 */
	@Override
	protected void updateComponent() {
		final SettingsModelPairs<?, ?> model = (SettingsModelPairs<?, ?>) getModel();
		int i = 0;
		tableModel.setRowCount(0);
		tableModel.setRowCount(1);
		tableModel.setValueAt("+", 0, ADD_COL);
		for (final Pair<?, ?> pair : model.getValues()) {
			tableModel.insertRow(i, new Object[] { null, null, "+", "-", "^",
					"v", "" });
			if (pair.getFirst() instanceof StringCell) {
				final StringCell left = (StringCell) pair.getFirst();
				tableModel.setValueAt(left, i, LEFT_COL);

			} else if (pair.getFirst() instanceof String) {
				final String left = (String) pair.getFirst();
				tableModel.setValueAt(new StringCell(left), i, LEFT_COL);
			}
			if (pair.getSecond() instanceof StringCell) {
				final StringCell right = (StringCell) pair.getSecond();
				tableModel.setValueAt(right, i, RIGHT_COL);

			} else if (pair.getSecond() instanceof String) {
				final String right = (String) pair.getSecond();
				tableModel.setValueAt(right, i, RIGHT_COL);
			}
			tableModel.setValueAt(model.getEnabledRows().get(i), i, ENABLE_COL);
			++i;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.DialogComponent#
	 * validateSettingsBeforeSave()
	 */
	@Override
	protected void validateSettingsBeforeSave() {
		updateModel();
	}

	/**
	 * Updates the {@link #getModel()}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void updateModel() {
		final int rowCount = tableModel.getRowCount() - 1;
		final BitSet enabledRows = new BitSet();
		final List<Pair<Left, Right>> values = new ArrayList<>(rowCount);
		for (int i = 0; i < rowCount; ++i) {
			enabledRows.set(i, (Boolean) tableModel.getValueAt(i, 6));
			final Object leftVal = tableModel.getValueAt(i, 0);
			final Object rightVal = tableModel.getValueAt(i, 1);
			final StringCell left = convert(leftVal), right = convert(rightVal);
			final Pair pair = new Pair(left, right);
			values.add(pair);
		}
		final SettingsModelPairs<Left, Right> model = (SettingsModelPairs<Left, Right>) getModel();
		model.setValues(values);
		model.setEnabledRows(enabledRows);
	}

	/**
	 * Converts a value to {@link StringCell}.
	 * 
	 * @param val
	 *            An {@link Object}.
	 * @return {@code val} as a {@link StringCell}, or {@code null}, if it was
	 *         {@code null}.
	 */
	private static StringCell convert(final Object val) {
		if (val instanceof String) {
			return new StringCell((String) val);
		}
		if (val instanceof StringCell) {
			return (StringCell) val;
		}
		if (val == null) {
			return null;
		}
		return new StringCell(val.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.knime.core.node.defaultnodesettings.DialogComponent#
	 * checkConfigurabilityBeforeLoad(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs) {
		hasLeftPossibleValues = isEnumerable(specs, true);
		hasRightPossibleValues = isEnumerable(specs, false);
		hasLeftSuggestions = hasSuggestions(specs, true);
		hasRightSuggestions = hasSuggestions(specs, false);
		if (hasLeftPossibleValues) {
			leftSuggestions = leftPossibleValues = leftPossibleValues(specs);
			fillModel(leftPossibleValuesModel, leftPossibleValues);
		} else if (hasLeftSuggestions) {
			leftPossibleValues = null;
			leftSuggestions = leftSuggestions(specs);
			fillModel(leftSuggestionsModel, leftSuggestions);
		} else {
			leftSuggestions = leftPossibleValues = null;
		}
		if (hasRightPossibleValues) {
			rightSuggestions = rightPossibleValues = rightPossibleValues(specs);
			fillModel(rightPossibleValuesModel, rightPossibleValues);
		} else if (hasRightSuggestions) {
			rightPossibleValues = null;
			rightSuggestions = rightSuggestions(specs);
			fillModel(rightSuggestionsModel, rightSuggestions);
		} else {
			rightPossibleValues = rightSuggestions = null;
		}
	}

	/**
	 * Sets the values from {@code values} to the elements of {@code model}.
	 * 
	 * @param model
	 *            A {@link DefaultComboBoxModel}.
	 * @param values
	 *            The new values.
	 */
	protected <T> void fillModel(final DefaultComboBoxModel<T> model,
			final Collection<T> values) {
		model.removeAllElements();
		for (final T t : values) {
			model.addElement(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#setEnabledComponents
	 * (boolean)
	 */
	@Override
	protected void setEnabledComponents(final boolean enabled) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.knime.core.node.defaultnodesettings.DialogComponent#setToolTipText
	 * (java.lang.String)
	 */
	@Override
	public void setToolTipText(final String text) {
		// TODO Auto-generated method stub

	}

	/**
	 * Checks whether all values can be computed for the specified side.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @param leftColumn
	 *            The left {@code true} or the right {@code false} side do we
	 *            refer?
	 * @return {@code true} iff we can provide
	 *         {@link #leftPossibleValues(PortObjectSpec[])} or
	 *         {@link #rightPossibleValues(PortObjectSpec[])} according to
	 *         {@code leftColumn}.
	 */
	protected boolean isEnumerable(@SuppressWarnings("unused") final PortObjectSpec[] specs,
			@SuppressWarnings("unused") final boolean leftColumn) {
		return false;
	}

	/**
	 * Checks whether we can compute suggestions for the specified side.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @param leftColumn
	 *            The left {@code true} or the right {@code false} side do we
	 *            refer?
	 * @return {@code true} iff we can provide
	 *         {@link #leftSuggestions(PortObjectSpec[])} or
	 *         {@link #rightSuggestions(PortObjectSpec[])} according to
	 *         {@code leftColumn}.
	 */
	protected boolean hasSuggestions(@SuppressWarnings("unused") final PortObjectSpec[] specs,
			@SuppressWarnings("unused") final boolean leftColumn) {
		return false;
	}

	/**
	 * Intended to be overridden, default implementation is empty list.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @return The possible values for the left column, although this is not
	 *         necessarily exhaustive.
	 */
	protected Collection<Left> leftSuggestions(@SuppressWarnings("unused") final PortObjectSpec[] specs) {
		return Collections.emptyList();
	}

	/**
	 * Intended to be overridden, default implementation is empty list.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @return The possible values for the right column, although this is not
	 *         necessarily exhaustive.
	 */
	protected Collection<Right> rightSuggestions(@SuppressWarnings("unused") final PortObjectSpec[] specs) {
		return Collections.emptyList();
	}

	/**
	 * Intended to be overridden, default implementation is empty list.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @return All of the possible values for the left column.
	 */
	protected Collection<Left> leftPossibleValues(@SuppressWarnings("unused") final PortObjectSpec[] specs) {
		return Collections.emptyList();
	}

	/**
	 * Intended to be overridden, default implementation is empty list.
	 * 
	 * @param specs
	 *            The {@link PortObjectSpec}s.
	 * @return All of the possible values for the right column.
	 */
	protected Collection<Right> rightPossibleValues(@SuppressWarnings("unused") final PortObjectSpec[] specs) {
		return Collections.emptyList();
	}

	// A few helper methods.

	/**
	 * Collects the column names from the specified {@link DataTableSpec}.
	 * 
	 * @param specs
	 *            Some {@link PortObjectSpec}s, the {@code index}th value should
	 *            be a {@link DataTableSpec}.
	 * @param index
	 *            A non-negative integer to select a {@link DataTableSpec} from
	 *            {@code specs}.
	 * @return The column names as {@link StringCell}s from the selected table
	 *         spec.
	 */
	protected Collection<StringCell> columnsFromSpec(
			final PortObjectSpec[] specs, final int index) {
		if (specs == null || specs[index] == null) {
			return Collections.emptyList();
		}
		final DataTableSpec spec = (DataTableSpec) specs[index];
		final Collection<StringCell> ret = new ArrayList<>(spec.getNumColumns());
		for (final DataColumnSpec colSpec : spec) {
			ret.add(new StringCell(colSpec.getName()));
		}
		return ret;
	}

	/**
	 * @return The flow variable names as {@link StringCell}s.
	 * @see #getAvailableFlowVariable()
	 */
	protected Collection<StringCell> flowVariableNames() {
		final Map<String, FlowVariable> map = getAvailableFlowVariable();
		final Collection<StringCell> ret = new ArrayList<>(map.size());
		for (final String key : map.keySet()) {
			ret.add(new StringCell(key));
		}
		return ret;
	}

	/**
	 * Collects the domain values of a column as a {@link Collection} of
	 * {@link StringCell}s.
	 * 
	 * @param specs
	 *            Some {@link PortObjectSpec}s, the {@code portIndex}th value
	 *            should be a {@link DataTableSpec}.
	 * @param portIndex
	 *            A non-negative integer to select a {@link DataTableSpec} from
	 *            {@code specs}.
	 * @param columnIndex
	 *            The position of column within the {@link DataTableSpec}
	 *            (non-negative).
	 * @return The possible values from the column domain as {@link StringCell}
	 *         s.
	 */
	protected Collection<StringCell> domainValues(final PortObjectSpec[] specs,
			final int portIndex, final int columnIndex) {
		final DataTableSpec tableSpec = (DataTableSpec) specs[portIndex];
		final DataColumnSpec colSpec = tableSpec.getColumnSpec(columnIndex);
		final DataColumnDomain domain = colSpec.getDomain();
		final Collection<StringCell> ret = new LinkedHashSet<>();
		for (final DataCell value : domain.getValues()) {
			if (value instanceof StringCell) {
				final StringCell strCell = (StringCell) value;
				ret.add(strCell);
			} else if (value != null) {
				ret.add(new StringCell(value.toString()));
			}
		}
		if (colSpec.getType().getPreferredValueClass() == IntValue.class) {
			final DataCell lowerCell = domain.getLowerBound();
			final DataCell upperCell = domain.getUpperBound();
			if (!lowerCell.isMissing() && !upperCell.isMissing()) {
				if (lowerCell instanceof IntValue) {
					final IntValue lowerValue = (IntValue) lowerCell;
					if (upperCell instanceof IntValue) {
						final IntValue upperValue = (IntValue) upperCell;
						if (upperValue.getIntValue() - lowerValue.getIntValue() < 60) {
							final int max = Math.min(upperValue.getIntValue(),
									Integer.MAX_VALUE - 1);
							for (int i = lowerValue.getIntValue(); i <= max; ++i) {
								ret.add(new StringCell(Integer.toString(i)));
							}
							if (upperValue.getIntValue() == Integer.MAX_VALUE) {
								ret.add(new StringCell(Integer
										.toString(Integer.MAX_VALUE)));
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Please implement if you want to use {@link #flowVariableNames()}
	 * 
	 * @return The available flow variables.
	 * @see #flowVariableNames()
	 * @see DefaultNodeSettingsPane#getAvailableFlowVariables()
	 */
	protected Map<String, FlowVariable> getAvailableFlowVariable() {
		throw new UnsupportedOperationException("Please implement this method!");
	}
}
