package utils;

import java.util.HashMap;
import java.util.Map;

public class MapDoubleSens <K, V>{

	private Map<K, V> sens1 = new HashMap<>();
	private Map<V, K> sens2 = new HashMap<>();
	
	public synchronized void put(K key, V value){
		sens1.put(key, value);
		sens2.put(value, key);
	}
	
	public synchronized V get(K key){
		return sens1.get(key);
	}
	
	public synchronized K getReverse(V value){
		return sens2.get(value);
	}
	
	public void clear(){
		sens1.clear();
		sens2.clear();
	}
	public synchronized boolean containsKey(K key){
		return sens1.containsKey(key);
	}
	public synchronized boolean containsValue(V value){
		return sens2.containsKey(value);
	}
}
