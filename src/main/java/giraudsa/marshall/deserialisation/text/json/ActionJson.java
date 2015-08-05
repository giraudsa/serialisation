package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.text.ActionText;

public abstract class ActionJson<T> extends ActionText<T> {

	public ActionJson(Class<T> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, nom, jsonUnmarshaller);
	}

	protected abstract Class<?> getType(String clefEnCours);

}
