package giraudsa.marshall.deserialisation;

import utils.champ.FieldInformations;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

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

	protected abstract void construitObjet() throws InstantiationException, IllegalAccessException;


	protected <W> W getObject(String id, Class<W> type) throws InstantiationException, IllegalAccessException{
		return unmarshaller.getObject(id, type);
	}

	protected Object getObjetDejaVu(){
		return obj;
	}

	protected abstract <W> void integreObjet(String nomAttribut, W objet) throws IllegalAccessException, InstantiationException;
	protected abstract void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	protected boolean isUniversalId() {
		return true;
	}

}
