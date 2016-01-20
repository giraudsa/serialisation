package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryShort extends ActionBinarySimple<Short> {

	private ActionBinaryShort(Class<Short> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Short> getInstance(){
		return new ActionBinaryShort(Short.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Short> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryShort(Short.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		obj = readShort();
	}
}
