package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringEscapeUtils;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public static <U> ActionAbstrait<U> getInstance(Class<U> type, XmlUnmarshaller<?> u) {	
			return new ActionXmlSimpleComportement<>(type, u);
		}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlSimpleComportement<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	protected ActionXmlSimpleComportement(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}
	

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = type.getConstructor(String.class).newInstance(StringEscapeUtils.unescapeXml(donnees));
	}

	@Override
	protected void construitObjet() {
		//rien a faire
	}

	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien a faire
	}
}
