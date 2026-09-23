package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionXmlSimpleComportement<>(Object.class, null);
	}

	/**
	 * Le parseur SAX a déjà décodé les entités et références : un second
	 * décodage corromprait un texte contenant par exemple "&amp;lt;".
	 */
	protected static String unescapeXml(final String text) {
		return text;
	}

	protected StringBuilder sb = new StringBuilder();

	protected ActionXmlSimpleComportement(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void construitObjet() throws InstanciationException {
		if (type == Character.class) { // pas de constructeur Character(String)
			if (sb.length() != 1)
				throw new InstanciationException("un caractère est attendu au lieu de \"" + sb + "\"");
			obj = (T) Character.valueOf(sb.charAt(0));
			return;
		}
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
