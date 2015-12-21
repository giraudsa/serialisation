package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonVoid extends ActionJsonSimpleComportement<Void> {

	public static ActionAbstrait<?> getInstance(JsonUnmarshaller<?> jsonUnmarshaller){
		return new ActionJsonVoid(Void.class, jsonUnmarshaller);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonVoid(Void.class, (JsonUnmarshaller<?>)unmarshaller);
	}
	private ActionJsonVoid(Class<Void> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	@Override
	protected Void getObjetDejaVu() {
		return null;
	}
}
