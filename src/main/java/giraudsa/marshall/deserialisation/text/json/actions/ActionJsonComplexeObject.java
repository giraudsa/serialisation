package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public abstract class ActionJsonComplexeObject<T> extends ActionJson<T> {

	protected ActionJsonComplexeObject(Class<T> type, JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void rempliData(String donnees) {
		//Rien a faire
	}

}
