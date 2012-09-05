/**
 * 
 */
package com.mind_era.guava.helper.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Maps;

/**
 * Helper methods to access zip, zipWithIndex functions.
 * 
 * @author Gabor Bakos
 */
public class Zip {

	private Zip() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Zips the values with the indices starting from {@code startIndex}.
	 * 
	 * @param <T>
	 *            Type of elements.
	 * @param iter
	 *            The {@link Iterable} of elements.
	 * @param startIndex
	 *            The number of the first element.
	 * @return A new {@link Iterable} with index.
	 */
	public static <T> Iterable<Map.Entry<T, Integer>> zipWithIndex(
			final Iterable<? extends T> iter, final int startIndex) {
		return new Iterable<Map.Entry<T, Integer>>() {
			@Override
			public Iterator<Map.Entry<T, Integer>> iterator() {
				return new Iterator<Map.Entry<T, Integer>>() {
					private AtomicInteger i = new AtomicInteger(startIndex);

					private final Iterator<? extends T> it = iter.iterator();

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Entry<T, Integer> next() {
						final T t = it.next();
						return Maps.immutableEntry(t,
								Integer.valueOf(i.getAndIncrement()));
					}

					@Override
					public void remove() {
						it.remove();
					}

				};
			}

		};
	}

	/**
	 * Zips the values with the indices starting from {@code startIndex}.
	 * 
	 * @param <T>
	 *            Type of elements.
	 * @param list
	 *            The {@link List} of elements.
	 * @param startIndex
	 *            The number of the first element.
	 * @return A new {@link List} with index.
	 */
	public static <T> List<Entry<T, Integer>> zipWithIndexList(
			final Collection<? extends T> list, final int startIndex) {
		final List<Entry<T, Integer>> ret = new ArrayList<Map.Entry<T, Integer>>(
				list.size());
		int i = startIndex;
		for (final T t : list) {
			ret.add(Maps.immutableEntry(t, Integer.valueOf(i++)));
		}
		return ret;
	}
}
