package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.net.URL;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryUrl extends ActionBinarySimple<URL> {

	public static ActionAbstrait<URL> getInstance() {
		return new ActionBinaryUrl(URL.class, null);
	}

	private ActionBinaryUrl(final Class<URL> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends URL> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryUrl(URL.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new URL(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
