package io.github.giraudsa.fidelis.deserialisation.binary.actions.simple;

import io.github.giraudsa.fidelis.deserialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.exception.EntityManagerImplementationException;
import io.github.giraudsa.fidelis.exception.InstanciationException;
import io.github.giraudsa.fidelis.exception.SetValueException;

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
