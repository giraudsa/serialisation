package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryShort extends ActionBinarySimple<Short> {

	public static ActionAbstrait<Short> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryShort(Short.class, bu);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Short> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryShort(Short.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	private ActionBinaryShort(Class<Short> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void initialise() throws IOException{
		obj = readShort();
	}
}
