package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.Locale;
import java.util.StringTokenizer;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlLocale extends ActionXmlSimpleComportement<String> {

	public static ActionAbstrait<String> getInstance() {
		return new ActionXmlLocale(String.class, null);
	}

	private ActionXmlLocale(final Class<String> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		final String locale = unescapeXml(sb.toString());
		final StringTokenizer tokenizer = new StringTokenizer(locale, "_");
		String language = null;
		String country = null;
		String variant = null;
		if (tokenizer.hasMoreElements())
			language = tokenizer.nextToken();
		if (tokenizer.hasMoreElements())
			country = tokenizer.nextToken();
		if (tokenizer.hasMoreElements())
			variant = tokenizer.nextToken();
		if (country == null && variant == null)
			obj = new Locale(language);
		else if (variant == null)
			obj = new Locale(language, country);
		else
			obj = new Locale(language, country, variant);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlLocale(String.class, (XmlUnmarshaller<?>) unmarshaller);
	}

}
