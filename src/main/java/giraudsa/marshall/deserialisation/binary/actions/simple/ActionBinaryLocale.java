package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;


public class ActionBinaryLocale extends ActionBinarySimple<Locale> {

	private ActionBinaryLocale(Class<Locale> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Locale> getInstance(){
		return new ActionBinaryLocale(Locale.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Locale> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryLocale(Locale.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		if(isDejaVu())
			obj = getObjet();
		else{

			String locale = readUTF();
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
			stockeObjetId();
		}
	}
}
