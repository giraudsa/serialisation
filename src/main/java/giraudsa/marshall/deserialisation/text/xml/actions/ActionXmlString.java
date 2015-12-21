package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;

public class ActionXmlString extends ActionXmlSimpleComportement<String>{

	StringBuilder sb = new StringBuilder();
	
	public static ActionAbstrait<String> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlString(String.class, u);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlString(String.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	private ActionXmlString(Class<String> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}
	
	@Override protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		sb.append(donnees);
	}
	
	@Override protected void construitObjet() {
		obj = sb.toString();
	}

}
