package utils;

import java.util.concurrent.ConcurrentHashMap;

public class BiHashMap<K1, K2, V> extends ConcurrentHashMap<Pair<K1, K2>, V> {
	private static final long serialVersionUID = 1L;

	public V get(final K1 key1, final K2 key2) {
		return get(key(key1, key2));
	}

	private Pair<K1, K2> key(final K1 key1, final K2 key2) {
		return new Pair<>(key1, key2);
	}

	public void put(final K1 key1, final K2 key2, final V value) {
		put(key(key1, key2), value);
	}

	public void removeObj(final K1 key1, final K2 key2) {
		remove(key(key1, key2));
	}

}
