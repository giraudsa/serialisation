package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.net.InetAddress;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryInetAddress<T extends InetAddress> extends ActionBinarySimple<T> {

	public static ActionAbstrait<InetAddress> getInstance() {
		return new ActionBinaryInetAddress<>(InetAddress.class, null);
	}

	private ActionBinaryInetAddress(final Class<T> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryInetAddress<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = InetAddress.getByName(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
