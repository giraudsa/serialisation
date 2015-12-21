package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public abstract class ActionJsonComplexeObject<T> extends ActionJson<T> {

	protected ActionJsonComplexeObject(Class<T> type, JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//Rien a faire
	}

}
