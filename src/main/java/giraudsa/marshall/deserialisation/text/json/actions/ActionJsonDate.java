package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionJsonDate<T extends Date> extends ActionJsonSimpleComportement<T>{
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonDate.class);
	private ActionJsonDate(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	public static ActionAbstrait<Date> getInstance(){
		return new ActionJsonDate<>(Date.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonDate<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) {
		Date date;
		long time = 0;
		try {
			date = getDateFormat().parse(donnees);
			time = date.getTime();
			obj = type.getConstructor(long.class).newInstance(time);
		} catch (ParseException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.error("pas de constructeur avec un long pour le type date " + type.getName(), e);
			obj = new Date(time);
		} 
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return Date.class;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = objet;
	}
}
