package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;

public class ActionJsonDate<DateGeneric> extends ActionJsonSimpleComportement<DateGeneric> {
	@Override
	protected Class<?> getType() {
		return Date.class;
	}
	
	public ActionJsonDate(Class<DateGeneric> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM, nomClef);
	}

	@Override
	protected void ecritValeur(DateGeneric obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeWithQuote(getDateFormat().format(obj));
	}
}
