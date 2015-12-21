package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryDouble extends ActionBinarySimple<Double> {
	public static ActionAbstrait<Double> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryDouble(Double.class, bu);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Double> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryDouble(Double.class, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	private ActionBinaryDouble(Class<Double> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void initialise() throws IOException{
		obj = readDouble();
	}

}
