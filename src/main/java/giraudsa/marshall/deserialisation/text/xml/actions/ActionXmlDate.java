package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlDate<T extends Date> extends ActionXmlSimpleComportement<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlDate.class);

	public static ActionAbstrait<Date> getInstance() {
		return new ActionXmlDate<>(Date.class, null);
	}

	private ActionXmlDate(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		Date date;
		long time = 0;
		try {
			date = getDateFormat().parse(sb.toString());
			time = date.getTime();
			obj = type.getConstructor(long.class).newInstance(time);
		} catch (ParseException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			obj = new Date(time);
			LOGGER.error("pas de constructeur avec un long pour le type date " + type.getName(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlDate<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}
}
