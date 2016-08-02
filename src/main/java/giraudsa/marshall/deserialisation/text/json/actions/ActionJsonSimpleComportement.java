package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	
	protected ActionJsonSimpleComportement(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(Object.class, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonSimpleComportement<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}


	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut)) 
			return type;
		return null;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien à faire
	}
	
	@Override
	protected void rempliData(String donnees) throws InstanciationException{
		try {
			obj = type.getConstructor(String.class).newInstance(donnees);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new InstanciationException("impossible de trouver un constructeur avec un string pour le type " + type.getName(), e);
		}
	}
	
	@Override
	protected void construitObjet() {
		//rien a faire
	}

}
