package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Constants;


public abstract class Unmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Unmarshaller.class);
	protected T obj;
	protected final EntityManager entity;
	protected final Stack<ActionAbstrait<?>> pileAction = new Stack<ActionAbstrait<?>>();
	protected CacheObject  cacheObject;

	protected Unmarshaller(EntityManager entity) throws ClassNotFoundException, IOException {
		this.entity = entity;
		cacheObject = new CacheEmpty();
	}
	
	protected void setCache(boolean isIdUniversel) {
		cacheObject = isIdUniversel ? new CacheIdUniversel() : new CacheIdNonUniversel();
	}


	@SuppressWarnings("unchecked")
	protected <U> ActionAbstrait<U> getActionEnCours(){
		if(pileAction.isEmpty())
			return null;
		return (ActionAbstrait<U>) pileAction.peek();
	}


	@SuppressWarnings( { "unchecked", "rawtypes" })
	protected <U> ActionAbstrait getAction(Class<U> type) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException  {
		Map<Class<?>, ActionAbstrait<?>> actions = getdicoTypeToAction();
		ActionAbstrait behavior = null;
		if (type != null) {
			behavior = actions.get(type);
			if (behavior == null) {
				behavior = choseAction(type);
			}	
		}
		return  behavior.getNewInstance(type, this);
	}


	protected abstract Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction();

	private <U> ActionAbstrait<?> choseAction(Class<U> type) throws NotImplementedSerializeException {
		Map<Class<?>, ActionAbstrait<?>> actions = getdicoTypeToAction();
		ActionAbstrait<?> behavior;
		Class<?> genericType = type;
		if (type.isEnum())
			genericType = Constants.enumType;
		else if (Constants.dictionaryType.isAssignableFrom(type))
			genericType = Constants.dictionaryType;
		else if(Constants.dateType.isAssignableFrom(type))
			genericType = Constants.dateType;
		else if (Constants.collectionType.isAssignableFrom(type))
			genericType = Constants.collectionType;
		else if(type.isArray())
			genericType = Constants.arrayType;
		else if(Constants.inetAdress.isAssignableFrom(type))
			genericType = Constants.inetAdress;
		else if(Constants.calendarType.isAssignableFrom(type))
			genericType = Constants.calendarType;
		else if (type.getPackage() == null || ! type.getPackage().getName().startsWith("System"))
			genericType = Constants.objectType;
		behavior = actions.get(genericType);
		actions.put(type, behavior); 
		if (behavior == null) {
			throw new NotImplementedSerializeException("not implemented: " + type);
		}
		return behavior;
	}
	

    <W> W getObject(String id, Class<W> type) throws InstantiationException, IllegalAccessException{
		if (id == null) 
			return type.newInstance();
		W objet = cacheObject.getObject(type, id);
		if(objet == null){
			if(entity != null){
				synchronized (entity) {
					objet = entity.findObject(id, type);
					if(objet == null){
						objet = newInstance(type);
						entity.metEnCache(id, objet);
					}	
				}
			}else{
				objet = newInstance(type);
			}
			if(objet != null)
				cacheObject.addObject(objet, id);
		}
		return objet;
	}

    protected static Class<?> getTypeDepuisNom(String smallNameType) throws ClassNotFoundException {
		return Class.forName(Constants.getNameType(smallNameType));
	}

	@SuppressWarnings("unchecked")
	private static <W> W newInstance(Class<W> type) throws InstantiationException {
		W objet = null;
		try{
			objet = type.newInstance();
		}catch (Exception e){
			LOGGER.debug(e.getMessage(), e);
			try {
				Constructor<?> constr = type.getDeclaredConstructor(Constants.getClassVide());
				constr.setAccessible(true);
				objet = (W) constr.newInstance(Constants.getNullArgument());
			} catch (Exception e1) {
				LOGGER.error(e1.getMessage(), e1);
				throw new InstantiationException("la classe " + type.getName() + "n'a pas pu être instanciée.");
			}
		}
		return objet;
	}
	
	protected Object getObjet(ActionAbstrait<?> action) {
		return action.getObjet();
	}

	
	protected <W> void integreObjet(ActionAbstrait<?> action, String nom, W objet) throws IllegalAccessException, InstantiationException, UnmarshallExeption {
		action.integreObjet(nom, objet);
	}
	protected void rempliData(ActionAbstrait<?> action, String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, UnmarshallExeption {
		action.rempliData(donnees);
		
	}
	protected void construitObjet(ActionAbstrait<?> action) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, UnmarshallExeption {
		action.construitObjet();
	}
	
	public void dispose() throws IOException {
	}
}
