package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonDate<T extends Date> extends ActionJsonSimpleComportement<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonDate.class);

	@SuppressWarnings("unchecked")
	public static ActionAbstrait<Date> getInstance() {
		return new ActionJsonDate<>(Date.class, null);
	}

	private ActionJsonDate(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonDate<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Date.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		obj = objet;
	}

	@Override
	protected void rempliData(final String donnees) {
		Date date;
		long time = 0;
		try {
			date = getDateFormat().parse(donnees);
			time = date.getTime();
			obj = type.getConstructor(long.class).newInstance(time);
		} catch (ParseException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.error("pas de constructeur avec un long pour le type date " + type.getName(), e);
			obj = new Date(time);
		}
	}
}
