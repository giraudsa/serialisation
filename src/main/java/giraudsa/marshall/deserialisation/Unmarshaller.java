package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.UnmarshalException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Constants;


public abstract class Unmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(Unmarshaller.class);
	private final Fabrique fabrique;
	protected T obj;
	protected final EntityManager entity;
	protected final Deque<ActionAbstrait<?>> pileAction = new ArrayDeque<>();
	protected CacheObject  cacheObject;

	protected Unmarshaller(EntityManager entity) throws ClassNotFoundException, IOException, UnmarshallExeption {
		this.entity = entity;
		fabrique = Fabrique.getInstance();
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
	protected <U> ActionAbstrait getAction(Class<U> type) throws NotImplementedSerializeException, IllegalAccessException  {
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
	

    <W> W getObject(String id, Class<W> type) throws IllegalAccessException, UnmarshallExeption{
		if (id == null) 
			return newInstance(type);
		W objet = cacheObject.getObject(type, id);
		if(objet == null){
			if(entity != null){
				try {
					objet = entity.findObjectOrCreate(id, type,true);
				} catch (InstantiationException e) {
					throw new UnmarshallExeption("problème dans la déserialisation", e);
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
	<W> W newInstance(Class<W> type) throws UnmarshallExeption {
		return fabrique.newObject(type);
	}
	
	protected Object getObjet(ActionAbstrait<?> action) {
		return action.getObjet();
	}

	
	protected <W> void integreObjet(ActionAbstrait<?> action, String nom, W objet) throws IllegalAccessException, UnmarshallExeption {
		action.integreObjet(nom, objet);
	}
	protected void rempliData(ActionAbstrait<?> action, String donnees) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, UnmarshallExeption {
		action.rempliData(donnees);
		
	}
	protected void construitObjet(ActionAbstrait<?> action) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, UnmarshallExeption, InstantiationException, IllegalArgumentException, SecurityException {
		action.construitObjet();
	}
	
	public void dispose() throws IOException {
	}
}
