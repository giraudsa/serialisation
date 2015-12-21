package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

public class ActionXmlDate<DateType extends Date> extends ActionXmlSimpleComportement<DateType>{
	
	public static ActionAbstrait<?> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlDate<Date>(Date.class, u);
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends DateType> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlDate<U>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	private ActionXmlDate(Class<DateType> type,  XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees){
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
}
