package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public abstract class ActionBinarySimple<T> extends ActionBinary<T>{

	protected ActionBinarySimple(Class<T> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
	
	public void deserialisePariellement() throws IllegalArgumentException, IllegalAccessException, InstantiationException{
		exporteObject();
	}
	
	//non applicable sur un type simple
	@Override
	public void integreObject(Object obj) {}

}
