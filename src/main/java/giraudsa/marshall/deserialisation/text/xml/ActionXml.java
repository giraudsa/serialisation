package giraudsa.marshall.deserialisation.text.xml;

import giraudsa.marshall.deserialisation.text.ActionText;

public abstract class ActionXml<T> extends ActionText<T> {
	
	protected ActionXml(Class<T> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

}
