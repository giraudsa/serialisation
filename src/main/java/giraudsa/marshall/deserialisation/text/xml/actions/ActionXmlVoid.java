package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlVoid<T> extends ActionXmlSimpleComportement<T> {

	public ActionXmlVoid(Class<T> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, nom, xmlUnmarshaller);
	}
	
	@Override
	protected T getObjet() {
		return null;
	}
}
