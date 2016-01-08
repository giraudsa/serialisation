package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import utils.Constants;


public abstract class Unmarshaller<T> {
	
	protected T obj;
	protected static EntityManager entity;
	
	protected Stack<ActionAbstrait<?>> pileAction = new Stack<>();
	protected ActionAbstrait<?> getActionEnCours(){
		if(pileAction.isEmpty()) return null;
		return pileAction.peek();
	}
	
	protected final CacheObject  cacheObject;
	
	protected Map<Class<?>, ActionAbstrait<?>> actions = new HashMap<>();


	protected Unmarshaller(EntityManager entity, boolean isUniversalId) throws ClassNotFoundException, IOException {
		Unmarshaller.entity = entity;
		cacheObject = isUniversalId ? new CacheIdUniversel() : new CacheIdNonUniversel();
		initialiseActions();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <U> ActionAbstrait getAction(Class<U> type) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException  {
		ActionAbstrait behavior = null;
		if (type != null) {
			behavior = actions.get(type);
			if (behavior == null) {
				Class<?> genericType = type;
				if (type.isEnum())
					genericType = Constants.enumType;
				else if(Constants.dateType.isAssignableFrom(type))
					genericType = Constants.dateType;
				else if (Constants.dictionaryType.isAssignableFrom(type))
					genericType = Constants.dictionaryType;
				else if (type != Constants.stringType && Constants.collectionType.isAssignableFrom(type))
					genericType = Constants.collectionType;
				else if (type.getPackage() == null || ! type.getPackage().getName().startsWith("System"))
					genericType = Constants.objectType;
				behavior = actions.get(genericType);
				actions.put(type, behavior); 
				if (behavior == null) {
					throw new NotImplementedSerializeException("not implemented: " + type);
				}
			}	
		}
		return  behavior.getNewInstance(type, this);
	}
	

    <W> W getObject(String id, Class<W> type) throws InstantiationException, IllegalAccessException{
		if (id == null) return type.newInstance();
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


	@SuppressWarnings("unchecked")
	private <W> W newInstance(Class<W> type) {
		W objet = null;
		try{
			objet = type.newInstance();
		}catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException e){
			try {
				//System.out.println("la plan A n'a pas fonctionné pour " + type.toString() + ", on passe au plan B !");
				Constructor<?> constr = type.getDeclaredConstructor(Constants.classVide);
				constr.setAccessible(true);
				objet = (W) constr.newInstance(Constants.nullArgument);
			} catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException e1) {
				//System.out.println("pas de création d'instance possible meme avec le plan B pour " + type.toString());
			}
		}
		return objet;
	}
	
	protected Object getObjet(ActionAbstrait<?> action) {
		return action.getObjetDejaVu();
	}

	
	protected <W> void integreObjet(ActionAbstrait<?> action, String nom, W objet) {
		action.integreObjet(nom, objet);
	}
	protected void rempliData(ActionAbstrait<?> action, String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
		action.rempliData(donnees);
		
	}
	protected void construitObjet(ActionAbstrait<?> action) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		action.construitObjet();
	}
	
	public void dispose() throws IOException {
	}


	protected abstract void initialiseActions() throws IOException;
}
