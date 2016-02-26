package utils;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V>{

	private K key;
	private V value;
	public Pair(Entry<? extends K, ? extends V> entry) {
		key = entry.getKey();
		value = entry.getValue();
	}
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		 if (other == null)
			 return false;
		 if (other == this)
			 return true;
		 if (!(other instanceof Pair))
			 return false;
		 Pair<?,?> otherMyClass = (Pair<?,?>)other;
		 return otherMyClass.key == key && otherMyClass.value == value;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode() + (value == null ? 0 : value.hashCode());
	}
	
	
}
