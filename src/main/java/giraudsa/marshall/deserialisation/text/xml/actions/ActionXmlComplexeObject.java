package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public abstract class ActionXmlComplexeObject<T> extends ActionXml<T> {

	protected ActionXmlComplexeObject(final Class<T> type, final XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void rempliData(final String donnees) {
		// Rien a faire
	}

}
