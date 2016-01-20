package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.Locale;
import java.util.StringTokenizer;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlLocale extends ActionXmlSimpleComportement<String>{

	private ActionXmlLocale(Class<String> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<String> getInstance() {	
		return new ActionXmlLocale(String.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlLocale(String.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected void construitObjet() {
		String locale = unescapeXml(sb.toString());
		StringTokenizer tokenizer = new StringTokenizer(locale, "_");
		String language = null;
		String country = null;
		String variant = null;
		if (tokenizer.hasMoreElements()) {
			language = tokenizer.nextToken();
		}
		if (tokenizer.hasMoreElements()) {
			country = tokenizer.nextToken();
		}
		if (tokenizer.hasMoreElements()) {
			variant = tokenizer.nextToken();
		}
		if (country == null && variant == null) {
			obj = new Locale(language);
		} else if (variant == null) {
			obj = new Locale(language, country);
		} else {
			obj = new Locale(language, country, variant);
		}
	}

}
