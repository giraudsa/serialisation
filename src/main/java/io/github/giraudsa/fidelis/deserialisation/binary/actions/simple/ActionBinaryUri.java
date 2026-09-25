package io.github.giraudsa.fidelis.deserialisation.binary.actions.simple;

import java.lang.System.Logger.Level;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.exception.UnmarshallExeption;

public class ActionBinaryUri extends ActionBinarySimple<URI> {
	private static final System.Logger LOGGER = System.getLogger(ActionBinaryUri.class.getName());

	public static ActionAbstrait<URI> getInstance() {
		return new ActionBinaryUri(URI.class, null);
	}

	private ActionBinaryUri(final Class<URI> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends URI> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryUri(URI.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, UnmarshallExeption {
		if (isDejaVu())
			obj = getObjet();
		else {
			try {
				obj = new URI(readUTF());
			} catch (final URISyntaxException e) {
				LOGGER.log(Level.ERROR, e.getMessage(), e);
				throw new UnmarshallExeption("bad Uri Syntax", e);
			}
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
