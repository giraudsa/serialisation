package com.actemium.marshall.serialisation.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.json.JsonMarshaller;

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
	
	public ActionJsonDate(Class<DateGeneric> type, String nomClef) {
		super(type, nomClef);
	}

	@Override
	protected void ecritValeur(DateGeneric obj, TypeRelation relation, Marshaller jsonM, SetQueue<Object> aSerialiser) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeWithQuote((JsonMarshaller)jsonM, df.format(obj));
	}
}
