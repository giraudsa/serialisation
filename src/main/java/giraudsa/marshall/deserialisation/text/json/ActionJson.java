package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.text.ActionText;
import utils.champ.FieldInformations;

public abstract class ActionJson<T> extends ActionText<T> {

	protected ActionJson(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type,jsonUnmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return null;
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return null;
	}

}
