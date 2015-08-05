package giraudsa.marshall.deserialisation.text.xml;

import giraudsa.marshall.deserialisation.text.ActionText;

public abstract class ActionXml<T> extends ActionText<T> {
	
	public ActionXml(Class<T> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, nom, xmlUnmarshaller);
	}

}
