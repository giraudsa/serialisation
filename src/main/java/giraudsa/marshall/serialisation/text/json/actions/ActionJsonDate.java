package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller.SetQueue;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ActionJsonDate<DateGeneric> extends ActionJsonSimpleComportement<DateGeneric> {
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static TimeZone  tz = TimeZone.getTimeZone("UTC");
	static{
		df.setTimeZone(tz);
	}
	@Override
	protected Class<?> getType() {
		return Date.class;
	}
	
	public ActionJsonDate(Class<DateGeneric> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM, nomClef);
	}

	@Override
	protected void ecritValeur(DateGeneric obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeWithQuote(df.format(obj));
	}
}
