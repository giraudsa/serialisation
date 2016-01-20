package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Locale;
import java.util.StringTokenizer;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonLocale extends ActionJsonSimpleComportement<Locale> {

	private ActionJsonLocale(Class<Locale> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonLocale(Locale.class, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Locale> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(Locale.class, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringTokenizer tokenizer = new StringTokenizer(donnees, "_");
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
