package giraudsa.marshall.deserialisation;

import utils.champ.FieldInformations;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import giraudsa.marshall.exception.UnmarshallExeption;

public abstract class ActionAbstrait<T> {

	protected Class<T> type;
	protected Object obj;
	protected Unmarshaller<?> unmarshaller;
	protected FieldInformations fieldInformations;

	protected ActionAbstrait(Class<T> type, Unmarshaller<?> unmarshaller){
		this.type = type;
		this.unmarshaller = unmarshaller;
	}

	@SuppressWarnings("rawtypes")
	public abstract <U extends T>  ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller);

	protected abstract void construitObjet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption, InstantiationException, IllegalArgumentException, SecurityException;


	protected <W> W getObject(String id, Class<W> type) throws IllegalAccessException, UnmarshallExeption{
		return unmarshaller.getObject(id, type);
	}
	
	protected T newInstanceOfType() throws UnmarshallExeption{
		return unmarshaller.newInstance(type);
	}

	protected Object getObjet(){
		return obj;
	}

	protected abstract <W> void integreObjet(String nomAttribut, W objet) throws IllegalAccessException, UnmarshallExeption;
	protected abstract void rempliData(String donnees) throws ParseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption;

	protected boolean isUniversalId() {
		return true;
	}

}
