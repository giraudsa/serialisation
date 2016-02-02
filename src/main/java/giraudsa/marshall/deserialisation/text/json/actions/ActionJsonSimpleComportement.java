package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	
	protected ActionJsonSimpleComportement(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<Object>(Object.class, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonSimpleComportement<U>(type, (JsonUnmarshaller<?>)unmarshaller);
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
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption {
		obj = type.getConstructor(String.class).newInstance(donnees);
	}
	
	@Override
	protected void construitObjet() {
		//rien a faire
	}

}
