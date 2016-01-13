package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	
	protected ActionJsonSimpleComportement(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	public static <U> ActionAbstrait<U> getInstance(Class<U> type, JsonUnmarshaller<?> u) {	
		return new ActionJsonSimpleComportement<>(type, u);
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
		obj = objet;
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		obj = type.getConstructor(String.class).newInstance(donnees);
	}
	
	@Override
	protected void construitObjet() {
		//rien a faire
	}

}
