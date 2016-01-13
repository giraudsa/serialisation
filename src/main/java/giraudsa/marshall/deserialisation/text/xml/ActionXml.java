package giraudsa.marshall.deserialisation.text.xml;

import giraudsa.marshall.deserialisation.text.ActionText;
import utils.champ.FieldInformations;

public abstract class ActionXml<T> extends ActionText<T> {
	
	protected ActionXml(Class<T> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
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
