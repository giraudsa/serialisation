package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlEscapeUtil;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	protected ActionXmlSimpleComportement(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static <U> ActionAbstrait<U> getInstance(Class<U> type, XmlUnmarshaller<?> u) {	
			return new ActionXmlSimpleComportement<>(type, u);
		}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlSimpleComportement<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		obj = type.getConstructor(String.class).newInstance(unescapeXml(donnees));
	}

	@Override
	protected void construitObjet() {
		//rien a faire
	}

	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien a faire
	}
	
	private String unescapeXml(final String text) {
        // The chosen symbols (1.0 or 1.1) don't really matter, as both contain the same CERs
        return XmlEscapeUtil.unescape(text);
    }
}
