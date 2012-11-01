/**
 * 
 */
package com.mind_era.guava.helper.data;

import java.util.Iterator;

/**
 * Consumes {@link Iterable} or {@link Iterator}.
 * 
 * @author Gabor Bakos
 */
public class ForEach {

	/**
	 * Hidden constructor.
	 */
	private ForEach() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Goes through the whole {@link Iterable} for the side effects.
	 * 
	 * @param <T>
	 *            Type of elements.
	 * @param it
	 *            An {@link Iterable}.
	 */
	public static <T> void consume(final Iterable<T> it) {
		consume(it.iterator());
	}

	/**
	 * Goes through the whole {@link Iterator} for the side effects.
	 * 
	 * @param <T>
	 *            Type of elements.
	 * @param it
	 *            An {@link Iterator}.
	 */
	public static <T> void consume(final Iterator<T> it) {
		while (it.hasNext()) {
			T t = it.next();
			if (t != null) {
				t.toString();
			}
		}
	}
}
