package giraudsa.marshall.deserialisation;

import utils.champ.FieldInformations;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;
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

	protected abstract void construitObjet() throws EntityManagerImplementationException, InstanciationException, SetValueException;


	protected <W> W getObject(String id, Class<W> type) throws EntityManagerImplementationException, InstanciationException{
		return unmarshaller.getObject(id, type);
	}
	
	protected T newInstanceOfType() throws InstanciationException{
		return unmarshaller.newInstance(type);
	}

	protected Object getObjet(){
		return obj;
	}

	protected abstract <W> void integreObjet(String nomAttribut, W objet) throws EntityManagerImplementationException, InstanciationException, IllegalAccessException, SetValueException;
	protected abstract void rempliData(String donnees) throws InstanciationException;

	protected boolean isUniversalId() {
		return true;
	}
	
	protected Map<Object, UUID> getDicoObjToFakeId() {
		return unmarshaller.getDicoObjToFakeId();
	}

}
