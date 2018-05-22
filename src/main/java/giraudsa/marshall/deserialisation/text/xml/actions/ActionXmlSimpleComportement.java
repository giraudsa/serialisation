package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlEscapeUtil;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionXmlSimpleComportement<>(Object.class, null);
	}

	protected static String unescapeXml(final String text) {
		return XmlEscapeUtil.unescape(text);
	}

	protected StringBuilder sb = new StringBuilder();

	protected ActionXmlSimpleComportement(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() throws InstanciationException {
		try {
			obj = type.getConstructor(String.class).newInstance(unescapeXml(sb.toString()));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InstanciationException("impossible d'instancier un objet de type " + type + " avec la valeur "
					+ System.lineSeparator() + unescapeXml(sb.toString()), e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlSimpleComportement<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// rien a faire
	}

	@Override
	protected void rempliData(final String donnees) {
		sb.append(donnees);
	}
}
