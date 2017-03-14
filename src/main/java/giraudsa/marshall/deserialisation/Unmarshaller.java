package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import utils.Constants;
import utils.EntityManager;


public abstract class Unmarshaller<T> {
	private final Fabrique fabrique;
	private final Map<Object, UUID> dicoObjToFakeId = new HashMap<>();
	protected T obj;
	protected final EntityManager entity;
	protected final Deque<ActionAbstrait<?>> pileAction = new ArrayDeque<>();
	protected CacheObject  cacheObject;

	protected Unmarshaller(EntityManager entity) throws FabriqueInstantiationException {
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
	protected <U> ActionAbstrait getAction(Class<U> type) throws NotImplementedSerializeException  {
		Map<Class<?>, ActionAbstrait<?>> actions = getdicoTypeToAction();
		ActionAbstrait behavior = null;
		if (type != null) {
			behavior = actions.get(type);
			if (behavior == null) {
				behavior = choseAction(type);
			}
			return  behavior.getNewInstance(type, this);
		}
		return null;
	}


	protected abstract Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction();

	protected Map<Object, UUID> getDicoObjToFakeId() {
		return dicoObjToFakeId;
	}
	
	protected EntityManager getEntityManager() {
		return entity;
	}

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
	

    <W> W getObject(String id, Class<W> type) throws EntityManagerImplementationException, InstanciationException{
		if (id == null){
			W ret = newInstance(type);
			if((type != void.class || type != Void.class)  && ret == null){
				throw new EntityManagerImplementationException("Le contrat d'interface n'est pas remplie, l'objet récupéré est null pour le type " + type, new NullPointerException());
			}
			return ret;
		}
		W objet = cacheObject.getObject(type, id);
		if(objet == null){
			if(entity != null){
				objet = entity.findObjectOrCreate(id, type);
				if((type != void.class || type != Void.class)  && objet == null){
					throw new EntityManagerImplementationException("Le contrat d'interface n'est pas remplie, l'objet récupéré est null pour le type " + type, new NullPointerException());
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

	<W> W newInstance(Class<W> type) throws InstanciationException {
		return fabrique.newObject(type);
	}
	
	protected <W> W  createInstance(Class<W> type) throws InstanciationException{
		return fabrique.newObject(type);
	}
	
	protected Object getObjet(ActionAbstrait<?> action) {
		return action.getObjet();
	}

	
	protected <W> void integreObjet(ActionAbstrait<?> action, String nom, W objet) throws IllegalAccessException, EntityManagerImplementationException, InstanciationException, SetValueException{
		action.integreObjet(nom, objet);
	}
	protected void rempliData(ActionAbstrait<?> action, String donnees) throws InstanciationException{
		action.rempliData(donnees);
		
	}
	protected void construitObjet(ActionAbstrait<?> action) throws EntityManagerImplementationException, InstanciationException, SetValueException{
		action.construitObjet();
	}
	
	public void dispose() throws IOException {
	}
}
