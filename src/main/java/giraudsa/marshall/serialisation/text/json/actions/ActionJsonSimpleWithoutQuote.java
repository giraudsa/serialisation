package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

public class ActionJsonSimpleWithoutQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithoutQuote(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(T obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(StringEscapeUtils.escapeJson(obj.toString()));
	}
}
