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

import java.io.Closeable;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.primitives.Doubles;
import com.mind_era.guava.helper.data.ForEach;
import com.mind_era.guava.helper.data.MapHelper;
import com.mind_era.guava.helper.data.Zip;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.SimpleAttributes;
import com.rapidminer.example.set.AbstractExampleSet;
import com.rapidminer.example.table.AbstractExampleTable;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.ListDataRowReader;
import com.rapidminer.example.table.NominalMapping;
import com.rapidminer.example.table.PolynominalMapping;
import com.rapidminer.tools.Ontology;

/**
 * A wrapper around {@link BufferedDataTable} to show as an {@link ExampleSet}.
 * 
 * <br/>
 * Unfortunately still not a viable alternative to memory-copy the data.
 * 
 * @author Gabor Bakos
 */
@Deprecated
public class KnimeExampleSet extends AbstractExampleSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4991615941790203693L;
	private final BufferedDataTable inData;
	private final Map<Integer, Map<String, Double>> mapping;
	private final Attributes attributes;
	private final boolean generateRowIds;
	private final String rowIdColumnName;
	private AbstractExampleTable exampleTable;

	private KnimeExampleSet(final BufferedDataTable inData,
			Map<Integer, Map<String, Double>> map, Attributes attrs,
			boolean generateRowIds, String rowIdColumnName) {
		super();
		this.inData = inData;
		this.generateRowIds = generateRowIds;
		this.rowIdColumnName = rowIdColumnName;
		this.mapping = map;
		this.attributes = attrs;
		exampleTable = initExampleTable();
	}

	/**
	 * Constructor.
	 * 
	 * @param inData
	 *            The {@link BufferedDataTable} to wrap.
	 */
	public KnimeExampleSet(final BufferedDataTable inData,
			boolean generateRowIds, String rowIdColumnName) {
		super();
		this.inData = inData;
		this.generateRowIds = generateRowIds;
		this.rowIdColumnName = rowIdColumnName;
		mapping = createMapping(inData, generateRowIds);
		attributes = createAttributes();
		exampleTable = initExampleTable();
	}

	/**
	 * Constructor
	 * 
	 * @param toCopy
	 *            The {@link KnimeExampleSet} containing the
	 *            {@link BufferedDataTable} to wrap.
	 */
	public KnimeExampleSet(final KnimeExampleSet toCopy) {
		this(toCopy.inData, toCopy.mapping, toCopy.attributes,
				toCopy.generateRowIds, toCopy.rowIdColumnName);
	}

	private static Map<Integer, Map<String, Double>> createMapping(
			final BufferedDataTable inData, boolean generateRowId) {
		final Iterable<Integer> keys = Iterables.concat(
				generateRowId ? Collections.singletonList(Integer.valueOf(0))
						: Collections.<Integer> emptyList(),
				Iterables.transform(Iterables.filter(
				// add index for transform
						Zip.zipWithIndex(inData.getDataTableSpec(), 1),
						// use only the string valued columns
						new Predicate<Map.Entry<DataColumnSpec, Integer>>() {
							@Override
							public boolean apply(
									final Map.Entry<DataColumnSpec, Integer> input) {
								return input
										.getKey()
										.getType()
										.isCompatible(
												org.knime.core.data.StringValue.class);
							}
						}),
				// Project to the index
						new Function<Map.Entry<?, Integer>, Integer>() {
							@Override
							public Integer apply(final Entry<?, Integer> input) {
								return input.getValue();
							}
						}));
		// Initialise helper and result maps
		final TreeMap<Integer, Integer> max = Maps
				.<Integer, Integer> newTreeMap();
		final Builder<Integer, Map<String, Double>> builder = ImmutableMap
				.<Integer, Map<String, Double>> builder();
		for (final Integer key : keys) {
			max.put(key, Integer.valueOf(0));
			builder.put(key, Maps.<String, Double> newHashMap());
		}
		final ImmutableMap<Integer, Map<String, Double>> ret = builder.build();

		// Go through the data
		final CloseableRowIterator it = inData.iterator();
		try {
			ForEach.consume(
			// for (Iterator<Void> consumer =
			// Fill the result map values
			Iterators.transform(it, new Function<DataRow, Void>() {
				@Override
				public Void apply(final DataRow row) {
					EntryTransformer<Integer, Map<String, Double>, Void> m = new Maps.EntryTransformer<Integer, Map<String, Double>, Void>() {
						@Override
						public Void transformEntry(final Integer key,
								final Map<String, Double> value) {
							final String val = getStringValue(row, key);
							if (!value.containsKey(val)) {
								final Integer maxValue = max.get(key);
								value.put(val,
										Double.valueOf(maxValue.doubleValue()));
								max.put(key, Integer.valueOf(maxValue
										.intValue() + 1));
							}
							return null;
						}

						/**
						 * @param row
						 *            The row to get the {@link String} value.
						 * @param key
						 *            The {@code 1}-based index, {@code 0} means
						 *            row id.
						 * @return The {@link String} value at {@code key}
						 *         position, or {@code null}
						 */
						private String getStringValue(final DataRow row,
								final Integer key) {
							int kv = key.intValue();
							if (kv == 0) {
								return row.getKey().getString();
							}
							final DataCell cell = row.getCell(kv - 1);
							final String val = cell.isMissing() ? null
									: ((org.knime.core.data.StringValue) cell)
											.getStringValue();
							return val;
						}
					};
					// Updating max and ret maps.
					Map<Integer, Void> transformedEntries = Maps
							.transformEntries(ret, m);
					ForEach.consume(transformedEntries.entrySet());
					return null;
				}
			}));//; consumer.hasNext(); consumer.next());
			return ret;
		} finally {
			it.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.ExampleSet#getAttributes()
	 */
	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	/**
	 * @return The attributes based on the input data specs.
	 */
	private Attributes createAttributes() {
		Iterable<AttributeRole> attributeRoles = Iterables.transform(
				Iterables.filter(
				// Adding index for later transform
						Zip.zipWithIndex(inData.getDataTableSpec(), generateRowIds ? 1 : 0),
						// Keep only the string or double valued columns
						new Predicate<Map.Entry<DataColumnSpec, Integer>>() {
							@Override
							public boolean apply(
									final Entry<DataColumnSpec, Integer> entry) {
								final DataColumnSpec spec = entry.getKey();
								return spec.getType().isCompatible(
										org.knime.core.data.StringValue.class)
										|| spec.getType().isCompatible(
												DoubleValue.class);
							}
						}),
				// Create the AttributeRole
				new Function<Map.Entry<DataColumnSpec, Integer>, AttributeRole>() {
					@Override
					public AttributeRole apply(
							final Entry<DataColumnSpec, Integer> entry) {
						final AttributeRole role = createAttributeRole(entry);
						return role;
					}

				});
		if (generateRowIds) {
			attributeRoles = Iterables.concat(Collections
					.singletonList(createAttributeRole(Collections
							.singletonMap(
									new DataColumnSpecCreator(rowIdColumnName,
											StringCell.TYPE).createSpec(), 0)
							.entrySet().iterator().next())), attributeRoles);
		}
		final SimpleAttributes ret = new SimpleAttributes();
		for (final AttributeRole attributeRole : attributeRoles) {
			ret.add(attributeRole);
		}
		return ret;
	}

	/**
	 * @param entry
	 * @return A new NominalMapping.
	 */
	private NominalMapping createPolynomialMapping(
			final Entry<DataColumnSpec, Integer> entry) {
		return new PolynominalMapping(
				MapHelper.<Integer, String> newHashMap(Iterables.transform(
						mapping.get(entry.getValue()).entrySet(),
						new Function<Entry<String, ? extends Number>, Entry<Integer, String>>() {
							@Override
							public Entry<Integer, String> apply(
									final Entry<String, ? extends Number> input) {
								return Maps.immutableEntry(Integer
										.valueOf(input.getValue().intValue()),
										input.getKey());
							}
						})));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.ExampleSet#getExample(int)
	 */
	@Override
	public Example getExample(final int n) {
		return Iterators.get(iterator(), n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.ExampleSet#getExampleTable()
	 */
	@Override
	public ExampleTable getExampleTable() {
		return exampleTable;
	}

	/**
	 * @return
	 */
	private AbstractExampleTable initExampleTable() {
		return new AbstractExampleTable(Lists.newLinkedList(getAttributes())) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2509180947314894424L;

			@Override
			public com.rapidminer.example.table.DataRow getDataRow(final int x) {
				throw new UnsupportedOperationException();
				//return getExample(x).getDataRow();
			}

			@Override
			public int size() {
				return KnimeExampleSet.this.size();
			}

			@Override
			public DataRowReader getDataRowReader() {
				return new ListDataRowReader(
						Iterators.transform(
								KnimeExampleSet.this.iterator(),
								new Function<Example, com.rapidminer.example.table.DataRow>() {
									@Override
									public com.rapidminer.example.table.DataRow apply(
											final Example input) {
										return input.getDataRow();
									}
								}));
			};
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.example.ExampleSet#size()
	 */
	@Override
	public int size() {
		return inData.getRowCount();
	}

	/**
	 * Wraps an iterator and closes the {@link CloseableRowIterator} when there
	 * are no more elements.
	 * 
	 * @param <E>
	 *            The type of the iterated elements.
	 */
	private static class ClosableIterator<E> implements Iterator<E>, Closeable {
		private final CloseableRowIterator closeable;
		private final Iterator<E> iterator;

		/**
		 * @param it
		 *            the row iterator.
		 * @param iterator
		 *            the iterator for the elements.
		 */
		ClosableIterator(final CloseableRowIterator it,
				final Iterator<E> iterator) {
			this.closeable = it;
			this.iterator = iterator;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.Closeable#close()
		 */
		@Override
		public void close() throws IOException {
			closeable.close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			final boolean hasNext = iterator.hasNext();
			if (!hasNext) {
				closeable.close();
			}
			return hasNext;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		public E next() {
			return iterator.next();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			iterator.remove();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Example> iterator() {
		final CloseableRowIterator it = inData.iterator();
		return new ClosableIterator<Example>(it, Iterators.transform(it, new Function<DataRow, Example>() {
				@Override
				public Example apply(final DataRow input) {
					return new Example(convertRow(input, mapping,
							generateRowIds), KnimeExampleSet.this);
				}
			}));
	}

	/**
	 * @param entry
	 * @return The new AttributeRole.
	 */
	private AttributeRole createAttributeRole(
			final Entry<DataColumnSpec, Integer> entry) {
		final Attribute attribute = AttributeFactory
				.createAttribute(
						entry.getKey().getName(),
						entry.getKey()
								.getType()
								.isCompatible(
										org.knime.core.data.StringValue.class) ? Ontology.NOMINAL
								: Ontology.NUMERICAL);
		attribute.setTableIndex(entry.getValue().intValue());
		final AttributeRole role = new AttributeRole(attribute);
		// We have to provide mapping
		if (mapping.containsKey(entry.getValue())) {
			role.getAttribute().setMapping(createPolynomialMapping(entry));
		}
		return role;
	}

	/**
	 * Converts a KNIME row to a RapidMiner row.
	 * 
	 * @param input
	 *            A KNIME row.
	 * @param mapping
	 *            The mapping.
	 * @param generateRowIds
	 *            Specify to generate row ids or not.
	 * @return The transformed row.
	 */
	protected static com.rapidminer.example.table.DataRow convertRow(
			final DataRow input,
			final Map<Integer, Map<String, Double>> mapping,
			boolean generateRowIds) {
		return new DoubleArrayDataRow(
				Doubles.toArray(Lists.<Double> newArrayList(Iterables.transform(
						Iterables.concat(
								generateRowIds ? Collections
										.singletonList(new AbstractMap.SimpleEntry<DataCell, Integer>(
												null, Integer.valueOf(0)))
										: Collections
												.<Entry<DataCell, Integer>> emptyList(),
								Iterables.filter(
										Zip.zipWithIndex(input, 1),
										new Predicate<Entry<DataCell, Integer>>() {
											@Override
											public boolean apply(
													final Entry<DataCell, Integer> entry) {
												final DataCell cell = entry
														.getKey();
												return cell instanceof DoubleValue
														|| cell instanceof org.knime.core.data.StringValue
														|| cell.isMissing();
											}
										})),
						new Function<Entry<DataCell, Integer>, Double>() {
							@Override
							public Double apply(
									final Entry<DataCell, Integer> entry) {
								if (entry.getValue().intValue() == 0) {
									return mapping.get(entry.getValue()).get(
											input.getKey().getString());
								}
								final DataCell cell = entry.getKey();
								return cell instanceof DoubleValue ? Double
										.valueOf(((DoubleValue) cell)
												.getDoubleValue())
										: cell.isMissing() ? Double
												.valueOf(Double.NaN)
												: mapping
														.get(entry.getValue())
														.get(((org.knime.core.data.StringValue) cell)
																.getStringValue());
							}
						}))));
	}
}