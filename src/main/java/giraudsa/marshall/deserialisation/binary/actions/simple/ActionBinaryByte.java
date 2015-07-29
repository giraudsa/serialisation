package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinaryByte extends ActionBinary<Byte> {

	public ActionBinaryByte(Class<? extends Byte> type, Unmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected Byte readObject(Class<? extends Byte> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		return readByte();
	}

}
