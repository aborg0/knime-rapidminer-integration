/**
 * 
 */
package com.mind_era.guava.helper.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gábor Bakos
 * 
 */
public class MapHelper {

	/**
	 * 
	 */
	private MapHelper() {
		throw new UnsupportedOperationException();
	}

	public static <K, V> Map<K, V> newHashMap(final Iterable<Map.Entry<K, V>> it) {
		final Map<K, V> ret = new HashMap<K, V>();
		for (final Entry<K, V> entry : it) {
			ret.put(entry.getKey(), entry.getValue());
		}
		return ret;
	}
}
