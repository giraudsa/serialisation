package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlEscapeUtil;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.lang.reflect.InvocationTargetException;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {
	protected StringBuilder sb = new StringBuilder();
	protected ActionXmlSimpleComportement(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
			return (ActionAbstrait<U>) new ActionXmlSimpleComportement<>(Object.class, null);
		}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlSimpleComportement<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees){
		sb.append(donnees);
	}

	@Override
	protected void construitObjet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption, InstantiationException, IllegalArgumentException, SecurityException {
		obj = type.getConstructor(String.class).newInstance(unescapeXml(sb.toString()));
	}

	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien a faire
	}
	
	protected static String unescapeXml(final String text) {
        return XmlEscapeUtil.unescape(text);
    }
}
