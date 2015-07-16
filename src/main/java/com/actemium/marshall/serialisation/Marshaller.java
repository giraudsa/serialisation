package com.actemium.marshall.serialisation;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import utils.Constants;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;

public abstract class Marshaller {
	
	protected Writer writer;
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, Class<? extends ActionAbstrait>> behaviors = new HashMap<>();
	protected SetQueue<Object> aSerialiser;

	//////ATTRIBUT
	Set<Object> dejaVu = new HashSet<>();
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> Class<? extends ActionAbstrait> getBehavior(T obj) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException  {
		Class<T> type = null;
		Class<? extends ActionAbstrait> behavior;
		if (obj == null) {
			behavior = behaviors.get(void.class);
		} else {
			type = (Class<T>) obj.getClass();
			behavior =  behaviors.get(type);
			if (behavior == null) {
				Class<?> genericType = type;
				if (type.isEnum())
					genericType = Constants.enumType;
				else if (Constants.dictionaryType.isAssignableFrom(type))
					genericType = Constants.dictionaryType;
				else if (type != Constants.stringType && Constants.collectionType.isAssignableFrom(type))
					genericType = Constants.collectionType;
				else if (type.getPackage() == null || ! type.getPackage().getName().startsWith("System"))
					genericType = Constants.objectType;
				behavior = behaviors.get(genericType);
				behaviors.put(type, behavior); 
				if (behavior == null) {
					throw new NotImplementedSerializeException("not implemented: " + type);
				}
			}
		}
		if (behavior == null) { 
			throw new NotImplementedSerializeException("not implemented: " + type);
		}
		return behavior;
	}
	
	void write(String string) throws IOException {
		writer.write(string);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	<T> void marshall(T value, TypeRelation typeRelation, String nom) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		marshallSpecialise(value, typeRelation, nom);
	}
	
	protected abstract  <T> void marshallSpecialise(T value, TypeRelation typeRelation, String nom) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException;
	
	public class SetQueue<E> implements Queue<E>{
		private Set<E> queue = new LinkedHashSet<>();
		@Override public int size() {return queue.size();}
		@Override public boolean isEmpty() {return queue.isEmpty();}
		@Override public boolean contains(Object o) {return queue.contains(o);}
		@Override public Iterator<E> iterator() {return queue.iterator();}
		@Override public Object[] toArray() {return queue.toArray();}
		@Override public <T> T[] toArray(T[] a) {return queue.toArray(a);}
		@Override public boolean remove(Object o) {return queue.remove(o);}
		@Override public boolean containsAll(Collection<?> c) {return queue.containsAll(c);}
		@Override public boolean addAll(Collection<? extends E> c) {return queue.addAll(c);}
		@Override public boolean removeAll(Collection<?> c) {return queue.removeAll(c);}
		@Override public boolean retainAll(Collection<?> c) {return queue.retainAll(c);}
		@Override public void clear() {queue.clear();}
		@Override public boolean add(E e) {return queue.add(e);}
		@Override public boolean offer(E e) {return queue.add(e);}
		@Override public E remove() {
			if(queue.isEmpty()) throw new NoSuchElementException();
			E e = queue.iterator().next();
			queue.remove(e);
			return e;
		}
		@Override public E poll() {
			if(queue.isEmpty()) return null;
			E e = queue.iterator().next();
			queue.remove(e);
			return e;
		}
		@Override public E element() {
			if(queue.isEmpty()) return null;
			E e = queue.iterator().next();
			return e;
		}
		@Override
		public E peek() {
			if(queue.isEmpty()) throw new NoSuchElementException();
			E e = queue.iterator().next();
			return e;
		}
	}
}
