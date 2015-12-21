package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

public class ActionJsonDate<DateType extends Date> extends ActionJsonSimpleComportement<DateType>{
	
	public static ActionAbstrait<Date> getInstance(JsonUnmarshaller<?> jsonUnmarshaller){
		return new ActionJsonDate<>(Date.class, jsonUnmarshaller);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends DateType> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonDate<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	private ActionJsonDate(Class<DateType> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException {
		Date date;
		try {
			date = getDateFormat().parse(donnees);
			long time = date.getTime();
			obj = type.getConstructor(long.class).newInstance(time);
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
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
