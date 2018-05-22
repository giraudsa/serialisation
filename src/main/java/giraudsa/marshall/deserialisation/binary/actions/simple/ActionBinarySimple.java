package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;

public abstract class ActionBinarySimple<T> extends ActionBinary<T> {

	protected ActionBinarySimple(final Class<T> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void deserialisePariellement() throws IllegalAccessException, EntityManagerImplementationException,
			InstanciationException, SetValueException {
		exporteObject();
	}

	@Override
	protected void integreObjet(final String nom, final Object obj) {
		// non applicable sur un type simple
	}

}
