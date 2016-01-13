package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryInteger extends ActionBinarySimple<Integer> {

	private ActionBinaryInteger(Class<Integer> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Integer> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryInteger(Integer.class, bu);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Integer> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryInteger(Integer.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException {
		obj = readInt();
	}
}
