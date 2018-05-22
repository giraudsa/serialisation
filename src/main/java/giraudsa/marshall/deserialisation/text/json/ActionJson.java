package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.text.ActionText;
import utils.champ.FieldInformations;

public abstract class ActionJson<T> extends ActionText<T> {

	protected ActionJson(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return null;
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return null;
	}

}
