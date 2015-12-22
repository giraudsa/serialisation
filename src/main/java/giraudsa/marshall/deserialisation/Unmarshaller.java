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


public class Unmarshaller<T> {
	
	protected T obj;
	protected static EntityManager entity;
	
	protected Stack<ActionAbstrait<?>> pileAction = new Stack<>();
	protected ActionAbstrait<?> getActionEnCours(){
		if(pileAction.isEmpty()) return null;
		return pileAction.peek();
	}
	
	protected final Map<String, Object>  dicoIdToObject = new HashMap<>();

	protected Map<Class<?>, ActionAbstrait<?>> actions = new HashMap<>();

	protected Unmarshaller() throws ClassNotFoundException {
	}
	

	protected Unmarshaller(EntityManager entity) throws ClassNotFoundException {
		Unmarshaller.entity = entity;
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
				//TODO ajouter pour les dates
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
	

	
	@SuppressWarnings("unchecked")
    <W> W getObject(String id, Class<W> type, boolean isFake) throws InstantiationException, IllegalAccessException{
		if (id == null) return type.newInstance();
		W objet = (W) dicoIdToObject.get(id);
		if(objet == null){
			if(entity != null && !isFake){
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
			dicoIdToObject.put(id, objet);
		}
		return objet;
	}


	@SuppressWarnings("unchecked")
	private <W> W newInstance(Class<W> type) throws InstantiationException, IllegalAccessException {
		W objet = null;
		try{
			objet = type.newInstance();
		}catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException e){
			try {
				Constructor<?> constr = type.getDeclaredConstructor(Constants.classVide);
				constr.setAccessible(true);
				objet = (W) constr.newInstance(Constants.nullArgument);
			} catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e1) {
				//TODO récupérer le premier constructeur public et mettre des arguments bidons
				e1.printStackTrace();
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
}
