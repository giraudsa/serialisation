package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryLocale extends ActionBinarySimple<Locale> {

	public static ActionAbstrait<Locale> getInstance() {
		return new ActionBinaryLocale(Locale.class, null);
	}

	private ActionBinaryLocale(final Class<Locale> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Locale> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryLocale(Locale.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {

			final String locale = readUTF();
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
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
