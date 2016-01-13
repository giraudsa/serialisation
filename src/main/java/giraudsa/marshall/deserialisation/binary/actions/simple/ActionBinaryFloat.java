package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryFloat extends ActionBinarySimple<Float> {

	private ActionBinaryFloat(Class<Float> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Float> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryFloat(Float.class, bu);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Float> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryFloat(Float.class, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException {
		obj = readFloat();
	}
}
