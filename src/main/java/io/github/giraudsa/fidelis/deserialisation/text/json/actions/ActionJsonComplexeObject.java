package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import io.github.giraudsa.fidelis.deserialisation.text.json.ActionJson;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;

public abstract class ActionJsonComplexeObject<T> extends ActionJson<T> {

	protected ActionJsonComplexeObject(final Class<T> type, final JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void rempliData(final String donnees) {
		// Rien a faire
	}

}
