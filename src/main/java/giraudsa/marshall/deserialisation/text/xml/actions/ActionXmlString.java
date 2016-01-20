package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlString extends ActionXmlSimpleComportement<String>{

	private ActionXmlString(Class<String> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<String> getInstance() {	
		return new ActionXmlString(String.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlString(String.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected void construitObjet() {
		obj = unescapeXml(sb.toString());
	}

}
