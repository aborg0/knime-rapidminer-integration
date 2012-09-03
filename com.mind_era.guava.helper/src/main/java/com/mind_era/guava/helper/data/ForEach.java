/**
 * 
 */
package com.mind_era.guava.helper.data;

import java.util.Iterator;

/**
 * Consumes {@link Iterable} or {@link Iterator}.
 * 
 * @author Gábor Bakos
 */
public class ForEach {

	/**
	 * 
	 */
	private ForEach() {
		throw new UnsupportedOperationException();
	}

	public static <T> void consume(final Iterable<T> it) {
		consume(it.iterator());
	}

	public static <T> void consume(final Iterator<T> it) {
		while (it.hasNext()) {
			it.next();
		}
	}
}
