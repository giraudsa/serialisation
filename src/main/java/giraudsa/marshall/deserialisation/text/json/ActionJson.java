package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.text.ActionText;

public abstract class ActionJson<T> extends ActionText<T> {

	protected ActionJson(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type,jsonUnmarshaller);
	}

}
