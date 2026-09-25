package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.util.Locale;
import java.util.StringTokenizer;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonLocale extends ActionJsonSimpleComportement<Locale> {

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionJsonLocale(Locale.class, null);
	}

	private ActionJsonLocale(final Class<Locale> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Locale> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonLocale(Locale.class, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void rempliData(final String donnees) {
		final StringTokenizer tokenizer = new StringTokenizer(donnees, "_");
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

}
