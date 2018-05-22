package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonVoid extends ActionJsonSimpleComportement<Void> {

	public static ActionAbstrait<Void> getInstance() {
		return new ActionJsonVoid(Void.class, null);
	}

	private ActionJsonVoid(final Class<Void> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonVoid(Void.class, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Void getObjet() {
		return null;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// rien a faire avec un objet null
	}

	@Override
	protected void rempliData(final String donnees) {
		// rien a faire avec un objet null
	}
}
