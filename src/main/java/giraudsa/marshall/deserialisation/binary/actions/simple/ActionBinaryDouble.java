package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinaryDouble extends ActionBinary<Double> {

	public ActionBinaryDouble(Class<? extends Double> type, Unmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected Double readObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			ClassNotFoundException, IOException, NotImplementedSerializeException {
		return readDouble();
	}

}
