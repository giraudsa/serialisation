package giraudsa.marshall.serialisation;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait.Comportement;
import giraudsa.marshall.strategie.StrategieDeSerialisation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.Constants;
import utils.champ.FieldInformations;

public abstract class Marshaller {
	
	//////ATTRIBUT
	protected int profondeur;
	protected StrategieDeSerialisation strategie;
	protected Set<Object> dejaTotalementSerialise = new HashSet<>();
	private Set<Object> dejaVu = new HashSet<>();
	@SuppressWarnings("rawtypes")
	protected Deque<Comportement> aFaire = new ArrayDeque<>();

	
	//////Constructeur
	protected Marshaller(StrategieDeSerialisation strategie){
		this.strategie = strategie;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> ActionAbstrait getAction(T obj) throws NotImplementedSerializeException {
		Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = getDicoTypeToAction();
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




	protected abstract Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction();

	@SuppressWarnings("rawtypes")
	private <T> ActionAbstrait choisiAction(Class<T> type) throws NotImplementedSerializeException {
		Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = getDicoTypeToAction();
		ActionAbstrait action;
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
		action = dicoTypeToAction.get(genericType);
		dicoTypeToAction.put(type, action); 
		if (action == null) {
			throw new NotImplementedSerializeException("not implemented: " + type);
		}
		return action;
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
		
	protected void deserialisePile() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption{
		aFaire.pop().evalue(this);
	}
	
	protected <T> void marshall(T value, FieldInformations fieldInformations) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption {
		ActionAbstrait<?> action = getAction(value);
		action.marshall(this, value, fieldInformations);
	}

	StrategieDeSerialisation getStrategie() {
		return strategie;
	}

	int getProfondeur() {
		return profondeur;
	}

	void diminueProfondeur() {
		--profondeur;
	}
	
	void augmenteProdondeur(){
		++profondeur;
	}
}
