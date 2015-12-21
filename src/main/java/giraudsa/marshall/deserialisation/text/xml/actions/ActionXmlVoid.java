package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlVoid extends ActionXmlSimpleComportement<Void> {

	public static ActionAbstrait<Void> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlVoid(Void.class, u);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlVoid(Void.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	private ActionXmlVoid(Class<Void> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}
	
	@Override
	protected Void getObjetDejaVu() {
		return null;
	}
}
