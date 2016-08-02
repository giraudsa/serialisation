package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;

public abstract class ActionBinarySimple<T> extends ActionBinary<T>{

	protected ActionBinarySimple(Class<T> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
	
	@Override
	protected void deserialisePariellement() throws IllegalAccessException, EntityManagerImplementationException, InstanciationException{
		exporteObject();
	}
	
	
	@Override
	protected void integreObjet(String nom, Object obj) {
		//non applicable sur un type simple
	}

}
