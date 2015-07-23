package giraudsa.marshall.serialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller.SetQueue;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionJsonVoid<T> extends ActionJson<T> {

	public ActionJsonVoid(Class<T> type, JsonMarshaller jsonM, String nomClef) {
		super(type, jsonM, nomClef);
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write("null");
	}

}
