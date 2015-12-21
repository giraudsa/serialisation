package giraudsa.marshall.serialisation;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait.Comportement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import utils.Constants;

public abstract class Marshaller {
	
	//////Constructeur
	public Marshaller(boolean isCompleteSerialisation){
		this.isCompleteSerialisation = isCompleteSerialisation;
		initialiseDico();
	}
	
	protected Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new HashMap<>();
	@SuppressWarnings("rawtypes")
	protected Stack<Comportement> aFaire = new Stack<>();

	//////ATTRIBUT
	public boolean isCompleteSerialisation;
	protected Map<Object, Integer> dejaVu = new HashMap<>();
	protected Set<Object> dejaTotalementSerialise = new HashSet<>();
	protected Map<Class<?>, Integer> dejaVuType = new HashMap<>();
	Integer compteur = 0;
	Integer compteurType = 1;	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> ActionAbstrait getAction(T obj) throws NotImplementedSerializeException {
		Class<T> type = null;
		ActionAbstrait action;
		if (obj == null) {
			action = dicoTypeToAction.get(void.class);
		} else {
			type = (Class<T>) obj.getClass();
			action =  dicoTypeToAction.get(type);
			if (action == null) {
				Class<?> genericType = type;
				if (type.isEnum())
					genericType = Constants.enumType;
				else if (Constants.dictionaryType.isAssignableFrom(type))
					genericType = Constants.dictionaryType;
				else if(Constants.dateType.isAssignableFrom(type))
					genericType = Constants.dateType;
				else if (type != Constants.stringType && Constants.collectionType.isAssignableFrom(type))
					genericType = Constants.collectionType;
				else if (type.getPackage() == null || ! type.getPackage().getName().startsWith("System"))
					genericType = Constants.objectType;
				action = dicoTypeToAction.get(genericType);
				dicoTypeToAction.put(type, action); 
				if (action == null) {
					throw new NotImplementedSerializeException("not implemented: " + type);
				}
			}
		}
		return action;
	}
	
	
	
	
	<T> void marshall(T value, TypeRelation typeRelation, String nom, boolean typeDevinable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		marshallSpecialise(value, typeRelation, nom, typeDevinable);
	}
	
	<T> boolean isDejaVu(T obj){
		return dejaVu.containsKey(obj);
	}
	
	<T> void setDejaVu(T obj){
		if(!isDejaVu(obj)) dejaVu.put(obj, compteur++);
	}
	
	<T> boolean isDejaTotalementSerialise(T obj){
		return dejaTotalementSerialise.contains(obj);
	}
	
	<T> void setDejaTotalementSerialise(T obj){
		dejaTotalementSerialise.add(obj);
	}
	
	protected void initialiseDico(){}
	
	protected void DeserialisePile() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		aFaire.pop().evalue();
	}
	
	protected boolean isDejaVuType(Class<?> typeObj) {
		return dejaVuType.containsKey(typeObj);
	}
	
	<T> void stockDejaVu(T obj, int smallId){
		dejaVu.put(obj, smallId);
	}
	
	int getSmallIdAndStockObj(Object obj){
		if(!isDejaVu(obj)){
			dejaVu.put(obj, compteur++);
		}
		 return dejaVu.get(obj);
	}
	
	
	protected int _getSmallIdAndStockObj(Object o) {
		return getSmallIdAndStockObj(o);
	}
	
	protected int _getSmallIdTypeAndStockType(Class<?> typeObj) {
		if(!isDejaVuType(typeObj)){
			dejaVuType.put(typeObj, compteurType++);
		}
		return dejaVuType.get(typeObj);
	}
	


	protected abstract <T> void marshallSpecialise(T value, TypeRelation typeRelation, String nom, boolean typeDevinable) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException;
	
}
