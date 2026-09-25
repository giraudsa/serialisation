package io.github.giraudsa.fidelis.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.net.InetAddress;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;

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
