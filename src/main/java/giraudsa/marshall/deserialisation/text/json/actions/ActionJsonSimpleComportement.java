package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	public ActionJsonSimpleComportement(Class<T> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, nom, jsonUnmarshaller);
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		return type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = (T) objet;
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		obj = type.getConstructor(String.class).newInstance(donnees);
	}

}
