package utils;

import java.util.HashMap;
import java.util.Map;

public class MapDoubleSens<K, V> {

	private final Map<K, V> sens1 = new HashMap<>();
	private final Map<V, K> sens2 = new HashMap<>();

	public void clear() {
		sens1.clear();
		sens2.clear();
	}

	public synchronized boolean containsKey(final K key) {
		return sens1.containsKey(key);
	}

	public synchronized boolean containsValue(final V value) {
		return sens2.containsKey(value);
	}

	public synchronized V get(final K key) {
		return sens1.get(key);
	}

	public synchronized K getReverse(final V value) {
		return sens2.get(value);
	}

	public synchronized void put(final K key, final V value) {
		sens1.put(key, value);
		sens2.put(value, key);
	}
}
