package giraudsa.marshall.serialisation;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait.Comportement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import utils.Constants;
import utils.champ.FieldInformations;

public abstract class Marshaller {
	
	//////ATTRIBUT
	protected boolean isCompleteSerialisation;
	protected Set<Object> dejaTotalementSerialise = new HashSet<>();
	private Set<Object> dejaVu = new HashSet<>();
	protected Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new HashMap<>();
	@SuppressWarnings("rawtypes")
	protected Deque<Comportement> aFaire = new ArrayDeque<>();

	
	//////Constructeur
	protected Marshaller(boolean isCompleteSerialisation){
		this.isCompleteSerialisation = isCompleteSerialisation;
		initialiseDico();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> ActionAbstrait getAction(T obj) throws NotImplementedSerializeException {
		ActionAbstrait action;
		if (obj == null) {
			action = dicoTypeToAction.get(void.class);
		} else {
			Class<T> type = (Class<T>) obj.getClass();
			action =  dicoTypeToAction.get(type);
			if (action == null) {
				action = choisiAction(type);
			}
		}
		return action;
	}




	@SuppressWarnings("rawtypes")
	private <T> ActionAbstrait choisiAction(Class<T> type) throws NotImplementedSerializeException {
		ActionAbstrait action;
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
		return action;
	}
	
	
	
	
	protected <T> void marshall(T value, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		marshallSpecialise(value, fieldInformations);
	}
	
	protected <T> boolean isDejaVu(T obj){
		return dejaVu.contains(obj);
	}
	
	protected <T> void setDejaVu(T obj){
		dejaVu.add(obj);
	}
	
	protected <T> boolean isDejaTotalementSerialise(T obj){
		return dejaTotalementSerialise.contains(obj);
	}
	
	protected <T> void setDejaTotalementSerialise(T obj){
		dejaTotalementSerialise.add(obj);
	}
	
	protected void initialiseDico(){}
	
	protected void deserialisePile() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption{
		aFaire.pop().evalue();
	}
	
	protected <T> void marshallSpecialise(T value, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException {
		ActionAbstrait<?> action = getAction(value);
		action.marshall(value, fieldInformations);
	}
}
