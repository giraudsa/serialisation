package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonVoid extends ActionJsonSimpleWithoutQuote<Void> {

	public ActionJsonVoid(JsonMarshaller b) {
		super(b);
	}
	
	@Override protected void ecritValeur(Void obj, TypeRelation relation, boolean ecrisSeparateur)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write("null");
	}
}
