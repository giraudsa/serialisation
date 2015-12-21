package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class ActionJsonDate extends ActionJsonSimpleWithQuote<Date> {

	public ActionJsonDate(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override
	protected void ecritValeur(Date obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeWithQuote(getDateFormat().format(obj));
	}
}
