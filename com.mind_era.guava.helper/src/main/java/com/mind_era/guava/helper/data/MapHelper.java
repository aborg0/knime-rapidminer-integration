/**
 * 
 */
package com.mind_era.guava.helper.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Some helper methods related to {@link Map}s.
 * 
 * @author Gabor Bakos
 */
public class MapHelper {

	/**
	 * Hidden constructor.
	 */
	private MapHelper() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a new hashmap from the entries {@link Iterable}.
	 * 
	 * @param <K>
	 *            Key type
	 * @param <V>
	 *            Value type
	 * @param it
	 *            The {@link Iterable} of entries.
	 * @return The new {@link HashMap}.
	 */
	public static <K, V> Map<K, V> newHashMap(final Iterable<Map.Entry<K, V>> it) {
		final Map<K, V> ret = new HashMap<K, V>();
		for (final Entry<K, V> entry : it) {
			ret.put(entry.getKey(), entry.getValue());
		}
		return ret;
	}
}
