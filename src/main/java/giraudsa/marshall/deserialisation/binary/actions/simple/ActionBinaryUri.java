package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionBinaryUri extends ActionBinarySimple<URI> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryUri.class);

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
				LOGGER.error(e.getMessage(), e);
				throw new UnmarshallExeption("bad Uri Syntax", e);
			}
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
