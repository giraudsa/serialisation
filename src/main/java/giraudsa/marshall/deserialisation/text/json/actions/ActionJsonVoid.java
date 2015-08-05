package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonVoid<T> extends ActionJsonSimpleComportement<T> {

	public ActionJsonVoid(Class<T> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, nom, jsonUnmarshaller);
	}
	
	@Override
	protected T getObjet() {
		return null;
	}
}
