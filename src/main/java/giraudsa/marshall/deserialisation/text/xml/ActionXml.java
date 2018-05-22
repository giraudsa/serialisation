package giraudsa.marshall.deserialisation.text.xml;

import giraudsa.marshall.deserialisation.text.ActionText;
import utils.champ.FieldInformations;

public abstract class ActionXml<T> extends ActionText<T> {

	protected ActionXml(final Class<T> type, final XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
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
