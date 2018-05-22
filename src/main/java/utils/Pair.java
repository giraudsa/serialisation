package utils;

import java.util.AbstractMap;
import java.util.Map.Entry;

public class Pair<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> {
	private static final long serialVersionUID = 1L;

	public Pair(final Entry<? extends K, ? extends V> entry) {
		super(entry);
	}

	public Pair(final K key, final V value) {
		super(key, value);
	}
}
