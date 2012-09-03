/**
 * 
 */
package com.mind_era.knime_rapidminer.knime.nodes.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mind_era.guava.helper.data.Zip;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AbstractDataRowReader;
import com.rapidminer.example.table.AbstractExampleTable;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;

/**
 * Converts a KNIME {@link BufferedDataTable} to a RapidMiner
 * {@link ExampleTable}. <br/>
 * Note: The rows do not support adding new columns (not even if you create an
 * {@link ExampleSet} from it), so you have to use {@link MemoryExampleTable}
 * for that purpose, like this:
 * {@code MemoryExampleTable.createCompleteCopy(new KnimeExampleTable(inData[0])).createExampleSet()}
 * .
 * 
 * @author Gabor Bakos
 */
public class KnimeExampleTable extends AbstractExampleTable {
	private static final long serialVersionUID = -5181159369963469224L;
	/**
	 * 
	 */
	private static final DataRowFactory DATA_ROW_FACTORY = new DataRowFactory(
			DataRowFactory.TYPE_DOUBLE_ARRAY, '.');

	private class KnimeDataRowReader extends AbstractDataRowReader {
		private final CloseableRowIterator it;

		/**
		 * @param it
		 */
		KnimeDataRowReader(final CloseableRowIterator it) {
			super(DATA_ROW_FACTORY);
			this.it = it;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		public DataRow next() {
			return createRow(it.next(), getAttributes(), getFactory(),
					withRowIds, rowIdColumnName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#finalize()
		 */
		@Override
		protected void finalize() throws Throwable {
			it.close();
			super.finalize();
		}
	}

	private final KnowsRowCountTable inData;
	private final boolean withRowIds;
	private final String rowIdColumnName;

	/**
	 * @param inData
	 *            The input {@link BufferedDataTable}.
	 * @param rowIdColumnName
	 *            This will be the new column name of the row ids.
	 * @param withRowIds
	 *            If set, the row ids will be added as a new column.
	 */
	public KnimeExampleTable(final KnowsRowCountTable inData,
			final boolean withRowIds, @Nullable final String rowIdColumnName) {
		super(createAttributes(inData.getDataTableSpec(), withRowIds,
				rowIdColumnName));
		this.inData = inData;
		this.withRowIds = withRowIds;
		this.rowIdColumnName = rowIdColumnName;
	}

	/**
	 * @param spec
	 *            A KNIME {@link DataTableSpec}.
	 * @return The {@link Attribute}s for the columns in {@code spec}.
	 */
	@Deprecated
	public static List<Attribute> createAttributes(final DataTableSpec spec) {
		return createAttributes(spec, false, null);
	}

	/**
	 * @param spec
	 *            A KNIME {@link DataTableSpec}.
	 * @param withRowIds
	 *            If {@code true}, the row id will be used as an input column.
	 * @param rowIdColumnName
	 *            This will be the name of the row id input column in
	 *            RapidMiner. {@code null} iff not {@code withRowIds}.
	 * @return The {@link Attribute}s for the columns in {@code spec}.
	 */
	public static List<Attribute> createAttributes(final DataTableSpec spec,
			final boolean withRowIds, final @Nullable String rowIdColumnName) {
		final List<Attribute> ret = new ArrayList<Attribute>(
				spec.getNumColumns());
		if (withRowIds) {
			ret.add(createAttribute(new DataColumnSpecCreator(rowIdColumnName,
					StringCell.TYPE).createSpec()));
		}
		for (final DataColumnSpec columnSpec : spec) {
			ret.add(createAttribute(columnSpec));
		}
		return ret;
	}

	/**
	 * @param columnSpec
	 *            A KNIME {@link DataColumnSpec}.
	 * @return The Attribute belonging to the {@code columnSpec}.
	 */
	private static Attribute createAttribute(final DataColumnSpec columnSpec) {
		int type;
		if (columnSpec.getType().isCompatible(DoubleValue.class)) {
			type = Ontology.NUMERICAL;
		} else if (columnSpec.getType().isCompatible(DateAndTimeValue.class)) {
			type = Ontology.DATE_TIME;
		} else if (columnSpec.getType().isCompatible(StringValue.class)) {
			type = Ontology.POLYNOMINAL;
		} else {
			throw new UnsupportedOperationException(
					"Not supported KNIME type: "
							+ columnSpec.getType().toString());
		}
		return AttributeFactory.createAttribute(columnSpec.getName(), type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.table.ExampleTable#size()
	 */
	@Override
	public int size() {
		return inData.getRowCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.table.ExampleTable#getDataRowReader()
	 */
	@Override
	public DataRowReader getDataRowReader() {
		return new KnimeDataRowReader(inData.iterator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.table.ExampleTable#getDataRow(int)
	 */
	@Override
	public DataRow getDataRow(final int index) {
		final CloseableRowIterator iterator = inData.iterator();
		try {
			if (!iterator.hasNext()) {
				throw new IllegalStateException("No rows!");
			}
			org.knime.core.data.DataRow next = iterator.next();
			if (index == 0) {
				return createRow(next, getAttributes(), DATA_ROW_FACTORY,
						withRowIds, rowIdColumnName);
			}
			for (int i = 1; iterator.hasNext(); ++i, next = iterator.next()) {
				if (i == index) {
					return createRow(next, getAttributes(), DATA_ROW_FACTORY,
							withRowIds, rowIdColumnName);
				}
			}
			throw new IndexOutOfBoundsException("Not enough rows: " + index);
		} finally {
			iterator.close();
		}
	}

	/**
	 * Creates a RapidMiner {@link DataRow} based on the parameters.
	 * 
	 * @param row
	 *            A KNIME {@link org.knime.core.data.DataRow}.
	 * @param attributes
	 *            The RapidMiner {@link Attribute}s.
	 * @param dataRowFactory
	 *            A RapidMiner {@link DataRowFactory}.
	 * @param withRowIds
	 *            If {@code true}, the row id will be used as an input column.
	 * @param rowIdColumnName
	 *            This will be the name of the row id input column in
	 *            RapidMiner. {@code null} iff not {@code withRowIds}.
	 * @return A new RapidMiner {@link DataRow}.
	 */
	private static DataRow createRow(final org.knime.core.data.DataRow row,
			final Attribute[] attributes, final DataRowFactory dataRowFactory,
			final boolean withRowIds, final String rowIdColumnName) {
		return dataRowFactory.create(
				createData(row, withRowIds, rowIdColumnName), attributes);
	}

	/**
	 * Intermediate {@link String} representation of RapidMiner data. Missing
	 * values are represented as {@code null} values.
	 * 
	 * @param row
	 *            A KNIME {@link org.knime.core.data.DataRow}.
	 * @param withRowIds
	 *            If {@code true}, the row id will be used as an input column.
	 * @param rowIdColumnName
	 *            This will be the name of the row id input column in
	 *            RapidMiner. {@code null} iff not {@code withRowIds}.
	 * @return The {@link String} representation of each cell.
	 */
	private static String[] createData(final org.knime.core.data.DataRow row,
			final boolean withRowIds, final String rowIdColumnName) {
		final ArrayList<String> dataList = Lists.newArrayList(Iterables
				.transform(Iterables.filter(Zip.zipWithIndex(row, 0),
						new Predicate<Entry<DataCell, Integer>>() {
							@Override
							public boolean apply(
									final Entry<DataCell, Integer> entry) {
								final DataCell cell = entry.getKey();
								return cell instanceof DoubleValue
										|| cell instanceof org.knime.core.data.StringValue
										|| cell.isMissing();
							}
						}), new Function<Entry<DataCell, Integer>, String>() {
					@Override
					public String apply(final Entry<DataCell, Integer> entry) {
						final DataCell cell = entry.getKey();
						return cell instanceof DoubleValue ? Double
								.toString(((DoubleValue) cell).getDoubleValue())
								: cell.isMissing() ? /*
													 * Double
													 * .toString(Double.NaN)
													 */null : /*
															 * mapping
															 * .get(entry
															 * .getValue())
															 * .get(
															 * ((org.knime.core
															 * .data
															 * .StringValue)
															 * cell)
															 * .getStringValue
															 * ())
															 */
								((org.knime.core.data.StringValue) cell)
										.getStringValue();
					}
				}));
		if (withRowIds) {
			dataList.add(0, row.getKey().getString());
		}
		return dataList.toArray(new String[0]);
	}
}
