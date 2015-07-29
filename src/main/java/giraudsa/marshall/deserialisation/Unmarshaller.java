package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import utils.Constants;


public class Unmarshaller<T> {
	
	protected T obj;
	protected static EntityManager entity;
	
	protected final Map<String, Object>  dicoIdToObject = new HashMap<>();

	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, Class<? extends ActionAbstrait>> typesAction = new HashMap<>();
	protected Map<Class<?>, ActionAbstrait<?>> actions = new HashMap<>();
	
	protected Unmarshaller() throws ClassNotFoundException {
	}
	

	protected Unmarshaller(EntityManager entity) throws ClassNotFoundException {
		Unmarshaller.entity = entity;
	}

	@SuppressWarnings("rawtypes")
	protected <U> Class<? extends ActionAbstrait> getTypeAction(Class<U> type) throws NotImplementedSerializeException  {
		Class<? extends ActionAbstrait> behavior = null;
		if (type != null) {
			behavior = typesAction.get(type);
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
				behavior = typesAction.get(genericType);
				typesAction.put(type, behavior); 
				if (behavior == null) {
					throw new NotImplementedSerializeException("not implemented: " + type);
				}
			}	
		}
		return behavior;
	}
	
	
	protected <U> ActionAbstrait<?> getAction(Class<U> type) throws NotImplementedSerializeException  {
		ActionAbstrait<?> action = null;
		if (type != null) {
			action = actions.get(type);
			if (action == null) {
				Class<?> genericType = type;
				if (type.isEnum())
					genericType = Constants.enumType;
				else if (Constants.dictionaryType.isAssignableFrom(type))
					genericType = Constants.dictionaryType;
				else if (type != Constants.stringType && Constants.collectionType.isAssignableFrom(type))
					genericType = Constants.collectionType;
				else if (type.getPackage() == null || ! type.getPackage().getName().startsWith("System"))
					genericType = Constants.objectType;
				action = actions.get(genericType);
				actions.put(type, action); 
				if (action == null) {
					throw new NotImplementedSerializeException("not implemented: " + type);
				}
			}	
		}
		return action;
	}
	
	@SuppressWarnings("unchecked")
    <W> W getObject(String id, Class<W> type, boolean isFake) throws InstantiationException, IllegalAccessException{
		if (id == null) return type.newInstance();
		W obj = (W) dicoIdToObject.get(id);
		if(obj == null){
			if(entity != null && !isFake)
				obj = entity.findObject(id, type);
			if(obj == null){
				try{
					obj = type.newInstance();
				}catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException e){
					try {
						Constructor<?> constr = type.getDeclaredConstructor(Constants.classVide);
						constr.setAccessible(true);
						obj = (W) constr.newInstance(Constants.nullArgument);
					} catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e1) {
						//TODO récupérer le premier constructeur public et mettre des arguments bidons
						e1.printStackTrace();
					}
				}
			}
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
	}	
}
