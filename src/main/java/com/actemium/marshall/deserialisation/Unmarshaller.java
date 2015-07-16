package com.actemium.marshall.deserialisation;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.actemium.marshall.deserialisation.xml.ActionXml;
import com.actemium.marshall.deserialisation.xml.XmlUnmarshaller;
import com.actemium.marshall.exception.NotImplementedSerializeException;

import utils.Constants;


public class Unmarshaller<T> {
	protected T obj;
	protected static EntityManager entity;
	
	protected final Reader reader;
	protected final Map<String, Object>  dicoIdToObject = new HashMap<>();

	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, Class<? extends ActionAbstrait>> behaviors = new HashMap<>();
	
	protected Unmarshaller(Reader reader) throws ClassNotFoundException {
		this.reader = reader;
	}
	

	protected Unmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException {
		this.reader = reader;
		Unmarshaller.entity = entity;
	}

	@SuppressWarnings("rawtypes")
	protected <U> Class<? extends ActionAbstrait> getBehavior(Class<U> type) throws NotImplementedSerializeException  {
		Class<? extends ActionAbstrait> behavior = null;
		if (type != null) {
			behavior = behaviors.get(type);
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
		return behavior;
	}
	
	@SuppressWarnings("unchecked")
    <W> W getObject(String id, Class<W> type) throws InstantiationException, IllegalAccessException {
		W obj = (W) dicoIdToObject.get(id);
		if(obj == null){
			if(entity != null)
				obj = entity.findObject(id, type);
			if(obj == null)
				obj = type.newInstance();
			dicoIdToObject.put(id, obj);
		}
		return obj;
	}
	
	protected <U> U getObjet(ActionAbstrait<U> action) {
		return action.getObjet();
	}

	protected String getNom(ActionAbstrait<?> action) {
		return action.getNom();
	}
	
	protected <W> void integreObjet(ActionAbstrait<?> action, String nom, W objet) {
		action.integreObjet(nom, objet);
	}
	protected void rempliData(ActionAbstrait<?> action, String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
		action.rempliData(donnees);
		
	}
	protected void construitObjet(ActionAbstrait<?> action) throws InstantiationException, IllegalAccessException {
		action.construitObjet(this);
	}
	
	public void dispose() throws IOException {
		reader.close();	
	}	
}
