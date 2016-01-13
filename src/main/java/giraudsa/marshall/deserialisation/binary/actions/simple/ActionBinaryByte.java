package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

import java.io.IOException;

public class ActionBinaryByte extends ActionBinarySimple<Byte> {
	
	private ActionBinaryByte(Class<Byte> type,  BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Byte> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryByte(Byte.class, bu);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Byte> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryByte(Byte.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException {
		obj = readByte();
	}

}
